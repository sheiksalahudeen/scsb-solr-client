package org.recap.executors;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.recap.model.Bib;
import org.recap.model.Item;
import org.recap.repository.temp.BibCrudRepositoryMultiCoreSupport;
import org.recap.repository.temp.ItemCrudRepositoryMultiCoreSupport;
import org.recap.util.BibJSONUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by pvsubrah on 6/13/16.
 */


public class BibIndexCallable implements Callable {
    private final String bibResourceUrl;
    private final int from;
    private final int to;
    private String coreName;
    private String solrURL;

    private BibCrudRepositoryMultiCoreSupport bibCrudRepositoryMultiCoreSupport;

    private ItemCrudRepositoryMultiCoreSupport itemCrudRepositoryMultiCoreSupport;

    public BibIndexCallable(String solrURL, String bibResourceUrl, String coreName, int from, int to) {
        this.coreName = coreName;
        this.solrURL = solrURL;
        this.bibResourceUrl = bibResourceUrl;
        this.from = from;
        this.to = to;
    }

    @Override
    public Object call() throws Exception {

        RestTemplate restTemplate = new RestTemplate();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ResponseEntity<String> response =
                restTemplate.getForEntity(bibResourceUrl + "/findByRangeOfIds?fromId=" + from + "&toId=" + to, String.class);

        stopWatch.stop();
        System.out.println("Time taken to get bibs and related data: " + stopWatch.getTotalTimeSeconds());

        JSONArray jsonArray = new JSONArray(response.getBody());

        List<Bib> bibsToIndex = new ArrayList<Bib>();
        List<Item> itemsToIndex = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            Map<String, List> map = BibJSONUtil.getInstance().generateBibAndItemsForIndex(jsonObject);
            Bib bib = (Bib) map.get("Bib");
            bibsToIndex.add(bib);

            List<Item> items = map.get("Item");
            itemsToIndex.addAll(items);
        }

        stopWatch.start();
        bibCrudRepositoryMultiCoreSupport = new BibCrudRepositoryMultiCoreSupport(coreName, solrURL);
        if (!CollectionUtils.isEmpty(bibsToIndex)) {
            bibCrudRepositoryMultiCoreSupport.save(bibsToIndex);
        }
        itemCrudRepositoryMultiCoreSupport = new ItemCrudRepositoryMultiCoreSupport(coreName, solrURL);
        if(!CollectionUtils.isEmpty(itemsToIndex)) {
            itemCrudRepositoryMultiCoreSupport.save(itemsToIndex);
        }
        stopWatch.stop();
        System.out.println("Time taken to index temp core: " + stopWatch.getTotalTimeSeconds());
        return null;
    }
}
