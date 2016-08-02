package org.recap.executors;

import org.junit.Before;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.data.solr.core.query.result.SolrResultPage;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by premkb on 2BibIndexCallable/8/16.
 */
public class BibIndexCallableUT extends BaseTestCase {

    @Test
    public void testBibIndexCallable()throws Exception{
        Page<BibliographicEntity> bibliographicEntities = new SolrResultPage<>(getBibliographicEntityList());
        BibIndexCallable mockBibIndexCallable = mock(BibIndexCallable.class);
        when(mockBibIndexCallable.call()).thenReturn(bibliographicEntities);
        Page<BibliographicEntity> bibliographicEntitieList = (Page<BibliographicEntity>)mockBibIndexCallable.call();
        assertNotNull(bibliographicEntitieList);
        assertEquals(1,bibliographicEntitieList.getTotalPages());
        assertEquals(1,bibliographicEntitieList.getContent().size());
        assertEquals(new Integer(1),bibliographicEntitieList.getContent().get(0).getBibliographicId());
        assertEquals("1",bibliographicEntitieList.getContent().get(0).getOwningInstitutionBibId());
        assertEquals(new Integer(1),bibliographicEntitieList.getContent().get(0).getOwningInstitutionId());

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
