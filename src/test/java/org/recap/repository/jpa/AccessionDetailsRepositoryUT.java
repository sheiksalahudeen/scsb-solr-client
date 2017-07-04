package org.recap.repository.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.AccessionEntity;
import org.recap.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by rajeshbabuk on 9/5/17.
 */
public class AccessionDetailsRepositoryUT extends BaseTestCase {

    @Autowired
    AccessionDetailsRepository accessionDetailsRepository;

    @Autowired
    DateUtil dateUtil;

    @Test
    public void testSaveAccessionRequest() throws Exception {
        AccessionEntity accessionEntity = new AccessionEntity();
        accessionEntity.setAccessionRequest("[{\"customerCode\":\"PA\",\"itemBarcode\":\"123\"}]");
        accessionEntity.setCreatedDate(new Date());
        accessionEntity.setAccessionStatus(RecapConstants.PENDING);
        AccessionEntity savedAccessionEntity = accessionDetailsRepository.save(accessionEntity);
        assertNotNull(savedAccessionEntity);
    }

    @Test
    public void getAccessionEntityByDateAndStatus() throws Exception {
        testSaveAccessionRequest();
        List<AccessionEntity> accessionEntities = accessionDetailsRepository.getAccessionEntityByDateAndStatus(dateUtil.getFromDate(new Date()), dateUtil.getToDate(new Date()), RecapConstants.PENDING);
        assertNotNull(accessionEntities);
        assertEquals(accessionEntities.size(), 1);
    }

    @Test
    public void getAccessionEntityByStatus() throws Exception {
        testSaveAccessionRequest();
        List<AccessionEntity> accessionEntities = accessionDetailsRepository.findByAccessionStatus(RecapConstants.PENDING);
        assertNotNull(accessionEntities);
    }

    @Test
    public void testAccessionEntity(){
        AccessionEntity accessionEntity = new AccessionEntity();
        accessionEntity.setAccessionId(1);
        accessionEntity.setAccessionRequest("test");
        accessionEntity.setAccessionStatus("Complete");
        accessionEntity.setCreatedDate(new Date());
        assertNotNull(accessionEntity.getAccessionId());
        assertNotNull(accessionEntity.getAccessionRequest());
        assertNotNull(accessionEntity.getAccessionStatus());
        assertNotNull(accessionEntity.getCreatedDate());
    }
}
