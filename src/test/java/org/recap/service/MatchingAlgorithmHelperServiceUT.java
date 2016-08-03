package org.recap.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.model.solr.Bib;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

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
    }

    @Test
    public void testGetBibs(){
        List<Bib> bibs =  matchingAlgorithmHelperService.getBibs("OCLCNumber","00 1 614-793-8682");
        assertNotNull(bibs);
        assertEquals(1,bibs.size());
        assertEquals("1",bibs.get(0).getId());
        assertEquals("SampleTitle",bibs.get(0).getTitle());
        assertEquals("PUL",bibs.get(0).getOwningInstitution());
        assertEquals("BA342",bibs.get(0).getBarcode());
        assertEquals("Imprint",bibs.get(0).getImprint());
    }



    private List<Bib> getBibs(){
        List<Bib> bibs = new ArrayList<>();
        Bib bib = new Bib();
        bib.setId("1");
        bib.setBibId(1);
        bib.setOwningInstitutionBibId("1");
        bib.setTitle("SampleTitle");
        bib.setOwningInstitution("PUL");
        bib.setBarcode("BA342");
        bib.setImprint("Imprint");
        List<Integer> owningInstHoldingsIdList = new ArrayList<>();
        owningInstHoldingsIdList.add(1);
        bib.setOwningInstHoldingsIdList(owningInstHoldingsIdList);
        bibs.add(bib);
        return bibs;
    }
}
