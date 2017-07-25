package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.springframework.context.ApplicationContext;
import org.recap.RecapConstants;
import org.recap.camel.processor.EmailService;
import org.recap.model.csv.OngoingAccessionReportRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by premkb on 07/02/17.
 */
@Component
public class FTPOngoingAccessionReportRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(FTPOngoingAccessionReportRouteBuilder.class);

    /**
     * This method instantiates a new route builder to generate ongoing accession report to the FTP.
     *
     * @param context            the context
     * @param ftpUserName        the ftp user name
     * @param ftpRemoteServer    the ftp remote server
     * @param ftpKnownHost       the ftp known host
     * @param ftpPrivateKey      the ftp private key
     * @param applicationContext the application context
     */
    @Autowired
    public FTPOngoingAccessionReportRouteBuilder(CamelContext context,
                                                 @Value("${ftp.userName}") String ftpUserName, @Value("${ftp.ongoing.accession.collection.report}") String ftpRemoteServer,
                                                 @Value("${ftp.knownHost}") String ftpKnownHost, @Value("${ftp.privateKey}") String ftpPrivateKey, ApplicationContext applicationContext) {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.FTP_ONGOING_ACCESSON_REPORT_Q)
                            .routeId(RecapConstants.FTP_ONGOING_ACCESSION_REPORT_ID)
                            .marshal().bindy(BindyType.Csv, OngoingAccessionReportRecord.class)
                            .to("sftp://" + ftpUserName + "@" + ftpRemoteServer + "?privateKeyFile=" + ftpPrivateKey + "&knownHostsFile=" + ftpKnownHost + "&fileName=${in.header.fileName}-${date:now:ddMMMyyyyHHmmss}.csv&fileExist=append")
                            .onCompletion()
                            .bean(applicationContext.getBean(EmailService.class),RecapConstants.ACCESSION_REPORTS_SEND_EMAIL);
                }
            });
        } catch (Exception e) {
            logger.error(RecapConstants.ERROR,e);
        }
    }
}
