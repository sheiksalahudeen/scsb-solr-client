package org.recap.executors;

import org.recap.repository.jpa.ItemDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Created by angelind on 16/6/16.
 */
@Service
public class ItemIndexExecutorService extends IndexExecutorService {

    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    @Override
    public Callable getCallable(String coreName, int pageNum, int docsPerPage, Integer owningInstitutionId, Date fromDate) {
        return new ItemIndexCallable(coreName, pageNum, docsPerPage, itemDetailsRepository, owningInstitutionId, producerTemplate);
    }

    @Override
    protected Integer getTotalDocCount(Integer owningInstitutionId, Date fromDate) {
        Long count = owningInstitutionId == null ? itemDetailsRepository.countByIsDeletedFalse() : itemDetailsRepository.countByOwningInstitutionIdAndIsDeletedFalse(owningInstitutionId);
        return count.intValue();
    }

    public void setItemDetailsRepository(ItemDetailsRepository itemDetailsRepository) {
        this.itemDetailsRepository = itemDetailsRepository;
    }
}