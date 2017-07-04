package org.recap.controller;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 23/2/17.
 */
public class RecapErrorPageControllerUT extends BaseTestCase{

    @Autowired
    RecapErrorPageController recapErrorPageController;

    @Test
    public void testErrorPage(){
        String response = recapErrorPageController.recapErrorPage();
        String path = recapErrorPageController.getErrorPath();
        assertNotNull(response);
        assertEquals(response,"error");
        assertNotNull(path);
        assertEquals(path,"/error");
    }

}