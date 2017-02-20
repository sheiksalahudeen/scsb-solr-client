package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.solr.Item;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by angelind on 15/6/16.
 */
public class ItemIndexCallable implements Callable {

    Logger logger = LoggerFactory.getLogger(ItemIndexCallable.class);

    private int pageNum;
    private int docsPerPage;
    private String coreName;
    private String solrURL;
    private Integer owningInstitutionId;
    private ItemDetailsRepository itemDetailsRepository;
    private ProducerTemplate producerTemplate;

    public ItemIndexCallable(String solrURL, String coreName, int pageNum, int docsPerPage, ItemDetailsRepository itemDetailsRepository, Integer owningInstitutionId, ProducerTemplate producerTemplate) {
        this.coreName = coreName;
        this.solrURL = solrURL;
        this.pageNum = pageNum;
        this.docsPerPage = docsPerPage;
        this.itemDetailsRepository = itemDetailsRepository;
        this.owningInstitutionId = owningInstitutionId;
        this.producerTemplate = producerTemplate;
    }

    @Override
    public Object call() throws Exception {
        Page<ItemEntity> itemEntities = owningInstitutionId == null ?
                itemDetailsRepository.findAllByIsDeletedFalse(new PageRequest(pageNum, docsPerPage)) :
                itemDetailsRepository.findByOwningInstitutionIdAndIsDeletedFalse(new PageRequest(pageNum, docsPerPage), owningInstitutionId);

        logger.info("Num Items Fetched : " + itemEntities.getNumberOfElements());
        List<Item> itemsToIndex = new ArrayList<>();

        Iterator<ItemEntity> iterator = itemEntities.iterator();

        ExecutorService executorService = Executors.newFixedThreadPool(50);
        List<Future> futures = new ArrayList<>();
        while (iterator.hasNext()) {
            ItemEntity itemEntity = iterator.next();
            Future submit = executorService.submit(new ItemRecordSetupCallable(itemEntity, producerTemplate));
            futures.add(submit);
        }

        for (Iterator<Future> futureIterator = futures.iterator(); futureIterator.hasNext(); ) {
            try {
                Future future = futureIterator.next();
                Item item = (Item) future.get();
                if(item != null)
                    itemsToIndex.add(item);
            } catch (Exception e) {
                logger.error(RecapConstants.LOG_ERROR,e);
            }
        }

        executorService.shutdown();

        logger.info("No of Items to index : {}",itemsToIndex.size());

        if (!CollectionUtils.isEmpty(itemsToIndex)) {
            producerTemplate.sendBodyAndHeader(RecapConstants.SOLR_QUEUE, itemsToIndex, RecapConstants.SOLR_CORE, coreName);
        }
        return itemsToIndex.size();
    }
}
