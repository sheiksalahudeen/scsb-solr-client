package org.recap.executors;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.recap.model.Item;
import org.recap.repository.temp.ItemCrudRepositoryMultiCoreSupport;
import org.recap.util.ItemJSONUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by angelind on 15/6/16.
 */
public class ItemIndexCallable implements Callable {

    private int pageNum;
    private int docsPerPage;
    private String coreName;
    private String solrURL;

    private ItemCrudRepositoryMultiCoreSupport itemCrudRepositoryMultiCoreSupport;

    public ItemIndexCallable(String solrURL, String coreName, int pageNum, int docsPerPage) {
        this.coreName = coreName;
        this.solrURL = solrURL;
        this.pageNum = pageNum;
        this.docsPerPage = docsPerPage;
    }

    @Override
    public Object call() throws Exception {

//        RestTemplate restTemplate = new RestTemplate();
//
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//
//        ResponseEntity<String> response =
//                restTemplate.getForEntity(itemResourceUrl + "/findByRangeOfIds?fromId=" + from + "&toId=" + to, String.class);
//        stopWatch.stop();
//        System.out.println("Time taken to get items and related data: " + stopWatch.getTotalTimeSeconds());
//
//        JSONArray jsonArray = new JSONArray(response.getBody());
//
//        List<Item> itemsToIndex = new ArrayList<>();
//
//        for(int i=0; i < jsonArray.length(); i++) {
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//            Item item = ItemJSONUtil.getInstance().generateItemForIndex(jsonObject);
//            itemsToIndex.add(item);
//        }
//
//        stopWatch.start();
//        itemCrudRepositoryMultiCoreSupport = new ItemCrudRepositoryMultiCoreSupport(coreName, solrURL);
//        if(!CollectionUtils.isEmpty(itemsToIndex)) {
//            itemCrudRepositoryMultiCoreSupport.save(itemsToIndex);
//        }
//        stopWatch.stop();
//        System.out.println("Time taken to index temp core: " + stopWatch.getTotalTimeSeconds());
        return null;
    }
}
