package org.recap.model.jpa;

import org.junit.Ignore;
import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 21/6/16.
 */
public class BibliographicHoldingsEntityTest extends BaseTestCase {

    @Test

    public void saveBibliographicHoldingsEntity(){

        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("Mock Bib Content");
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random));
        bibliographicEntity.setOwningInstitutionId(3);
        BibliographicEntity bibEntity = bibliographicDetailsRepository.save(bibliographicEntity);
        assertNotNull(bibEntity);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("Mock holding content");
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setBibliographicId(1);
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setOwningInstitutionHoldingsId(".c10899406");
        HoldingsEntity entity = holdingDetailRepository.save(holdingsEntity);
        assertNotNull(entity);

        BibliographicHoldingsEntity bibliographicHoldingsEntity = new BibliographicHoldingsEntity();
        bibliographicHoldingsEntity.setBibliographicId(bibEntity.getBibliographicId());
        bibliographicHoldingsEntity.setHoldingsId(entity.getHoldingsId());
        BibliographicHoldingsEntity bibHoldingEntity = bibliographicHoldingDetailsRepository.save(bibliographicHoldingsEntity);
        System.out.println("BibliographicHoldingId-->"+bibHoldingEntity.getBibliographicHoldingsId());
        assertNotNull(bibHoldingEntity);
        bibliographicHoldingDetailsRepository.delete(bibliographicHoldingsEntity);
        bibliographicDetailsRepository.delete(bibliographicEntity);
        holdingDetailRepository.delete(holdingsEntity);
    }
}