package org.recap.repository.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.RequestItemEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by akulak on 5/12/16.
 */
public class BDRequestItemDetailsRepositoryUT extends BaseTestCase {

    @Autowired
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    @Test
    public void checkSaveForItemEntity() throws Exception {
        List<ItemEntity> itemEntityList = new ArrayList<>();

        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setBarcode("b3");
        itemEntity1.setCustomerCode("c1");
        itemEntity1.setCallNumber("cn1");
        itemEntity1.setCallNumberType("ct1");
        itemEntity1.setItemAvailabilityStatusId(1);
        itemEntity1.setCopyNumber(1);
        itemEntity1.setOwningInstitutionId(1);
        itemEntity1.setCollectionGroupId(1);
        itemEntity1.setCreatedDate(new Date());
        itemEntity1.setCreatedBy("admin");
        itemEntity1.setLastUpdatedDate(new Date());
        itemEntity1.setLastUpdatedBy("admin");
        itemEntity1.setUseRestrictions("no");
        itemEntity1.setVolumePartYear("v3");
        itemEntity1.setOwningInstitutionItemId("t3");
        itemEntity1.setDeleted(false);

        itemEntityList.add(itemEntity1);

        ItemEntity itemEntity2 = new ItemEntity();
        itemEntity2.setBarcode("b4");
        itemEntity2.setCustomerCode("c2");
        itemEntity2.setCallNumber("cn2");
        itemEntity2.setCallNumberType("ct2");
        itemEntity2.setItemAvailabilityStatusId(2);
        itemEntity2.setCopyNumber(1);
        itemEntity2.setOwningInstitutionId(1);
        itemEntity2.setCollectionGroupId(1);
        itemEntity2.setCreatedDate(new Date());
        itemEntity2.setCreatedBy("admin");
        itemEntity2.setLastUpdatedDate(new Date());
        itemEntity2.setLastUpdatedBy("admin");
        itemEntity2.setUseRestrictions("no");
        itemEntity2.setVolumePartYear("v4");
        itemEntity2.setOwningInstitutionItemId("t4");
        itemEntity2.setDeleted(false);

        itemEntityList.add(itemEntity2);

        itemDetailsRepository.save(itemEntityList);

    }

    private void checkSaveForRequestEntity() throws Exception {
        List<RequestItemEntity> requestItemEntityList = new ArrayList<>();
        RequestItemEntity requestItemEntity1 = new RequestItemEntity();
        requestItemEntity1.setItemId(78);
        requestItemEntity1.setRequestTypeId(5);
        requestItemEntity1.setRequestingInstitutionId(2);
        requestItemEntity1.setRequestExpirationDate(new Date());
        requestItemEntity1.setCreatedDate(new Date());
        requestItemEntity1.setLastUpdatedDate(new Date());
        requestItemEntity1.setPatronId(99);
        requestItemEntity1.setRequestPosition(3);
        requestItemEntity1.setStopCode("s1");

        requestItemEntityList.add(requestItemEntity1);

        RequestItemEntity requestItemEntity2 = new RequestItemEntity();
        requestItemEntity2.setItemId(79);
        requestItemEntity2.setRequestTypeId(5);
        requestItemEntity2.setRequestingInstitutionId(3);
        requestItemEntity2.setRequestExpirationDate(new Date());
        requestItemEntity2.setCreatedDate(new Date());
        requestItemEntity2.setLastUpdatedDate(new Date());
        requestItemEntity2.setPatronId(100);
        requestItemEntity2.setRequestPosition(4);
        requestItemEntity2.setStopCode("s1");

        requestItemEntityList.add(requestItemEntity2);

        requestItemDetailsRepository.save(requestItemEntityList);
    }

    @Test
    public void checkGetBDRequestCountsForPul() throws Exception{
        //checkSaveForRequestEntity();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fromDate = simpleDateFormat.parse("2016-11-01 00:00:00");
        Date toDate = simpleDateFormat.parse("2016-12-20 23:59:59");
        long count = requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(fromDate,toDate,1,5);
        assertNotNull(count);
        assertEquals(2,count);
    }

    @Test
    public void checkGetBDRequestCountsForCul() throws Exception{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fromDate = simpleDateFormat.parse("2016-11-01 00:00:00");
        Date toDate = simpleDateFormat.parse("2016-12-20 23:59:59");
        long count = requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(fromDate,toDate,2,5);
        assertNotNull(count);
        assertEquals(0,count);
    }

    @Test
    public void checkGetBDRequestCountsForNypl() throws Exception{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fromDate = simpleDateFormat.parse("2016-11-01 00:00:00");
        Date toDate = simpleDateFormat.parse("2016-12-20 23:59:59");
        long count = requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(fromDate,toDate,3,5);
        assertNotNull(count);
        assertEquals(0,count);
    }
}

