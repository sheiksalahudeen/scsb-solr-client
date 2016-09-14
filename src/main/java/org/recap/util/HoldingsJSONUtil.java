package org.recap.util;

import org.marc4j.marc.Record;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.solr.Holdings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by rajeshbabuk on 13/9/16.
 */
public class HoldingsJSONUtil extends MarcUtil {

    Logger logger = LoggerFactory.getLogger(HoldingsJSONUtil.class);

    public Holdings generateHoldingsForIndex(HoldingsEntity holdingsEntity) {
        Holdings holdings = new Holdings();
        try {
            holdings.setHoldingsId(holdingsEntity.getHoldingsId());
            holdings.setDocType("Holdings");
            String holdingsContent = new String(holdingsEntity.getContent());
            List<Record> records = convertMarcXmlToRecord(holdingsContent);
            Record marcRecord = records.get(0);
            holdings.setSummaryHoldings(getDataFieldValue(marcRecord, "866", null, null, "a"));
            InstitutionEntity institutionEntity = holdingsEntity.getInstitutionEntity();
            String institutionCode = null != institutionEntity ? institutionEntity.getInstitutionCode() : "";
            holdings.setOwningInstitution(institutionCode);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return holdings;
    }
}
