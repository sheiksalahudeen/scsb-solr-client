package org.recap.service.deAccession;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.deAccession.DeAccessionDBResponseEntity;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.report.ReportGenerator;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by angelind on 10/11/16.
 */
public class DeAccessionServiceUT extends BaseTestCase{

    @Autowired
    ReportGenerator reportGenerator;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    DeAccessionService deAccessionService;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void deAccessionItemsInDB() throws Exception {
        Random random = new Random();
        String itemBarcode = String.valueOf(random.nextInt());
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem(itemBarcode);

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getBibliographicId());
        Thread.sleep(3000);
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = deAccessionService.deAccessionItemsInDB(Arrays.asList(itemBarcode));
        assertNotNull(deAccessionDBResponseEntities);
        assertTrue(deAccessionDBResponseEntities.size()==1);
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = deAccessionDBResponseEntities.get(0);
        assertNotNull(deAccessionDBResponseEntity);
        assertEquals(deAccessionDBResponseEntity.getStatus(), RecapConstants.SUCCESS);

        List<ItemEntity> fetchedItemEntities = itemDetailsRepository.findByBarcodeIn(Arrays.asList(itemBarcode));
        entityManager.refresh(fetchedItemEntities.get(0));
        assertNotNull(fetchedItemEntities);
        assertTrue(fetchedItemEntities.size() == 1);
        assertEquals(Boolean.TRUE, fetchedItemEntities.get(0).isDeleted());
        assertNotNull(fetchedItemEntities.get(0).getLastUpdatedBy());
        assertEquals(RecapConstants.GUEST, fetchedItemEntities.get(0).getLastUpdatedBy());
        assertNotNull(fetchedItemEntities.get(0).getLastUpdatedDate());
        assertNotNull(savedBibliographicEntity.getHoldingsEntities());
        assertTrue(savedBibliographicEntity.getHoldingsEntities().size() == 1);
        assertNotNull(savedBibliographicEntity.getHoldingsEntities().get(0).getItemEntities());
        assertTrue(savedBibliographicEntity.getHoldingsEntities().get(0).getItemEntities().size() == 1);
        assertNotEquals(savedBibliographicEntity.getHoldingsEntities().get(0).getItemEntities().get(0).getLastUpdatedDate(), fetchedItemEntities.get(0).getLastUpdatedDate());
    }

    @Test
    public void processAndSave() throws Exception {
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setBarcode("12345");
        deAccessionDBResponseEntity.setStatus(RecapConstants.FAILURE);
        deAccessionDBResponseEntity.setReasonForFailure(RecapConstants.ITEM_BARCDE_DOESNOT_EXIST);

        List<ReportEntity> reportEntities = deAccessionService.processAndSave(Arrays.asList(deAccessionDBResponseEntity));
        assertNotNull(reportEntities);
        assertTrue(reportEntities.size()==1);
        ReportEntity reportEntity = reportEntities.get(0);
        assertNotNull(reportEntity);

        assertNotNull(reportEntity.getReportDataEntities());
        assertTrue(reportEntity.getReportDataEntities().size()==4);
        Date createdDate = reportEntities.get(0).getCreatedDate();
        String generatedReportFileNameInFileSyatem = reportGenerator.generateReport(RecapConstants.DEACCESSION_REPORT, RecapConstants.PRINCETON, RecapConstants.DEACCESSION_SUMMARY_REPORT, RecapConstants.FILE_SYSTEM, getFromDate(createdDate), getToDate(createdDate));
        Thread.sleep(1000);
        assertNotNull(generatedReportFileNameInFileSyatem);
        String generatedReportFileNameInFTP = reportGenerator.generateReport(RecapConstants.DEACCESSION_REPORT, RecapConstants.PRINCETON, RecapConstants.DEACCESSION_SUMMARY_REPORT, RecapConstants.FTP, getFromDate(createdDate), getToDate(createdDate));
        Thread.sleep(1000);
        assertNotNull(generatedReportFileNameInFTP);
    }

    private Date getFromDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return  cal.getTime();
    }

    private Date getToDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
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