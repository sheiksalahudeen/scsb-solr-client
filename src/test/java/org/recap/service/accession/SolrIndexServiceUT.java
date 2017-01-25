package org.recap.service.accession;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.solr.Bib;
import org.recap.service.accession.SolrIndexService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by rajeshbabuk on 10/11/16.
 */
public class SolrIndexServiceUT extends BaseTestCase {

    @Autowired
    SolrIndexService solrIndexService;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void indexByBibliographicId() throws Exception {
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem();

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        Integer bibliographicId = savedBibliographicEntity.getBibliographicId();
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getHoldingsEntities());
        assertNotNull(savedBibliographicEntity.getItemEntities());

        solrIndexService.indexByBibliographicId(bibliographicId);

        Bib bib = bibSolrCrudRepository.findByBibId(bibliographicId);
        assertNotNull(bib);
        deleteByDocId("BibId",savedBibliographicEntity.getBibliographicId().toString());
        deleteByDocId("HoldingId",savedBibliographicEntity.getHoldingsEntities().get(0).getHoldingsId().toString());
        deleteByDocId("ItemId",savedBibliographicEntity.getItemEntities().get(0).getItemId().toString());
    }

    public void deleteByDocId(String docIdParam, String docIdValue) throws IOException, SolrServerException {
        UpdateResponse updateResponse = solrTemplate.getSolrClient().deleteByQuery(docIdParam+":"+docIdValue);
        solrTemplate.commit();
    }

    @Test
    public void deleteByDocId() throws Exception {
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem();

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        Integer bibliographicId = savedBibliographicEntity.getBibliographicId();
        solrIndexService.indexByBibliographicId(bibliographicId);
        Bib bib = bibSolrCrudRepository.findByBibId(bibliographicId);
        assertNotNull(bib);
        solrIndexService.deleteByDocId("BibId",String.valueOf(bibliographicId));
        Bib bib1 = bibSolrCrudRepository.findByBibId(bibliographicId);
        assertNull(bib1);
        deleteByDocId("BibId",savedBibliographicEntity.getBibliographicId().toString());
        deleteByDocId("HoldingId",savedBibliographicEntity.getHoldingsEntities().get(0).getHoldingsId().toString());
        deleteByDocId("ItemId",savedBibliographicEntity.getItemEntities().get(0).getItemId().toString());
    }

    private BibliographicEntity getBibEntityWithHoldingsAndItem() throws Exception {
        Random random = new Random();
        File bibContentFile = getBibContentFile();
        File holdingsContentFile = getHoldingsContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        String sourceHoldingsContent = FileUtils.readFileToString(holdingsContentFile, "UTF-8");

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(sourceBibContent.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setDeleted(false);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent(sourceHoldingsContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));
        holdingsEntity.setDeleted(false);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        String barcode = String.valueOf(random.nextInt());
        itemEntity.setBarcode(barcode);
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setDeleted(false);

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;
    }

    private File getBibContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("BibContent.xml");
        return new File(resource.toURI());
    }

    private File getHoldingsContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("HoldingsContent.xml");
        return new File(resource.toURI());
    }
}
