package org.recap.model.solr;

import org.junit.Before;
import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by rajeshbabuk on 13/9/16.
 */
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

        Holdings indexedHoldings = holdingsSolrCrudRepository.save(holdings);
        assertNotNull(indexedHoldings);
        assertEquals(indexedHoldings.getHoldingsId(),new Integer(1001));
        assertEquals(indexedHoldings.getDocType(),"Holdings");
        assertEquals(indexedHoldings.getSummaryHoldings(),"Test Summary Holdings Info");
    }
}
