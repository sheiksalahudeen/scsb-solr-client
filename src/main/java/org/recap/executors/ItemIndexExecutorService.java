package org.recap.executors;

import org.recap.repository.jpa.ItemDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

/**
 * Created by angelind on 16/6/16.
 */
@Service
public class ItemIndexExecutorService extends IndexExecutorService {

    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    @Override
    public Callable getCallable(String coreName, int pageNum, int docsPerPage, Integer owningInstitutionId) {
        return new ItemIndexCallable(solrUrl, coreName, pageNum, docsPerPage, itemDetailsRepository, owningInstitutionId);
    }

    @Override
    protected Integer getTotalDocCount(Integer owningInstitutionId) {
        Long count = owningInstitutionId == null ? itemDetailsRepository.count() : itemDetailsRepository.countByOwningInstitutionId(owningInstitutionId);
        return count.intValue();
    }

    @Override
    protected String getResourceURL() {
        return itemResourceURL;
    }
}
