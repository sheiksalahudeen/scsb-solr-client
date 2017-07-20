package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.recap.RecapConstants;
import org.recap.model.csv.SubmitCollectionReportRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by hemalathas on 21/12/16.
 */
@Component
public class FTPSubmitCollectionSummaryReportRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(FTPSubmitCollectionSummaryReportRouteBuilder.class);

    /**
     * This method instantiates a new route builder to generate submit collection summary report to the FTP.
     *
     * @param context                         the context
     * @param ftpUserName                     the ftp user name
     * @param submitCollectionPULFtpLocation  the submit collection pul ftp location
     * @param submitCollectionCULFtpLocation  the submit collection cul ftp location
     * @param submitCollectionNYPLFtpLocation the submit collection nypl ftp location
     * @param ftpKnownHost                    the ftp known host
     * @param ftpPrivateKey                   the ftp private key
     */
    @Autowired
    public FTPSubmitCollectionSummaryReportRouteBuilder(CamelContext context,
                                                        @Value("${ftp.userName}") String ftpUserName, @Value("${ftp.submit.collection.report}") String submitCollectionFtpLocation,
                                                        @Value("${ftp.submit.collection.pul.report}") String submitCollectionPULFtpLocation,
                                                        @Value("${ftp.submit.collection.cul.report}") String submitCollectionCULFtpLocation,
                                                        @Value("${ftp.submit.collection.nypl.report}") String submitCollectionNYPLFtpLocation,
                                                        @Value("${ftp.knownHost}") String ftpKnownHost, @Value("${ftp.privateKey}") String ftpPrivateKey) {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.FTP_SUBMIT_COLLECTION_SUMMARY_REPORT_Q)
                            .routeId(RecapConstants.FTP_SUBMIT_COLLECTION_SUMMARY_REPORT_ID)
                            .marshal().bindy(BindyType.Csv, SubmitCollectionReportRecord.class)
                            .choice()
                                .when(header(RecapConstants.FILE_NAME).contains(RecapConstants.PRINCETON))
                                    .to(RecapConstants.SFTP + ftpUserName + RecapConstants.AT + submitCollectionPULFtpLocation + RecapConstants.PRIVATE_KEY_FILE + ftpPrivateKey + RecapConstants.KNOWN_HOST_FILE + ftpKnownHost + RecapConstants.SUBMIT_COLLECTION_REPORT_SFTP_OPTIONS)
                                .when(header(RecapConstants.FILE_NAME).contains(RecapConstants.COLUMBIA))
                                    .to(RecapConstants.SFTP + ftpUserName + RecapConstants.AT + submitCollectionCULFtpLocation + RecapConstants.PRIVATE_KEY_FILE + ftpPrivateKey + RecapConstants.KNOWN_HOST_FILE + ftpKnownHost + RecapConstants.SUBMIT_COLLECTION_REPORT_SFTP_OPTIONS)
                                .when(header(RecapConstants.FILE_NAME).contains(RecapConstants.NYPL))
                                    .to(RecapConstants.SFTP + ftpUserName + RecapConstants.AT + submitCollectionNYPLFtpLocation + RecapConstants.PRIVATE_KEY_FILE + ftpPrivateKey + RecapConstants.KNOWN_HOST_FILE + ftpKnownHost + RecapConstants.SUBMIT_COLLECTION_REPORT_SFTP_OPTIONS)
                                .otherwise()
                                    .to(RecapConstants.SFTP + ftpUserName + RecapConstants.AT + submitCollectionFtpLocation + RecapConstants.PRIVATE_KEY_FILE + ftpPrivateKey + RecapConstants.KNOWN_HOST_FILE + ftpKnownHost + RecapConstants.SUBMIT_COLLECTION_REPORT_SFTP_OPTIONS);

                }
            });

        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
    }

}
