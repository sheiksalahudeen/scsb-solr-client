package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

/**
 * Created by rajeshbabuk on 13/9/16.
 */
@Service
public class HoldingsIndexExecutorService extends IndexExecutorService {

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    ProducerTemplate producerTemplate;


    @Override
    public Callable getCallable(String coreName, int pageNum, int docsPerPage, Integer owningInstitutionId) {
        return new HoldingsIndexCallable(pageNum, docsPerPage, holdingsDetailsRepository, owningInstitutionId, producerTemplate);
    }

    @Override
    protected Integer getTotalDocCount(Integer owningInstitutionId) {
        Long count = owningInstitutionId == null ? holdingsDetailsRepository.count() : holdingsDetailsRepository.countByOwningInstitutionId(owningInstitutionId);
        return count.intValue();
    }

    @Override
    protected String getResourceURL() {
        return null;
    }

    public void setBibliographicDetailsRepository(HoldingsDetailsRepository holdingsDetailsRepository) {
        this.holdingsDetailsRepository = holdingsDetailsRepository;
    }
}
