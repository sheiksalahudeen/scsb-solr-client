package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.FileUtils;
import org.recap.RecapConstants;
import org.recap.model.camel.EmailPayLoad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * Created by rajeshbabuk on 19/1/17.
 */
@Component
public class EmailRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(EmailRouteBuilder.class);

    private String emailBodyForCgdUpdate;
    private String emailBodyForBatchJob;
    private String emailPassword;

    /**
     * This method instantiates a new route builder to send email.
     *
     * @param context           the context
     * @param username          the username
     * @param passwordDirectory the password directory
     * @param from              the from
     * @param upadteCgdTo       the update cgd to
     * @param batchJobTo        the batch job to
     * @param updateCgdSubject  the update cgd subject
     * @param batchJobSubject   the batch job subject
     * @param smtpServer        the smtp server
     */
    @Autowired
    public EmailRouteBuilder(CamelContext context, @Value("${scsb.email.username}") String username, @Value("${scsb.email.password.file}") String passwordDirectory,
                             @Value("${scsb.email.from}") String from, @Value("${scsb.updateCgd.email.to}") String upadteCgdTo, @Value("${scsb.batch.job.email.to}") String batchJobTo,
                             @Value("${scsb.updateCgd.email.subject}") String updateCgdSubject, @Value("${scsb.batch.job.email.subject}") String batchJobSubject, @Value("${scsb.email.smtpServer}") String smtpServer) {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    loadEmailBodyForCgdUpdateTemplate();
                    loadEmailBodyForBatchJobTemplate();
                    loadEmailPassword();

                    from(RecapConstants.EMAIL_Q)
                            .routeId(RecapConstants.EMAIL_ROUTE_ID)
                            .setHeader("emailPayLoad").body(EmailPayLoad.class)
                            .onCompletion().log("Email has been sent successfully.")
                            .end()
                                .choice()
                                    .when(header(RecapConstants.EMAIL_FOR).isEqualTo(RecapConstants.UPDATECGD))
                                        .setHeader("subject", simple(updateCgdSubject))
                                        .setBody(simple(emailBodyForCgdUpdate))
                                        .setHeader("from", simple(from))
                                        .setHeader("to", simple(upadteCgdTo))
                                        .log("Email for update cgd")
                                        .to("smtps://" + smtpServer + "?username=" + username + "&password=" + emailPassword)
                                    .when(header(RecapConstants.EMAIL_FOR).isEqualTo(RecapConstants.BATCHJOB))
                                        .setHeader("subject", simple(batchJobSubject))
                                        .setBody(simple(emailBodyForBatchJob))
                                        .setHeader("from", simple(from))
                                        .setHeader("to", simple(batchJobTo))
                                        .log("Email for batch job")
                                        .to("smtps://" + smtpServer + "?username=" + username + "&password=" + emailPassword);
                }

                private void loadEmailBodyForCgdUpdateTemplate() {
                    InputStream inputStream = getClass().getResourceAsStream("updateCgd_email_body.vm");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder out = new StringBuilder();
                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            if (line.isEmpty()) {
                                out.append("\n");
                            } else {
                                out.append(line);
                                out.append("\n");
                            }
                        }
                    } catch (IOException e) {
                        logger.error(RecapConstants.LOG_ERROR,e);
                    }
                    emailBodyForCgdUpdate = out.toString();
                }

                private void loadEmailBodyForBatchJobTemplate() {
                    InputStream inputStream = getClass().getResourceAsStream("batchJobEmail.vm");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder out = new StringBuilder();
                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            if (line.isEmpty()) {
                                out.append("\n");
                            } else {
                                out.append(line);
                                out.append("\n");
                            }
                        }
                    } catch (IOException e) {
                        logger.error(RecapConstants.LOG_ERROR,e);
                    }
                    emailBodyForBatchJob = out.toString();
                }

                private void loadEmailPassword() {
                    File file = new File(passwordDirectory);
                    if (file.exists()) {
                        try {
                            emailPassword = FileUtils.readFileToString(file, "UTF-8").trim();
                        } catch (IOException e) {
                            logger.error(RecapConstants.LOG_ERROR,e);
                        }
                    }
                }
            });
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
    }

}
