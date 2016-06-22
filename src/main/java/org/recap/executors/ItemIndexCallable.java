package org.recap.executors;

import org.recap.model.jpa.ItemEntity;
import org.recap.model.solr.Item;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.solr.temp.ItemCrudRepositoryMultiCoreSupport;
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

    private int pageNum;
    private int docsPerPage;
    private String coreName;
    private String solrURL;
    private Integer owningInstitutionId;
    private ItemDetailsRepository itemDetailsRepository;

    private ItemCrudRepositoryMultiCoreSupport itemCrudRepositoryMultiCoreSupport;

    public ItemIndexCallable(String solrURL, String coreName, int pageNum, int docsPerPage, ItemDetailsRepository itemDetailsRepository, Integer owningInstitutionId) {
        this.coreName = coreName;
        this.solrURL = solrURL;
        this.pageNum = pageNum;
        this.docsPerPage = docsPerPage;
        this.itemDetailsRepository = itemDetailsRepository;
        this.owningInstitutionId = owningInstitutionId;
    }

    @Override
    public Object call() throws Exception {
        Page<ItemEntity> itemEntities = owningInstitutionId == null ?
                itemDetailsRepository.findAll(new PageRequest(pageNum, docsPerPage)) :
                itemDetailsRepository.findByOwningInstitutionId(new PageRequest(pageNum, docsPerPage), owningInstitutionId);

        List<Item> itemsToIndex = new ArrayList<>();

        Iterator<ItemEntity> iterator = itemEntities.iterator();

        ExecutorService executorService = Executors.newFixedThreadPool(50);
        List<Future> futures = new ArrayList<>();
        while (iterator.hasNext()) {
            ItemEntity itemEntity = iterator.next();
            Future submit = executorService.submit(new ItemRecordSetupCallable(itemEntity));
            futures.add(submit);
        }

        for (Iterator<Future> futureIterator = futures.iterator(); futureIterator.hasNext(); ) {
            Future future = futureIterator.next();

            Item item = (Item) future.get();
            itemsToIndex.add(item);
        }

        executorService.shutdown();

        itemCrudRepositoryMultiCoreSupport = new ItemCrudRepositoryMultiCoreSupport(coreName, solrURL);
        if (!CollectionUtils.isEmpty(itemsToIndex)) {
            itemCrudRepositoryMultiCoreSupport.save(itemsToIndex);
        }
        return itemEntities.getSize();
    }
}
