package org.recap.repository.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.ItemChangeLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by rajeshbabuk on 18/10/16.
 */
public class ItemChangeLogDetailsRepositoryUT extends BaseTestCase {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void saveItemChangeLogEntity() throws Exception {
        ItemChangeLogEntity itemChangeLogEntity = new ItemChangeLogEntity();
        itemChangeLogEntity.setUpdatedBy("guest");
        itemChangeLogEntity.setUpdatedDate(new Date());
        itemChangeLogEntity.setOperationType("Test");
        itemChangeLogEntity.setNotes("Test Notes");

        ItemChangeLogEntity savedItemChangeLogEntity = itemChangeLogDetailsRepository.save(itemChangeLogEntity);
        entityManager.refresh(savedItemChangeLogEntity);
        assertNotNull(savedItemChangeLogEntity);
        assertNotNull(savedItemChangeLogEntity.getChangeLogId());
    }

    @Test
    public void checkfindByRecordId() throws Exception{
        ItemChangeLogEntity itemChangeLogEntity = saveDeaccessionNotes();
        ItemChangeLogEntity byRecordId = itemChangeLogDetailsRepository.findByRecordIdAndOperationType(itemChangeLogEntity.getRecordId(),"Deaccession");
        assertNotNull(byRecordId);
        if (itemChangeLogEntity.getOperationType().equalsIgnoreCase("Deaccession")){
            assertEquals("testing",byRecordId.getNotes());
        }

    }

    @Test
    public void getRecordIdByOperationType() throws Exception {
        ItemChangeLogEntity itemChangeLogEntity = saveDeaccessionNotes();
        Page<Integer> recordIdByOperationType = itemChangeLogDetailsRepository.getRecordIdByOperationType(new PageRequest(0,10), itemChangeLogEntity.getOperationType());
        assertNotNull(recordIdByOperationType);
        assertTrue(recordIdByOperationType.getTotalElements() > 0);
        assertNotNull(recordIdByOperationType.getContent());
        Integer recordId = recordIdByOperationType.getContent().get(0);
        assertNotNull(recordId);
    }

    private ItemChangeLogEntity saveDeaccessionNotes() throws Exception{
        ItemChangeLogEntity itemChangeLogEntity = new ItemChangeLogEntity();
        itemChangeLogEntity.setUpdatedBy("guest");
        itemChangeLogEntity.setUpdatedDate(new Date());
        itemChangeLogEntity.setOperationType("Deaccession");
        itemChangeLogEntity.setNotes("testing");
        itemChangeLogEntity.setRecordId(1);
        ItemChangeLogEntity savedItemChangeLogEntity = itemChangeLogDetailsRepository.save(itemChangeLogEntity);
        entityManager.refresh(savedItemChangeLogEntity);
        return itemChangeLogEntity;
    }

}
