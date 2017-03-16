package org.recap.model.solr;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Ignore;
import org.junit.Test;
import org.marc4j.marc.Record;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.recap.repository.solr.temp.BibCrudRepositoryMultiCoreSupport;
import org.recap.util.BibJSONUtil;
import org.recap.util.MarcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.SolrCrudRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * Created by rajeshbabuk on 5/7/16.
 */
@Ignore
public class SolrUnicodeAT extends BaseTestCase {

    @Value("${solr.url}")
    String solrUrl;

    private BibCrudRepositoryMultiCoreSupport bibCrudRepositoryMultiCoreSupport;

    private SolrCrudRepository solrCrudRepository;

    @Autowired
    SolrTemplate solrTemplate;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    BibSolrCrudRepository bibSolrCrudRepository;

    @Autowired
    ItemCrudRepository itemCrudRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void fetchUnicodeBibRecordSaveAndMatchWithSolr() throws Exception {
        Random random = new Random();
        File bibContentFile = getUnicodeContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(sourceBibContent.getBytes());
        bibliographicEntity.setOwningInstitutionId(1);
        String owningInstitutionBibId = String.valueOf(random.nextInt());
        bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setLastUpdatedBy("tst");

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity);

        BibliographicEntity fetchedBibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibIdAndIsDeletedFalse(1, owningInstitutionBibId);
        assertNotNull(fetchedBibliographicEntity);
        assertNotNull(fetchedBibliographicEntity.getContent());

        String fetchedBibContent = new String(fetchedBibliographicEntity.getContent());
        assertEquals(sourceBibContent, fetchedBibContent);

        SolrInputDocument solrInputDocument = new BibJSONUtil().generateBibAndItemsForIndex(fetchedBibliographicEntity, solrTemplate, bibliographicDetailsRepository, holdingsDetailsRepository);
        assertNotNull(solrInputDocument);

        //bibSolrCrudRepository = new BibCrudRepositoryMultiCoreSupport("recap", solrUrl);
        solrTemplate.saveDocument(solrInputDocument);
        Bib solrBib = bibSolrCrudRepository.findByBibId(fetchedBibliographicEntity.getBibliographicId());
        assertNotNull(solrBib);

        String solrTitle = solrBib.getTitle();
        assertNotNull(solrTitle);

        MarcUtil marcUtil = new MarcUtil();
        List<Record> records = marcUtil.convertMarcXmlToRecord(sourceBibContent);
        assertNotNull(records);
        assertTrue(records.size() > 0);

        String sourceTitle = marcUtil.getDataFieldValueStartsWith(records.get(0), "24", Arrays.asList('a', 'b'));
        assertNotNull(sourceTitle);

        assertEquals(sourceTitle.trim(), solrTitle.trim());

        deleteByDocId("BibId",savedBibliographicEntity.getBibliographicId().toString());
        deleteByDocId("HoldingId",savedBibliographicEntity.getHoldingsEntities().get(0).getHoldingsId().toString());
        deleteByDocId("ItemId",savedBibliographicEntity.getItemEntities().get(0).getItemId().toString());
    }

    public File getUnicodeContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("UnicodeBibContent.xml");
        return new File(resource.toURI());
    }

    public void deleteByDocId(String docIdParam, String docIdValue) throws IOException, SolrServerException {
        UpdateResponse updateResponse = solrTemplate.getSolrClient().deleteByQuery(docIdParam+":"+docIdValue);
        solrTemplate.commit();
    }
}
