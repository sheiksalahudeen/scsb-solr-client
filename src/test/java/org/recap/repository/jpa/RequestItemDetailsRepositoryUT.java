package org.recap.repository.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.NotesEntity;
import org.recap.model.jpa.RequestItemEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by rajeshbabuk on 28/10/16.
 */
public class RequestItemDetailsRepositoryUT extends BaseTestCase {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void saveRequestItemEntity() throws Exception {
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setItemId(1);
        requestItemEntity.setRequestTypeId(1);
        requestItemEntity.setRequestingInstitutionId(1);
        requestItemEntity.setPatronId(1);
        requestItemEntity.setRequestPosition(99);
        requestItemEntity.setCreatedDate(new Date());
        requestItemEntity.setStopCode("PA");

        NotesEntity notesEntity = new NotesEntity();
        notesEntity.setNotes("Test Notes");

        requestItemEntity.setNotesEntities(Arrays.asList(notesEntity));

        RequestItemEntity savedRequestItemEntity = requestItemDetailsRepository.save(requestItemEntity);
        assertNotNull(savedRequestItemEntity);
        assertNotNull(savedRequestItemEntity.getRequestId());
        assertNotNull(savedRequestItemEntity.getNotesEntities());
        assertTrue(savedRequestItemEntity.getNotesEntities().size() > 0);
    }
}
