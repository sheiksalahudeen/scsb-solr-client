package org.recap.executors;

import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Created by rajeshbabuk on 13/9/16.
 */
@Service
public class HoldingsIndexExecutorService extends IndexExecutorService {

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Override
    public Callable getCallable(String coreName, int pageNum, int docsPerPage, Integer owningInstitutionId, Date fromDate) {
        return new HoldingsIndexCallable(pageNum, coreName, docsPerPage, holdingsDetailsRepository, owningInstitutionId, producerTemplate);
    }

    @Override
    protected Integer getTotalDocCount(Integer owningInstitutionId, Date fromDate) {
        Long count = owningInstitutionId == null ? holdingsDetailsRepository.countByIsDeletedFalse() : holdingsDetailsRepository.countByOwningInstitutionIdAndIsDeletedFalse(owningInstitutionId);
        return count.intValue();
    }

    public void setBibliographicDetailsRepository(HoldingsDetailsRepository holdingsDetailsRepository) {
        this.holdingsDetailsRepository = holdingsDetailsRepository;
    }
}