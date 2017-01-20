package org.recap.camel.route;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.camel.EmailPayLoad;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by rajeshbabuk on 19/1/17.
 */
public class EmailRouteBuilderUT extends BaseTestCase {

    @Autowired
    ProducerTemplate producerTemplate;

    @Test
    public void testSendEmail() throws Exception {
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.setItemBarcode("123");
        emailPayLoad.setItemInstitution("PUL");
        emailPayLoad.setOldCgd("Shared");
        emailPayLoad.setNewCgd("Open");
        emailPayLoad.setNotes("Update Shared to Open");
        producerTemplate.sendBody(RecapConstants.EMAIL_Q, emailPayLoad);
    }
}
