package org.recap.controller.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.RecapConstants;
import org.recap.controller.BaseControllerUT;
import org.recap.controller.SharedCollectionRestController;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.deAccession.DeAccessionRequest;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.solr.Bib;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.recap.service.ItemAvailabilityService;
import org.recap.service.accession.AccessionService;
import org.recap.service.accession.SolrIndexService;
import org.recap.service.deAccession.DeAccessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MvcResult;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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

    @Mock
    SolrIndexService solrIndexService;

    @Mock
    BibSolrCrudRepository bibSolrCrudRepository;

    @Mock
    SharedCollectionRestController mockedSharedCollectionRestController;

    @Mock
    DeAccessionService deAccessionService;

    @Mock
    AccessionService accessionService;

    @Mock
    ItemAvailabilityService itemAvailabilityService;

    @Before
    public void setup()throws Exception{
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void itemAvailabilityStatus() throws Exception {
        String itemBarcode = "32101056185125";
        saveBibEntityWithHoldingsAndItem(itemBarcode);
        String itemAvailabilityStatusCode = itemDetailsRepository.getItemStatusByBarcodeAndIsDeletedFalse(itemBarcode);
        Mockito.when(mockedSharedCollectionRestController.getItemAvailabilityService()).thenReturn(itemAvailabilityService);
        Mockito.when(mockedSharedCollectionRestController.getItemAvailabilityService().getItemStatusByBarcodeAndIsDeletedFalse(itemBarcode)).thenReturn(RecapConstants.AVAILABLE);
        Mockito.when(mockedSharedCollectionRestController.itemAvailabilityStatus(itemBarcode)).thenCallRealMethod();
        ResponseEntity responseEntity = mockedSharedCollectionRestController.itemAvailabilityStatus(itemBarcode);
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getBody(),itemAvailabilityStatusCode);
    }

    @Test
    public void accession() throws Exception {
        List<AccessionRequest> accessionRequestList = new ArrayList<>();
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PB");
        accessionRequest.setItemBarcode("32101062128309");
        accessionRequestList.add(accessionRequest);
        Mockito.when(mockedSharedCollectionRestController.getAccessionService()).thenReturn(accessionService);
        Mockito.when(mockedSharedCollectionRestController.getInputLimit()).thenReturn(10);
        Mockito.when(mockedSharedCollectionRestController.getAccessionService().processRequest(accessionRequestList)).thenReturn("Success");
        Mockito.when(mockedSharedCollectionRestController.accession(accessionRequestList)).thenCallRealMethod();
        ResponseEntity responseEntity = mockedSharedCollectionRestController.accession(accessionRequestList);
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getBody(),"Success");
    }

    @Test
    public void deAccession() throws Exception {
        Random random = new Random();
        String itemBarcode = String.valueOf(random.nextInt());
        BibliographicEntity bibliographicEntity = saveBibEntityWithHoldingsAndItem(itemBarcode);
        Bib bib1 = new Bib();
        bib1.setBibId(bibliographicEntity.getBibliographicId());

        assertNotNull(bibliographicEntity);
        Integer bibliographicId = bibliographicEntity.getBibliographicId();
        assertNotNull(bibliographicId);

        BibliographicEntity byBibliographicId = bibliographicDetailsRepository.findByBibliographicId(bibliographicId);
        assertNotNull(byBibliographicId);

        solrIndexService.indexByBibliographicId(bibliographicId);
        Mockito.when(bibSolrCrudRepository.findByBibId(bibliographicId)).thenReturn(bib1);
        Bib bib = bibSolrCrudRepository.findByBibId(bibliographicId);
        assertNotNull(bib);
        assertEquals(bib.getBibId(), bibliographicId);
        DeAccessionRequest deAccessionRequest = new DeAccessionRequest();
        deAccessionRequest.setItemBarcodes(Arrays.asList(itemBarcode));
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put(itemBarcode,"Success");
        Mockito.when(mockedSharedCollectionRestController.getDeAccessionService()).thenReturn(deAccessionService);
        Mockito.when(mockedSharedCollectionRestController.getDeAccessionService().deAccession(deAccessionRequest)).thenReturn(resultMap);
        Mockito.when(mockedSharedCollectionRestController.deAccession(deAccessionRequest)).thenCallRealMethod();
        ResponseEntity responseEntity = mockedSharedCollectionRestController.deAccession(deAccessionRequest);
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getBody().toString().contains("Success"));
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