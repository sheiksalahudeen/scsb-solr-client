package org.recap.matchingAlgorithm.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.RecapConstants;
import org.recap.model.solr.Bib;
import org.recap.model.solr.Item;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.recap.repository.solr.main.ItemCrudRepository;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Created by premkb on 3/8/16.
 */
public class MatchingAlgorithmHelperServiceUT {

    @InjectMocks
    MatchingAlgorithmHelperService matchingAlgorithmHelperService = new MatchingAlgorithmHelperService();

    @Mock
    public BibSolrCrudRepository bibCrudRepository;

    @Mock
    public ItemCrudRepository itemCrudRepository;

    @Before
    public void setup()throws Exception{
        MockitoAnnotations.initMocks(this);
        when(bibCrudRepository.findByOclcNumber("00 1 614-793-8682")).thenReturn(getBibs());
        when(itemCrudRepository.findByCollectionGroupDesignationAndItemIdIn(RecapConstants.SHARED_CGD, Arrays.asList(1))).thenReturn(Arrays.asList(getItem()));
        when(itemCrudRepository.findByItemId(1)).thenReturn(getItem());
    }

    @Test
    public void testGetBibs(){
        List<Bib> bibs =  matchingAlgorithmHelperService.getBibs("OCLCNumber","00 1 614-793-8682");
        assertNotNull(bibs);
        assertEquals(2,bibs.size());
        assertEquals("1",bibs.get(0).getId());
        assertEquals("SampleTitle",bibs.get(0).getTitle());
        assertEquals("PUL",bibs.get(0).getOwningInstitution());
        assertEquals("BA342",bibs.get(0).getBarcode());
        assertEquals("Imprint",bibs.get(0).getImprint());
    }

    @Test
    public void testGetMatchingReports(){
        Map<String, Set<Bib>> owningInstitutionMap = matchingAlgorithmHelperService.getMatchingBibsBasedOnTitle(getBibs(), new HashSet<>());
        assertNotNull(owningInstitutionMap);
        Set<Bib> bibSet = owningInstitutionMap.get("PUL");
        Bib bib = bibSet.iterator().next();
        assertNotNull("00 1 614-793-8682", bib.getOclcNumber());
        assertNotNull("1", bib.getId());
        assertNotNull("SampleTitle", bib.getTitle());
        assertNotNull("BA352", bib.getBarcode());
    }


    private List<Bib> getBibs(){
        List<Bib> bibs = new ArrayList<>();
        List<Integer> owningInstHoldingsIdList = new ArrayList<>();
        owningInstHoldingsIdList.add(1);
        List<Integer> bibItemIdList = new ArrayList<>();
        bibItemIdList.add(1);

        Bib bib1 = new Bib();
        bib1.setId("1");
        bib1.setBibId(1);
        bib1.setOwningInstitutionBibId("1");
        bib1.setTitle("SampleTitle");
        bib1.setTitleDisplay("SampleTitle");
        bib1.setOclcNumber(Arrays.asList("00 1 614-793-8682"));
        bib1.setOwningInstitution("PUL");
        bib1.setBarcode("BA342");
        bib1.setImprint("Imprint");
        bib1.setOwningInstHoldingsIdList(owningInstHoldingsIdList);
        bib1.setBibItemIdList(bibItemIdList);
        bibs.add(bib1);

        Bib bib2 = new Bib();
        bib2.setId("2");
        bib2.setBibId(2);
        bib2.setOwningInstitutionBibId("2");
        bib2.setTitle("SampleTitle");
        bib2.setTitleDisplay("SampleTitle");
        bib2.setOwningInstitution("PUL");
        bib2.setOclcNumber(Arrays.asList("00 1 614-793-8682"));
        bib2.setBarcode("BA342");
        bib2.setImprint("Imprint");
        bib2.setOwningInstHoldingsIdList(owningInstHoldingsIdList);
        bib2.setBibItemIdList(bibItemIdList);
        bibs.add(bib2);
        return bibs;
    }

    private Item getItem(){
        Item item = new Item();
        item.setId("1");
        item.setItemId(1);
        item.setBarcode("BA352");
        item.setAvailability("Available");
        item.setUseRestriction("Allowed");
        item.setSummaryHoldings("Summary Holding");
        item.setCollectionGroupDesignation("Shared");
        return item;
    }
}
