package org.recap.model.solr;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.util.BibJSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BibAT extends BaseTestCase {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    SolrTemplate solrTemplate;

    @Before
    public void setUp() throws Exception {
        assertNotNull(bibCrudRepository);
        bibCrudRepository.deleteAll();
    }

    @Test
    public void indexBib() throws Exception {

        List<String> issnList = new ArrayList<>();
        List<String>isbnList = new ArrayList<>();
        List<String> oclcNumberList = new ArrayList<>();
        List<Integer> holdingsIdList = new ArrayList<>();
        List<Integer> itemIdList = new ArrayList<>();
        issnList.add("0394469756");
        isbnList.add("0394469755");
        oclcNumberList.add("00133182");
        oclcNumberList.add("00440790");
        holdingsIdList.add(201);
        holdingsIdList.add(202);
        itemIdList.add(301);
        itemIdList.add(302);

        Bib bib = new Bib();
        bib.setBibId(101);
        bib.setDocType("Bib");
        bib.setTitle("Middleware for ReCAP");
        bib.setBarcode("1");
        bib.setTitle("Test Bib 1");
        bib.setAuthorDisplay("Hoepli, Nancy L");
        bib.setAuthorSearch(Arrays.asList("Hoepli, Nancy L", "Ibn Jubayr"));
        bib.setPublisher("McClelland & Stewart, limited");
        bib.setImprint("Toronto, McClelland & Stewart, limited [c1926]");
        bib.setIssn(issnList);
        bib.setIsbn(isbnList);
        bib.setOclcNumber(oclcNumberList);
        bib.setPublicationDate("1960");
        bib.setMaterialType("Material Type 1");
        bib.setNotes("Bibliographical footnotes 1");
        bib.setOwningInstitution("PUL");
        bib.setSubject("Arab countries Politics and government.");
        bib.setPublicationPlace("Paris");
        bib.setLccn("71448228");
        bib.setHoldingsIdList(holdingsIdList);
        bib.setBibItemIdList(itemIdList);
        Bib indexedBib = bibCrudRepository.save(bib);
        assertNotNull(indexedBib);

        assertTrue(indexedBib.getIssn().get(0).equals("0394469756"));
        assertTrue(indexedBib.getIsbn().get(0).equals("0394469755"));
        assertTrue(indexedBib.getOclcNumber().get(0).equals("00133182"));
        assertTrue(indexedBib.getHoldingsIdList().equals(holdingsIdList));
        assertTrue(indexedBib.getBibItemIdList().equals(itemIdList));
        assertEquals(indexedBib.getBibId(),new Integer(101));
        assertEquals(indexedBib.getDocType(),"Bib");
        assertEquals(indexedBib.getTitle(),"Test Bib 1");
        assertEquals(indexedBib.getBarcode(),"1");
        assertEquals(indexedBib.getTitle(),"Test Bib 1");
        assertEquals(indexedBib.getAuthorDisplay(),"Hoepli, Nancy L");
        assertEquals(indexedBib.getAuthorSearch().get(0),"Hoepli, Nancy L");
        assertEquals(indexedBib.getAuthorSearch().get(1),"Ibn Jubayr");
        assertEquals(indexedBib.getPublisher(),"McClelland & Stewart, limited");
        assertEquals(indexedBib.getImprint(),"Toronto, McClelland & Stewart, limited [c1926]");
        assertEquals(indexedBib.getPublicationDate(),"1960");
        assertEquals(indexedBib.getMaterialType(),"Material Type 1");
        assertEquals(indexedBib.getNotes(),"Bibliographical footnotes 1");
        assertEquals(indexedBib.getOwningInstitution(),"PUL");
        assertEquals(indexedBib.getSubject(),"Arab countries Politics and government.");
        assertEquals(indexedBib.getPublicationPlace(),"Paris");
        assertEquals(indexedBib.getLccn(),"71448228");
    }

    @Test
    public void saveBibAndIndex() throws Exception {
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

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("etl");
        String barcode = "123";
        itemEntity.setBarcode(barcode);
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("1");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntity(holdingsEntity);

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity);

        BibliographicEntity fetchedBibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(1, owningInstitutionBibId);
        assertNotNull(fetchedBibliographicEntity);
        assertEquals(owningInstitutionBibId, fetchedBibliographicEntity.getOwningInstitutionBibId());

        Map<String, List> stringListMap = new BibJSONUtil().generateBibAndItemsForIndex(fetchedBibliographicEntity);
        List<Bib> bibs = stringListMap.get("Bib");
        assertNotNull(bibs);
        assertTrue(bibs.size() == 1);

        List<Item> items = stringListMap.get("Item");
        assertNotNull(items);
        assertTrue(items.size() == 1);

        bibCrudRepository.save(bibs);
        itemCrudRepository.save(items);
        solrTemplate.softCommit();

        Integer bibId = bibs.get(0).getBibId();
        Bib bib = bibCrudRepository.findByBibId(bibId);
        assertNotNull(bib);
        assertEquals(owningInstitutionBibId, bib.getOwningInstitutionBibId());

        Integer itemId = bib.getBibItemIdList().get(0);
        Item item = itemCrudRepository.findByItemId(itemId);
        assertNotNull(item);
        assertEquals(barcode, item.getBarcode());
        assertNull(item.getUseRestriction());
        solrTemplate.rollback();
    }

    public File getUnicodeContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("BibContent.xml");
        return new File(resource.toURI());
    }


}
