package org.recap.controller.swagger;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.RecapConstants;
import org.recap.controller.BaseControllerUT;
import org.recap.controller.SharedCollectionRestController;
import org.recap.model.ItemAvailabilityResponse;
import org.recap.model.ItemAvailabityStatusRequest;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.recap.service.ItemAvailabilityService;
import org.recap.service.accession.AccessionService;
import org.recap.service.accession.SolrIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        BibliographicEntity bibliographicEntity = saveBibEntityWithHoldingsAndItem(itemBarcode);
        ItemAvailabityStatusRequest itemAvailabityStatusRequest = new ItemAvailabityStatusRequest();
        List<ItemEntity> itemEntities = bibliographicEntity.getItemEntities();
        String barcode = null;
        String status = null;
        for (ItemEntity itemEntity : itemEntities) {
            barcode  = itemEntity.getBarcode();
            status = itemEntity.getItemStatusEntity().getStatusDescription();
        }
        List<String>  barcodeList = new ArrayList<>();
        barcodeList.add(barcode);
        itemAvailabityStatusRequest.setBarcodes(barcodeList);
        Mockito.when(mockedSharedCollectionRestController.getItemAvailabilityService()).thenReturn(itemAvailabilityService);
        Mockito.when(mockedSharedCollectionRestController.getItemAvailabilityService().getItemStatusByBarcodeAndIsDeletedFalse(itemBarcode)).thenReturn(RecapConstants.AVAILABLE);
        Mockito.when(mockedSharedCollectionRestController.itemAvailabilityStatus(itemAvailabityStatusRequest)).thenCallRealMethod();
        List<ItemAvailabilityResponse> itemAvailabilityResponses = mockedSharedCollectionRestController.itemAvailabilityStatus(itemAvailabityStatusRequest);
        assertNotNull(itemAvailabilityResponses);
        for (ItemAvailabilityResponse itemAvailabilityResponse : itemAvailabilityResponses) {
            assertEquals(itemAvailabilityResponse.getItemAvailabilityStatus(),status);
        }
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