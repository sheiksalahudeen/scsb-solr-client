package org.recap.service.accession;

import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by sheiks on 26/05/17.
 */
@Repository
public class AccessionDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    private ReportDetailRepository reportDetailRepository;

    @Transactional
    public BibliographicEntity saveBibRecord(BibliographicEntity bibliographicEntity) {
        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        return savedBibliographicEntity;
    }

    @Transactional
    public ReportEntity saveReportEntity(ReportEntity reportEntity) {
        return reportDetailRepository.save(reportEntity);
    }
}
