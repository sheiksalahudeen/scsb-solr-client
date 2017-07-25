package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.recap.RecapConstants;
import org.recap.camel.processor.EmailService;
import org.recap.camel.processor.StopRouteProcessor;
import org.recap.model.matchingReports.MatchingSerialAndMVMReports;
import org.recap.model.matchingReports.MatchingSummaryReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by angelind on 22/6/17.
 */
@Component
public class FTPMatchingReportsRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(FTPMatchingReportsRouteBuilder.class);

    /**
     * Instantiates a new Ftp matching reports route builder.
     *
     * @param camelContext                the camel context
     * @param matchingReportsDirectory    the matching reports directory
     * @param ftpMatchingReportsDirectory the ftp matching reports directory
     * @param ftpUserName                 the ftp user name
     * @param ftpPrivateKey               the ftp private key
     * @param ftpKnownHost                the ftp known host
     * @param applicationContext          the application context
     */
    public FTPMatchingReportsRouteBuilder(CamelContext camelContext, @Value("${ongoing.matching.report.directory}") String matchingReportsDirectory,
                                          @Value("${ftp.matchingAlgorithm.remote.server}") String ftpMatchingReportsDirectory, @Value("${ftp.userName}") String ftpUserName,
                                          @Value("${ftp.privateKey}") String ftpPrivateKey, @Value("${ftp.knownHost}") String ftpKnownHost, ApplicationContext applicationContext) {
        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.FILE + matchingReportsDirectory + RecapConstants.DELETE_FILE_OPTION)
                            .routeId(RecapConstants.FTP_TITLE_EXCEPTION_REPORT_ROUTE_ID)
                            .noAutoStartup()
                            .to(RecapConstants.SFTP+ ftpUserName +  RecapConstants.AT + ftpMatchingReportsDirectory + RecapConstants.PRIVATE_KEY_FILE + ftpPrivateKey + RecapConstants.KNOWN_HOST_FILE + ftpKnownHost)
                            .onCompletion()
                            .process(new StopRouteProcessor(RecapConstants.FTP_TITLE_EXCEPTION_REPORT_ROUTE_ID))
                            .log("Title_Exception report generated and uploaded to ftp successfully.");
                }
            });
        } catch (Exception e) {
            logger.info(RecapConstants.LOG_ERROR+e);
        }

        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.FTP_SERIAL_MVM_REPORT_Q)
                            .routeId(RecapConstants.FTP_SERIAL_MVM_REPORT_ROUTE_ID)
                            .noAutoStartup()
                            .marshal().bindy(BindyType.Csv, MatchingSerialAndMVMReports.class)
                            .to(RecapConstants.SFTP+ ftpUserName +  RecapConstants.AT + ftpMatchingReportsDirectory + RecapConstants.PRIVATE_KEY_FILE + ftpPrivateKey + RecapConstants.KNOWN_HOST_FILE + ftpKnownHost + "&fileName=${in.header.fileName}_${date:now:yyyyMMdd_HHmmss}.csv")
                            .onCompletion()
                            .process(new StopRouteProcessor(RecapConstants.FTP_SERIAL_MVM_REPORT_ROUTE_ID))
                            .log("Matching Serial_MVM reports generated and uploaded to ftp successfully.");

                }
            });
        } catch (Exception e) {
            logger.info(RecapConstants.LOG_ERROR+e);
        }

        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.FTP_MATCHING_SUMMARY_REPORT_Q)
                            .routeId(RecapConstants.FTP_MATCHING_SUMMARY_REPORT_ROUTE_ID)
                            .noAutoStartup()
                            .marshal().bindy(BindyType.Csv, MatchingSummaryReport.class)
                            .to(RecapConstants.SFTP+ ftpUserName +  RecapConstants.AT + ftpMatchingReportsDirectory + RecapConstants.PRIVATE_KEY_FILE + ftpPrivateKey + RecapConstants.KNOWN_HOST_FILE + ftpKnownHost + "&fileName=${in.header.fileName}_${date:now:yyyyMMdd_HHmmss}.csv")
                            .onCompletion()
                            .bean(applicationContext.getBean(EmailService.class),RecapConstants.MATCHING_REPORTS_SEND_EMAIL)
                            .process(new StopRouteProcessor(RecapConstants.FTP_MATCHING_SUMMARY_REPORT_ROUTE_ID))
                            .log("Matching Summary reports generated and uploaded to ftp successfully.")
                            .end();

                }
            });
        } catch (Exception e) {
            logger.info(RecapConstants.LOG_ERROR+e);
        }
    }
}
