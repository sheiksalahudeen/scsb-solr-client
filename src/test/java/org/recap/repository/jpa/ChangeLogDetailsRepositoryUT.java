package org.recap.repository.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.ChangeLogEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created by rajeshbabuk on 18/10/16.
 */
public class ChangeLogDetailsRepositoryUT extends BaseTestCase {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void saveChangeLogEntity() throws Exception {
        ChangeLogEntity changeLogEntity = new ChangeLogEntity();
        changeLogEntity.setUpdatedBy("guest");
        changeLogEntity.setUpdatedDate(new Date());
        changeLogEntity.setOperationType("Test");
        changeLogEntity.setNotes("Test Notes");

        ChangeLogEntity savedChangeLogEntity = changeLogDetailsRepository.save(changeLogEntity);
        entityManager.refresh(savedChangeLogEntity);
        assertNotNull(savedChangeLogEntity);
        assertNotNull(savedChangeLogEntity.getChangeLogId());
    }
}
