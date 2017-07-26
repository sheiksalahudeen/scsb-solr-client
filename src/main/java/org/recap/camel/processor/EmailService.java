package org.recap.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.camel.EmailPayLoad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Created by angelind on 25/7/17.
 */
@Service
@Scope("prototype")
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private ProducerTemplate producerTemplate;

    private String institutionCode;

    @Value("${matching.reports.email.pul.to}")
    private String matchingPulEmailTo;

    @Value("${matching.reports.email.cul.to}")
    private String matchingCulEmailTo;

    @Value("${matching.reports.email.nypl.to}")
    private String matchingNyplEmailTo;

    @Value("${accession.reports.email.pul.to}")
    private String accessionPulEmailTo;

    @Value("${accession.reports.email.cul.to}")
    private String accessionCulEmailTo;

    @Value("${accession.reports.email.nypl.to}")
    private String accessionNyplEmailTo;

    @Value("${recap.assist.email.to}")
    private String recapSupportEmailTo;

    /**
     * Instantiates a new Email service.
     */
    public EmailService() {
    }

    /**
     * Gets institution code.
     *
     * @return the institution code
     */
    public String getInstitutionCode() {
        return institutionCode;
    }

    /**
     * Sets institution code.
     *
     * @param institutionCode the institution code
     */
    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    /**
     * Send email for matching reports.
     *
     * @param exchange the exchange
     */
    public void sendEmailForMatchingReports(Exchange exchange) {
        logger.info("matching algorithm reports email started ");
        producerTemplate.sendBodyAndHeader(RecapConstants.EMAIL_Q, getEmailPayLoadForMatching(exchange), RecapConstants.EMAIL_FOR, RecapConstants.MATCHING_REPORTS);
    }

    /**
     * Send email for accession reports.
     *
     * @param exchange the exchange
     */
    public void sendEmailForAccessionReports(Exchange exchange) {
        logger.info("accession reports email started ");
        producerTemplate.sendBodyAndHeader(RecapConstants.EMAIL_Q, getEmailPayLoadForAccession(exchange), RecapConstants.EMAIL_FOR, RecapConstants.ACCESSION_REPORTS);
    }

    /**
     * Get email pay load for matching email pay load.
     *
     * @param exchange the exchange
     * @return the email pay load
     */
    public EmailPayLoad getEmailPayLoadForMatching(Exchange exchange){
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        String fileNameWithPath = (String)exchange.getIn().getHeader("CamelFileNameProduced");
        File file = FileUtils.getFile(fileNameWithPath);
        String path = file.getParent();
        emailPayLoad.setTo(recapSupportEmailTo);
        getCc(emailPayLoad);
        emailPayLoad.setMessage("The Reports for Matching Algorithm is available at the FTP location " + path);
        logger.info("Matching Algorithm Reports email has been sent to : {} and cc : {} ",emailPayLoad.getTo(),emailPayLoad.getCc());
        return emailPayLoad;
    }

    /**
     * Get email pay load for accession email pay load.
     *
     * @param exchange the exchange
     * @return the email pay load
     */
    public EmailPayLoad getEmailPayLoadForAccession(Exchange exchange){
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        String fileNameWithPath = (String)exchange.getIn().getHeader("CamelFileNameProduced");
        institutionCode = (String) exchange.getIn().getHeader(RecapConstants.INSTITUTION_NAME);
        File file = FileUtils.getFile(fileNameWithPath);
        String absolutePath = file.getParent();
        String fileName = file.getName();
        emailPayLoad.setTo(recapSupportEmailTo);
        getCcBasedOnInstitution(emailPayLoad);
        emailPayLoad.setMessage("The Report " + fileName + " is available at the FTP location " + absolutePath);
        logger.info("Accession Reports email has been sent to : {} and cc : {} ",emailPayLoad.getTo(),emailPayLoad.getCc());
        return emailPayLoad;
    }

    private void getCc(EmailPayLoad emailPayLoad) {
        StringBuilder cc = new StringBuilder();
        cc.append(StringUtils.isNotBlank(matchingPulEmailTo) ? matchingPulEmailTo + "," : "");
        cc.append(StringUtils.isNotBlank(matchingCulEmailTo) ? matchingCulEmailTo + "," : "");
        cc.append(StringUtils.isNotBlank(matchingNyplEmailTo) ? matchingNyplEmailTo : "");
        emailPayLoad.setCc(cc.toString());
    }

    private void getCcBasedOnInstitution(EmailPayLoad emailPayLoad) {
        if (RecapConstants.NYPL.equalsIgnoreCase(institutionCode)) {
            emailPayLoad.setCc(accessionNyplEmailTo);
        } else if (RecapConstants.COLUMBIA.equalsIgnoreCase(institutionCode)) {
            emailPayLoad.setCc(accessionCulEmailTo);
        } else if (RecapConstants.PRINCETON.equalsIgnoreCase(institutionCode)) {
            emailPayLoad.setCc(accessionPulEmailTo);
        }
    }
}
