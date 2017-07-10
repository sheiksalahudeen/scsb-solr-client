package org.recap.service.accession.callable;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.accession.AccessionRequest;
import org.recap.service.accession.resolver.BibDataResolver;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 7/7/17.
 */
public class BibDataCallableUT extends BaseTestCase{

    @Autowired
    BibDataCallable bibDataCallable;

    @Test
    public void testBibDataCallaable(){
        bibDataCallable.setAccessionRequest(new AccessionRequest());
        List<BibDataResolver> bibDataResolvers = bibDataCallable.getBibDataResolvers();
        assertNotNull(bibDataResolvers);
        bibDataCallable.setWriteToReport(true);
    }

}