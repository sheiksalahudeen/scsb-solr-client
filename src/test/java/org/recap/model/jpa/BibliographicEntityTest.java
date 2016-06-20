package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by pvsubrah on 6/20/16.
 */
public class BibliographicEntityTest extends BaseTestCase {

    @Test
    public void findByInstitutionId() throws Exception {
        List<BibliographicEntity> byInstitutionId = bibliographicDetailsRepository.findByOwningInstitutionId(3);
        assertNotNull(byInstitutionId);
    }

}