package org.recap.executors;

import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Created by chenchulakshmig on 21/6/16.
 */
@Service
public class BibItemIndexExecutorService extends IndexExecutorService {

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    SolrTemplate solrTemplate;

    @Override
    public Callable getCallable(String coreName, int pageNum, int docsPerPage, Integer owningInstitutionId, Date fromDate) {
        return new BibItemIndexCallable(solrServerProtocol + solrUrl, coreName, pageNum, docsPerPage, bibliographicDetailsRepository, holdingsDetailsRepository, owningInstitutionId, fromDate, producerTemplate, solrTemplate);
    }

    @Override
    protected Integer getTotalDocCount(Integer owningInstitutionId, Date fromDate) {
        Long count = 0L;
        if (null == owningInstitutionId && null == fromDate) {
            count = bibliographicDetailsRepository.count();
        } else if (null != owningInstitutionId && null == fromDate) {
            count = bibliographicDetailsRepository.countByOwningInstitutionId(owningInstitutionId);
        } else if (null == owningInstitutionId && null != fromDate) {
            count = bibliographicDetailsRepository.countByLastUpdatedDateAfter(fromDate);
        } else if (null != owningInstitutionId && null != fromDate) {
            count = bibliographicDetailsRepository.countByOwningInstitutionIdAndLastUpdatedDateAfter(owningInstitutionId, fromDate);
        }
        return count.intValue();
    }

    public void setBibliographicDetailsRepository(BibliographicDetailsRepository bibliographicDetailsRepository) {
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
    }
}