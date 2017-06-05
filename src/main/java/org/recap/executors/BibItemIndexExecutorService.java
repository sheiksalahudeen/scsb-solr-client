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
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    private HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * Gets the callable class for the thread to process.
     * @param coreName
     * @param pageNum
     * @param docsPerPage
     * @param owningInstitutionId
     * @param fromDate
     * @return
     */
    @Override
    public Callable getCallable(String coreName, int pageNum, int docsPerPage, Integer owningInstitutionId, Date fromDate) {
        return new BibItemIndexCallable(solrServerProtocol + solrUrl, coreName, pageNum, docsPerPage, bibliographicDetailsRepository, holdingsDetailsRepository, owningInstitutionId, fromDate, producerTemplate, solrTemplate);
    }

    /**
     * Gets the total documents count from database by owning institution id or by using from date.
     * @param owningInstitutionId
     * @param fromDate
     * @return
     */
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

    /**
     * This method sets bibliographic details repository.
     *
     * @param bibliographicDetailsRepository the bibliographic details repository
     */
    public void setBibliographicDetailsRepository(BibliographicDetailsRepository bibliographicDetailsRepository) {
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
    }
}