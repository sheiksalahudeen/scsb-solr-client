package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.solr.Bib;
import org.recap.model.solr.Holdings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

/**
 * Created by hemalathas on 31/10/16.
 */
public class HoldingsRecordSetupCallableUT extends BaseTestCase {

    @Autowired
    ProducerTemplate producerTemplate;

    private String holdingContent = "<collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "            <record>\n" +
            "              <datafield tag=\"852\" ind1=\"0\" ind2=\"1\">\n" +
            "                <subfield code=\"b\">off,che</subfield>\n" +
            "                <subfield code=\"h\">TA434 .S15</subfield>\n" +
            "              </datafield>\n" +
            "              <datafield tag=\"866\" ind1=\"0\" ind2=\"0\">\n" +
            "                <subfield code=\"a\">v.1-16         </subfield>\n" +
            "              </datafield>\n" +
            "            </record>\n" +
            "          </collection>";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void call()throws Exception{
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setHoldingsId(1);
        holdingsEntity.setOwningInstitutionId(3);
        holdingsEntity.setOwningInstitutionHoldingsId("NYPG88-B90418");
        holdingsEntity.setContent(holdingContent.getBytes());
        HoldingsRecordSetupCallable holdingsRecordSetupCallable = new HoldingsRecordSetupCallable(holdingsEntity,producerTemplate);
        Holdings holdings= (Holdings) holdingsRecordSetupCallable.call();
        assertNotNull(holdings);
        assertEquals(new Integer(1),holdings.getHoldingsId());
    }

}