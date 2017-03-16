package org.recap.model.solr;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by rajeshbabuk on 13/9/16.
 */
@Ignore
public class HoldingsAT extends BaseTestCase {

    @Before
    public void setUp() throws Exception {
        assertNotNull(holdingsSolrCrudRepository);
        holdingsSolrCrudRepository.deleteAll();
    }

    @Test
    public void indexHoldings() throws Exception {
        Holdings holdings = new Holdings();
        holdings.setHoldingsId(1001);
        holdings.setDocType("Holdings");
        holdings.setSummaryHoldings("Test Summary Holdings Info");
        holdings.setOwningInstitution("NYPL");

        Holdings indexedHoldings = holdingsSolrCrudRepository.save(holdings);
        solrTemplate.softCommit();

        Holdings fetchedHoldings = holdingsSolrCrudRepository.findByHoldingsId(indexedHoldings.getHoldingsId());
        assertNotNull(fetchedHoldings);
        assertEquals(fetchedHoldings.getHoldingsId(), new Integer(1001));
        assertEquals(fetchedHoldings.getDocType(), "Holdings");
        assertEquals(fetchedHoldings.getSummaryHoldings(), "Test Summary Holdings Info");
        assertEquals(fetchedHoldings.getOwningInstitution(), "NYPL");
    }
}
