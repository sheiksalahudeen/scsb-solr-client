package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * Created by pvsubrah on 6/20/16.
 */
public class BibliographicEntityTest extends BaseTestCase {

    @Test
    public void findByInstitutionId() throws Exception {
        Long count = bibliographicDetailsRepository.countByOwningInstitutionId(3);
        assertNotNull(count);
        assertTrue(count == 3);
    }

    @Test
    public void findByInstitutionIdPagable() throws Exception {
        Page<BibliographicEntity> bibliographicEntities = bibliographicDetailsRepository.findByOwningInstitutionId(new PageRequest(0, 3), 3);
        assertNotNull(bibliographicEntities);
        assertTrue(bibliographicEntities.getTotalElements() == 3);
    }

}