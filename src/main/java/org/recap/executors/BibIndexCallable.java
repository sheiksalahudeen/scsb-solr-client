package org.recap.executors;

import org.recap.model.Bib;
import org.recap.model.BibliographicEntity;
import org.recap.model.Item;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.temp.BibCrudRepositoryMultiCoreSupport;
import org.recap.repository.temp.ItemCrudRepositoryMultiCoreSupport;
import org.recap.util.BibJSONUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

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
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    private BibCrudRepositoryMultiCoreSupport bibCrudRepositoryMultiCoreSupport;

    private ItemCrudRepositoryMultiCoreSupport itemCrudRepositoryMultiCoreSupport;

    public BibIndexCallable(String solrURL, String bibResourceUrl, String coreName, int from, int to, BibliographicDetailsRepository bibliographicDetailsRepository) {
        this.coreName = coreName;
        this.solrURL = solrURL;
        this.bibResourceUrl = bibResourceUrl;
        this.from = from;
        this.to = to;
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
    }

    @Override
    public Object call() throws Exception {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Page<BibliographicEntity> bibliographicEntities = bibliographicDetailsRepository.findAll(new PageRequest(from, to));
        stopWatch.stop();
        System.out.println("Time taken to get bibs and related data: " + stopWatch.getTotalTimeSeconds());

        List<Bib> bibsToIndex = new ArrayList<Bib>();
        List<Item> itemsToIndex = new ArrayList<>();

        for (BibliographicEntity bibliographicEntity : bibliographicEntities){
            Map<String, List> map = BibJSONUtil.getInstance().generateBibAndItemsForIndex(bibliographicEntity);
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
