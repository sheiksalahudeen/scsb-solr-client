package org.recap.controller;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.model.camel.EmailPayLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by rajeshbabuk on 10/4/17.
 */
@RestController
@RequestMapping("/batchJobEmailService")
public class BatchJobEmailController {

    @Autowired
    private ProducerTemplate producerTemplate;

    /**
     * This method is used to send email on successful completion of batch job.
     *
     * @param emailPayLoad the email pay load
     * @return the string
     */
    @RequestMapping(value="/batchJobEmail", method = RequestMethod.POST)
    public String batchJobSendEmail(@RequestBody EmailPayLoad emailPayLoad) {
        producerTemplate.sendBodyAndHeader(RecapConstants.EMAIL_Q, emailPayLoad, RecapConstants.EMAIL_FOR, RecapConstants.BATCHJOB);
        return RecapConstants.SUCCESS;
    }
}
