package org.recap.util;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.search.BibliographicMarcForm;
import org.recap.model.userManagement.UserDetailsForm;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.CustomerCodeDetailsRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 24/2/17.
 */
public class MarcRecordViewUtilUT extends BaseTestCase{

    @Mock
    MarcRecordViewUtil marcRecordViewUtil;

    @PersistenceContext
    private EntityManager entityManager;

    @Mock
    BibliographicDetailsRepository mockBibliographicDetailsRepository;

    @Mock
    CustomerCodeDetailsRepository mockCustomerCodeDetailsRepository;


    @Test
    public void testBuildBibliographicMarcForm() throws Exception {
        Integer bibId = 123;
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setLoginInstitutionId(1);
        userDetailsForm.setSuperAdmin(false);
        userDetailsForm.setRecapUser(false);
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        Integer itemId = bibliographicEntity.getItemEntities().get(0).getItemId();
        Mockito.when(marcRecordViewUtil.getBibliographicDetailsRepository()).thenReturn(mockBibliographicDetailsRepository);
        Mockito.when(marcRecordViewUtil.getCustomerCodeDetailsRepository()).thenReturn(mockCustomerCodeDetailsRepository);
        Mockito.when(marcRecordViewUtil.getBibliographicDetailsRepository().findByBibliographicIdAndIsDeletedFalse(bibId)).thenReturn(bibliographicEntity);
        Mockito.when(marcRecordViewUtil.getBibliographicDetailsRepository().getNonDeletedItemEntities(bibliographicEntity.getOwningInstitutionId(), bibliographicEntity.getOwningInstitutionBibId())).thenReturn(bibliographicEntity.getItemEntities());
        Mockito.when(marcRecordViewUtil.buildBibliographicMarcForm(bibId,itemId,userDetailsForm)).thenCallRealMethod();
        BibliographicMarcForm bibliographicMarcForm = marcRecordViewUtil.buildBibliographicMarcForm(bibId,itemId,userDetailsForm);
        assertNotNull(bibliographicMarcForm);
        assertEquals(bibliographicMarcForm.getOwningInstitution(),"PUL");
        assertEquals(bibliographicMarcForm.getAvailability(),"Available");
        assertEquals(bibliographicMarcForm.getLeaderMaterialType(),"Monograph");

    }

    public BibliographicEntity saveBibSingleHoldingsSingleItem() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        File holdingsContentFile = getHoldingsContentFile();
        File bibContentFile = getBibContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        String sourceHoldingContent = FileUtils.readFileToString(holdingsContentFile, "UTF-8");
        bibliographicEntity.setContent(sourceBibContent.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent(sourceHoldingContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("123");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(2);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getHoldingsEntities());
        assertNotNull(savedBibliographicEntity.getItemEntities());
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