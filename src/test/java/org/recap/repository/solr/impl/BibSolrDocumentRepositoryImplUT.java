package org.recap.repository.solr.impl;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.search.resolver.BibValueResolver;
import org.recap.model.search.resolver.HoldingsValueResolver;
import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.solr.BibItem;
import org.recap.model.solr.Holdings;
import org.recap.model.solr.Item;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 22/2/17.
 */
public class BibSolrDocumentRepositoryImplUT extends BaseTestCase{

    @Autowired
    BibSolrDocumentRepositoryImpl bibSolrDocumentRepository;

    @Test
    public void populateBibItem() {
        BibItem bibItem = new BibItem();
        Map<String,Object> map = bibValueResolvers();
        for (Map.Entry<String,Object> entry : map.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            for (Iterator<BibValueResolver> valueResolverIterator = bibSolrDocumentRepository.getBibValueResolvers().iterator(); valueResolverIterator.hasNext(); ) {
                assertNotNull(valueResolverIterator);
                BibValueResolver valueResolver = valueResolverIterator.next();
                if (valueResolver.isInterested(fieldName)) {
                    valueResolver.setValue(bibItem, fieldValue);
                }
            }
        }
    }

    @Test
    public void populateItem(){
        Item item = new Item();
        Map<String,Object> map = itemValueResolvers();
        for (Map.Entry<String,Object> entry : map.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            for (Iterator<ItemValueResolver> itemValueResolverIterator = bibSolrDocumentRepository.getItemValueResolvers().iterator(); itemValueResolverIterator.hasNext(); ) {
                assertNotNull(itemValueResolverIterator);
                ItemValueResolver itemValueResolver = itemValueResolverIterator.next();
                if (itemValueResolver.isInterested(fieldName)) {
                    itemValueResolver.setValue(item, fieldValue);
                }
            }
        }
    }

    @Test
    public void testHoldings(){
        Holdings holdings = new Holdings();
        Map<String,Object> map = holdingValueResolvers();
        for (Map.Entry<String,Object> entry : map.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            for (Iterator<HoldingsValueResolver> holdingsValueResolverIterator = bibSolrDocumentRepository.getHoldingsValueResolvers().iterator(); holdingsValueResolverIterator.hasNext(); ) {
                assertNotNull(holdingsValueResolverIterator);
                HoldingsValueResolver holdingsValueResolver = holdingsValueResolverIterator.next();
                if(holdingsValueResolver.isInterested(fieldName)) {
                    holdingsValueResolver.setValue(holdings, fieldValue);
                }
            }
        }

    }


    public Map bibValueResolvers(){
        Map<String,Object> bibResolverMap = new HashMap<>();
        bibResolverMap.put("_root_","root");
        bibResolverMap.put("Author_display","John");
        bibResolverMap.put("Author_search",Arrays.asList("Test"));
        bibResolverMap.put("BibId",1);
        bibResolverMap.put("DocType","Barcode");
        bibResolverMap.put("id","1");
        bibResolverMap.put("Imprint","test");
        bibResolverMap.put("ISBN",Arrays.asList("001"));
        bibResolverMap.put("ISSN",Arrays.asList("123"));
        bibResolverMap.put("LCCN","003");
        bibResolverMap.put("LeaderMaterialType","Others");
        bibResolverMap.put("MaterialType","Others");
        bibResolverMap.put("notes","test");
        bibResolverMap.put("OCLCNumber",Arrays.asList("123"));
        bibResolverMap.put("OwningInstitutionBibId","12345");
        bibResolverMap.put("BibOwningInstitution","PUL");
        bibResolverMap.put("PublicationDate",new Date().toString());
        bibResolverMap.put("PublicationPlace","test");
        bibResolverMap.put("Publisher","test");
        bibResolverMap.put("Subject","test");
        bibResolverMap.put("Title_display","test");
        bibResolverMap.put("Title_search","test");
        bibResolverMap.put("Title_sort","test");
        bibResolverMap.put(RecapConstants.IS_DELETED_BIB,false);
        return bibResolverMap;
    }

    public Map itemValueResolvers(){
        Map<String,Object> itemResolverMap = new HashMap<>();
        itemResolverMap.put("Availability_search","Test");
        itemResolverMap.put("Availability_display","Test");
        itemResolverMap.put("Barcode","456321");
        itemResolverMap.put("CallNumber_search","search");
        itemResolverMap.put("CallNumber_display","Test");
        itemResolverMap.put("CollectionGroupDesignation","Open");
        itemResolverMap.put("CustomerCode","PB");
        itemResolverMap.put("DocType","itemID");
        itemResolverMap.put("ItemOwningInstitution","PUL");
        itemResolverMap.put("UseRestriction_search","No Restriction");
        itemResolverMap.put("UseRestriction_display","No Restriction");
        itemResolverMap.put("VolumePartYear","2016");
        itemResolverMap.put("_root_","root");
        itemResolverMap.put("ItemId",1);
        itemResolverMap.put("id","1236598");
        itemResolverMap.put(RecapConstants.IS_DELETED_ITEM,false);
        return itemResolverMap;
    }

    public Map holdingValueResolvers(){
        Map<String,Object> holdingResolverMap = new HashMap<>();
        holdingResolverMap.put("_root_","root");
        holdingResolverMap.put("SummaryHoldings","Test");
        holdingResolverMap.put("DocType","holdingId");
        holdingResolverMap.put("id","565456456");
        holdingResolverMap.put("HoldingsId",534541);
        holdingResolverMap.put(RecapConstants.IS_DELETED_HOLDINGS,false);
        return holdingResolverMap;
    }

}