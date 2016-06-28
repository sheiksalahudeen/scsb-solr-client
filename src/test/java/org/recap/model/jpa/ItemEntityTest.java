package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 21/6/16.
 */
public class ItemEntityTest extends BaseTestCase {

    @Test
    public void findByInstitutionId() throws Exception{
        Long beforeSaveCount = itemDetailsRepository.countByOwningInstitutionId(3);
        assertNotNull(beforeSaveCount);
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("Mock holding content");
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setBibliographicId(1);
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setOwningInstitutionHoldingsId(".c10899406");
        HoldingsEntity entity = holdingDetailRepository.save(holdingsEntity);
        assertNotNull(entity);


        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setBarcode("123456");
        itemEntity.setCustomerCode("112345");
        itemEntity.setHoldingsId(entity.getHoldingsId());
        itemEntity.setCallNumber("123");
        itemEntity.setCallNumberType("x");
        itemEntity.setCopyNumber(1);
        itemEntity.setNotesId(1);
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setOwningInstitutionId(3);
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCreatedDate(new Date());
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setBibliographicId(1);
        itemEntity.setUseRestrictions("In library use");
        itemEntity.setVolumePartYear("2016");
        itemEntity.setOwningInstitutionItemId("1234");
        ItemEntity itemEntity1 = itemDetailsRepository.save(itemEntity);
        assertNotNull(itemEntity1);
        Long afterSaveCount = itemDetailsRepository.countByOwningInstitutionId(3);
        assertTrue((beforeSaveCount+1) == afterSaveCount);
        assertEquals(itemEntity1.getBarcode(),"123456");
        assertEquals(itemEntity1.getCustomerCode(),"112345");
        assertEquals(itemEntity1.getHoldingsId(),entity.getHoldingsId());
        assertEquals(itemEntity1.getCallNumber(),"123");
        assertEquals(itemEntity1.getCallNumberType(),"x");
        assertEquals(itemEntity1.getItemAvailabilityStatusId().toString(),"1");
        assertEquals(itemEntity1.getOwningInstitutionId().toString(),"3");
        assertEquals(itemEntity1.getCollectionGroupId().toString(),"1");
        assertEquals(itemEntity1.getBibliographicId().toString(),"1");
        assertEquals(itemEntity1.getUseRestrictions(),"In library use");
        assertEquals(itemEntity1.getVolumePartYear(),"2016");
        assertEquals(itemEntity1.getCopyNumber().toString(),"1");
        assertEquals(itemEntity1.getNotesId().toString(),"1");
        assertEquals(itemEntity1.getOwningInstitutionItemId(),"1234");
        System.out.println("Created date -->"+itemEntity1.getCreatedDate() +""+ "last updated date-->"+itemEntity1.getLastUpdatedDate());
        itemDetailsRepository.delete(itemEntity1);
        holdingDetailRepository.delete(entity);

    }
}