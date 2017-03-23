package org.recap.executors;

import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Created by pvsubrah on 6/15/16.
 */

@Service
public class BibIndexExecutorService extends IndexExecutorService {

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Override
    public Callable getCallable(String coreName, int pageNum, int docsPerPage, Integer owningInstitutionId, Date fromDate) {
        return new BibIndexCallable(coreName, pageNum, docsPerPage, bibliographicDetailsRepository, holdingsDetailsRepository, owningInstitutionId, producerTemplate);
    }

    @Override
    protected Integer getTotalDocCount(Integer owningInstitutionId, Date fromDate) {
        Long count = owningInstitutionId == null ? bibliographicDetailsRepository.countByIsDeletedFalse() : bibliographicDetailsRepository.countByOwningInstitutionIdAndIsDeletedFalse(owningInstitutionId);
        return count.intValue();
    }

    public void setBibliographicDetailsRepository(BibliographicDetailsRepository bibliographicDetailsRepository) {
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
    }
}