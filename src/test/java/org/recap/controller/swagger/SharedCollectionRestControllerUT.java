package org.recap.controller.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.recap.controller.BaseControllerUT;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.deAccession.DeAccessionRequest;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.solr.Bib;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.service.accession.SolrIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by chenchulakshmig on 14/10/16.
 */
public class SharedCollectionRestControllerUT extends BaseControllerUT {

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    @Autowired
    SolrIndexService solrIndexService;

    @Test
    public void itemAvailabilityStatus() throws Exception {
        String itemBarcode = "32101056185125";
        saveBibEntityWithHoldingsAndItem(itemBarcode);
        String itemAvailabilityStatusCode = itemDetailsRepository.getItemStatusByBarcodeAndIsDeletedFalse(itemBarcode);
        MvcResult mvcResult = this.mockMvc.perform(get("/sharedCollection/itemAvailabilityStatus")
                .param("itemBarcode",itemBarcode))
                .andReturn();
        assertNotNull(mvcResult);
        assertNotNull(mvcResult.getResponse());
        assertEquals(mvcResult.getResponse().getContentAsString(), itemAvailabilityStatusCode);
    }

    @Test
    public void accession() throws Exception {
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PB");
        accessionRequest.setItemBarcode("32101095533293");

        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult mvcResult = this.mockMvc.perform(post("/sharedCollection/accession")
                .headers(getHttpHeaders())
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(accessionRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertEquals(result, "Success");
    }

    @Test
    public void deAccession() throws Exception {
        Random random = new Random();
        String itemBarcode = String.valueOf(random.nextInt());
        BibliographicEntity bibliographicEntity = saveBibEntityWithHoldingsAndItem(itemBarcode);

        assertNotNull(bibliographicEntity);
        Integer bibliographicId = bibliographicEntity.getBibliographicId();
        assertNotNull(bibliographicId);

        BibliographicEntity byBibliographicId = bibliographicDetailsRepository.findByBibliographicId(bibliographicId);
        assertNotNull(byBibliographicId);

        solrIndexService.indexByBibliographicId(bibliographicId);

        Bib bib = bibSolrCrudRepository.findByBibId(bibliographicId);
        assertNotNull(bib);
        assertEquals(bib.getBibId(), bibliographicId);

        DeAccessionRequest deAccessionRequest = new DeAccessionRequest();
        deAccessionRequest.setItemBarcodes(Arrays.asList(itemBarcode));

        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult mvcResult = this.mockMvc.perform(post("/sharedCollection/deAccession")
                .headers(getHttpHeaders())
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(deAccessionRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertEquals(result, "{\"" + itemBarcode + "\":\"Success\"}");
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api_key", "recap");
        return headers;
    }

    private BibliographicEntity saveBibEntityWithHoldingsAndItem(String itemBarcode) throws Exception {
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
        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        return savedBibliographicEntity;
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