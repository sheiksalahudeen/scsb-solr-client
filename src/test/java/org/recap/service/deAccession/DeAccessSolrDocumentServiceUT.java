package org.recap.service.deAccession;

import org.apache.commons.io.FileUtils;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.util.BibJSONUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by angelind on 10/11/16.
 */
public class DeAccessSolrDocumentServiceUT extends BaseTestCase{

    @Autowired
    DeAccessSolrDocumentService deAccessSolrDocumentService;

    @Test
    public void testUpdateIsDeletedBibByBibId() throws Exception {
        Random random = new Random();
        String itemBarcode = String.valueOf(random.nextInt());
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem(itemBarcode);
        BibJSONUtil bibJSONUtil = new BibJSONUtil();
        SolrInputDocument solrInputDocument = bibJSONUtil.generateBibAndItemsForIndex(bibliographicEntity, solrTemplate, bibliographicDetailsRepository, holdingDetailRepository);
        solrTemplate.saveDocument(solrInputDocument);
        solrTemplate.commit();
        SolrInputField bibId = solrInputDocument.getField("BibId");
        assertNotNull(bibId);
        assertEquals(bibId, bibliographicEntity.getBibliographicId());
        String updateIsDeletedBibByBibId = deAccessSolrDocumentService.updateIsDeletedBibByBibId(Arrays.asList(bibliographicEntity.getBibliographicId()));
        assertNotNull(updateIsDeletedBibByBibId);
        assertEquals(updateIsDeletedBibByBibId,"Bib documents updated successfully.");
    }

    @Test
    public void testUpdateIsDeletedHoldingsByHoldingsId() throws Exception {
        String response = deAccessSolrDocumentService.updateIsDeletedHoldingsByHoldingsId(Arrays.asList(596095));
        assertNotNull(response);
        assertEquals(response, "Holdings documents updated successfully.");
    }

    @Test
    public void testUpdateIsDeletedItemByItemIds(){
        String response = deAccessSolrDocumentService.updateIsDeletedItemByItemIds(Arrays.asList(825172));
        assertNotNull(response);
        assertNotNull(response,"Item documents updated successfully.");

    }

    private BibliographicEntity getBibEntityWithHoldingsAndItem(String itemBarcode) throws Exception {
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
        String owningInstitutionBibId = String.valueOf(random.nextInt());
        bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);
        bibliographicEntity.setDeleted(false);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent(sourceHoldingsContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        String owningInstitutionHoldingsId = String.valueOf(random.nextInt());
        holdingsEntity.setOwningInstitutionHoldingsId(owningInstitutionHoldingsId);
        holdingsEntity.setDeleted(false);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        String owningInstitutionItemId = String.valueOf(random.nextInt());
        itemEntity.setOwningInstitutionItemId(owningInstitutionItemId);
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode(itemBarcode);
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