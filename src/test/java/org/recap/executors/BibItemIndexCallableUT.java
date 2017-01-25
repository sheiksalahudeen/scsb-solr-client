package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.junit.Ignore;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.result.SolrResultPage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by hemalathas on 19/1/17.
 */
public class BibItemIndexCallableUT extends BaseTestCase {

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    SolrTemplate solrTemplate;

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Test
    public void testBibItemIndexCallable() throws Exception{
        int page = 1;
        int size = 1;
        Page<BibliographicEntity> bibliographicEntities = new SolrResultPage<>(getBibliographicEntityList());
        BibItemIndexCallable mockBibItemIndexCallable = new BibItemIndexCallable("","",1,1,bibliographicDetailsRepository,holdingsDetailsRepository,1,new Date(),producerTemplate,solrTemplate);
        //when(bibliographicDetailsRepository.findAll(new PageRequest(page, size))).thenReturn(bibliographicEntities);
        int response = (int) mockBibItemIndexCallable.call();
        assertNotNull(response);
    }

    private List<BibliographicEntity> getBibliographicEntityList(){
        List<BibliographicEntity> bibliographicEntityList = new ArrayList<>();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setBibliographicId(1);
        bibliographicEntity.setContent("marc content".getBytes());
        bibliographicEntity.setOwningInstitutionBibId("1");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntityList.add(bibliographicEntity);
        return bibliographicEntityList;
    }
}