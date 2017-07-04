package org.recap.model.solr;

import org.apache.solr.client.solrj.beans.Field;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 8/6/17.
 */
public class BibUT extends BaseTestCase{

    @Test
    public void testBib(){
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
        bib.setId("1");
        bib.setContentType("XML");
        bib.setBibId(101);
        bib.setOwningInstitutionBibId("CU0021524");
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
        bib.setTitleDisplay("test");
        bib.setTitleStartsWith("test");
        bib.setTitleSubFieldA("test");
        bib.setTitleSort("test");
        bib.setLeaderMaterialType("test");
        bib.setBibCreatedBy("Guest");
        bib.setBibCreatedDate(new Date());
        bib.setBibLastUpdatedBy("Guest");
        bib.setBibLastUpdatedDate(new Date());
        bib.setBibHoldingLastUpdatedDate(new Date());
        bib.setBibItemLastUpdatedDate(new Date());
        bib.setDeletedBib(false);
        bib.setBibCatalogingStatus("Incomplete");
        bib.setOwningInstHoldingsIdList(Arrays.asList(1));
        bib.hashCode();
        assertNotNull(bib.getId());
        assertNotNull(bib.getBibId());
        assertNotNull(bib.getDocType());
        assertNotNull(bib.getBarcode());
        assertNotNull(bib.getTitle());
        assertNotNull(bib.getTitleDisplay());
        assertNotNull(bib.getTitleStartsWith());
        assertNotNull(bib.getTitleSubFieldA());
        assertNotNull(bib.getAuthorDisplay());
        assertNotNull(bib.getAuthorSearch());
        assertNotNull(bib.getOwningInstitution());
        assertNotNull(bib.getPublisher());
        assertNotNull(bib.getPublicationPlace());
        assertNotNull(bib.getPublicationDate());
        assertNotNull(bib.getSubject());
        assertNotNull(bib.getIsbn());
        assertNotNull(bib.getIssn());
        assertNotNull(bib.getOclcNumber());
        assertNotNull(bib.getMaterialType());
        assertNotNull(bib.getNotes());
        assertNotNull(bib.getLccn());
        assertNotNull(bib.getImprint());
        assertNotNull(bib.getHoldingsIdList());
        assertNotNull(bib.getOwningInstHoldingsIdList());
        assertNotNull(bib.getBibItemIdList());
        assertNotNull(bib.getOwningInstitutionBibId());
        assertNotNull(bib.getContentType());
        assertNotNull(bib.getLeaderMaterialType());
        assertNotNull(bib.getTitleSort());
        assertNotNull(bib.getBibCreatedBy());
        assertNotNull(bib.getBibCreatedDate());
        assertNotNull(bib.getBibLastUpdatedBy());
        assertNotNull(bib.getBibLastUpdatedDate());
        assertNotNull(bib.getBibHoldingLastUpdatedDate());
        assertNotNull(bib.getBibItemLastUpdatedDate());
        assertNotNull(bib.isDeletedBib());
        assertNotNull(bib.getBibCatalogingStatus());

    }



}