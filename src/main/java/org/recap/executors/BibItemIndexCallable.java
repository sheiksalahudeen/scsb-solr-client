package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.solr.Bib;
import org.recap.model.solr.Item;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by chenchulakshmig on 21/6/16.
 */
public class BibItemIndexCallable implements Callable {

    Logger logger = LoggerFactory.getLogger(BibItemIndexCallable.class);

    private final int pageNum;
    private final int docsPerPage;
    private String coreName;
    private String solrURL;
    private Integer owningInstitutionId;
    private BibliographicDetailsRepository bibliographicDetailsRepository;
    private ProducerTemplate producerTemplate;

    public BibItemIndexCallable(String solrURL, String coreName, int pageNum, int docsPerPage, BibliographicDetailsRepository bibliographicDetailsRepository, Integer owningInstitutionId, ProducerTemplate producerTemplate) {
        this.coreName = coreName;
        this.solrURL = solrURL;
        this.pageNum = pageNum;
        this.docsPerPage = docsPerPage;
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
        this.owningInstitutionId = owningInstitutionId;
        this.producerTemplate = producerTemplate;
    }

    @Override
    public Object call() throws Exception {

        Page<BibliographicEntity> bibliographicEntities = owningInstitutionId == null ?
                bibliographicDetailsRepository.findAll(new PageRequest(pageNum, docsPerPage)) :
                bibliographicDetailsRepository.findByOwningInstitutionId(new PageRequest(pageNum, docsPerPage), owningInstitutionId);

        logger.info("Num Bibs Fetched : " + bibliographicEntities.getNumberOfElements());
        List<Bib> bibsToIndex = new ArrayList<>();
        List<Bib> holdingsToIndex = new ArrayList<>();
        List<Item> itemsToIndex = new ArrayList<>();

        Iterator<BibliographicEntity> iterator = bibliographicEntities.iterator();


        ExecutorService executorService = Executors.newFixedThreadPool(50);
        List<Future> futures = new ArrayList<>();
        while(iterator.hasNext()){
            BibliographicEntity bibliographicEntity = iterator.next();
            Future submit = executorService.submit(new BibItemRecordSetupCallable(bibliographicEntity));
            futures.add(submit);
        }

        logger.info("Num futures to prepare Bib and Associated data : " + futures.size());

        try {
            for (Iterator<Future> futureIterator = futures.iterator(); futureIterator.hasNext(); ) {
                Future future = futureIterator.next();

                Map<String, List> stringListMap = (Map<String, List>) future.get();
                List bibs = stringListMap.get("Bib");
                bibsToIndex.addAll(bibs);
                List holdings = stringListMap.get("Holdings");
                holdingsToIndex.addAll(holdings);
                List items = stringListMap.get("Item");
                itemsToIndex.addAll(items);
            }
        } catch (Exception e) {
            logger.error("Exception  : " +e.getMessage());
        }

        logger.info("No of Bibs to index : " + bibsToIndex.size());
        logger.info("No of Holdings to index : " + holdingsToIndex.size());
        logger.info("No of Items to index : " + itemsToIndex.size());

        executorService.shutdown();

        if (!CollectionUtils.isEmpty(bibsToIndex)) {
            producerTemplate.sendBody("seda:solrQ", bibsToIndex);
        }
        if (!CollectionUtils.isEmpty(holdingsToIndex)) {
            producerTemplate.sendBody("seda:solrQ", holdingsToIndex);
        }
        if (!CollectionUtils.isEmpty(itemsToIndex)) {
            producerTemplate.sendBody("seda:solrQ", itemsToIndex);
        }
        return bibliographicEntities.getNumberOfElements();
    }
}
