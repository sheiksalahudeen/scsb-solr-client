package org.recap.util;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.marc4j.marc.Record;
import org.recap.model.Bib;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pvsubrah on 6/15/16.
 */
public class BibJSONUtil {

    private static BibJSONUtil bibJSONUtil;


    private BibJSONUtil() {
    }

    public static BibJSONUtil getInstance(){
        if(bibJSONUtil == null){
            bibJSONUtil = new BibJSONUtil();
        }

        return bibJSONUtil;
    }

    public Bib generateBibForIndex(JSONObject jsonObject) {
        Bib bib = new Bib();
        MarcUtil marcUtil = new MarcUtil();
        try {
            String bibliographicId = jsonObject.getString("bibliographicId");
            bib.setBibId(bibliographicId);
            bib.setId(bibliographicId);
            String bibContent = jsonObject.getString("content");
            List<Record> records = marcUtil.convertMarcXmlToRecord(bibContent);
            Record marcRecord = records.get(0);
            bib.setAuthor(marcUtil.getDataFieldValue(marcRecord, "100", null, null, "a"));
            bib.setTitle(marcUtil.getDataFieldValue(marcRecord, "245", null, null, "a"));
            bib.setIssn(marcUtil.getMultiDataFieldValues(marcRecord, "010", null, null, "a"));

            JSONArray holdingsEntities = jsonObject.getJSONArray("holdingsEntities");
            List<String> holdingsIds = new ArrayList<>();
            for(int j=0;j<holdingsEntities.length();j++){
                JSONObject holdings = holdingsEntities.getJSONObject(j);
                String holdingsId = holdings.getString("holdingsId");
                holdingsIds.add(holdingsId);
            }

            bib.setHoldingsIdList(holdingsIds);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bib;
    }
}
