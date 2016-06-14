package org.recap.executors;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.Bib;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by pvsubrah on 6/14/16.
 */
public class ExecutorTest extends BaseTestCase {

    @Autowired
    CoreAdminExecutorService coreAdminExecutorService;


    @Test
    public void indexMultipleBibsWithThreads() throws Exception {

        Bib bib1 = new Bib();
        bib1.setId(1L);
        Bib bib2 = new Bib();
        bib2.setId(2L);
        Bib bib3 = new Bib();
        bib3.setId(3L);

        List<Bib> bibList = asList(bib1, bib2, bib3);

        coreAdminExecutorService.index(3, 1, bibList);

    }

}
