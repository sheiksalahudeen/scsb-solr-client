package org.recap;

import org.junit.Before;
import org.junit.Test;
import org.recap.model.Bib;
import org.recap.repository.main.BibCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

/**
 * Created by pvsubrah on 6/11/16.
 */
public class BibIndexerTest extends BaseTestCase {

    @Before
    public void setUp() throws Exception {
        assertNotNull(bibCrudRepository);
        bibCrudRepository.deleteAll();
    }

    @Test
    public void indexBib() throws Exception {
        Bib bib = new Bib();
        bib.setBarcode("101");
        bib.setTitle("Middleware for ReCAP");
        Bib indexedBib = bibCrudRepository.save(bib);

        assertNotNull(indexedBib);

        Bib searchBib = bibCrudRepository.findByBarcode(indexedBib.getBarcode());
        assertNotNull(searchBib);

    }
}
