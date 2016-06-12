package org.recap;

import org.junit.Before;
import org.junit.Test;
import org.recap.model.Bib;
import org.recap.repository.BibCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created by pvsubrah on 6/11/16.
 */
public class BibIndexerTest extends BaseTestCase {

    @Autowired
    BibCrudRepository bibCrudRepository;


    @Before
    public void setUp() throws Exception {
        assertNotNull(bibCrudRepository);
    }

    @Test
    public void indexBib() throws Exception {
        Bib bib = new Bib();

        Bib indexedBib = bibCrudRepository.save(bib);

        assertNotNull(indexedBib);

    }
}
