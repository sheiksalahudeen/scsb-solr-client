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
public class FTPSubmitCollectionsummaryReportRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CSVMatchingRecordRouteBuilder.class);

    @Autowired
    public FTPSubmitCollectionsummaryReportRouteBuilder(CamelContext context,
                                                        @Value("${ftp.userName}") String ftpUserName, @Value("${ftp.submit.collection.pul.report}") String submitCollectionPULFtpLocation,
                                                        @Value("${ftp.submit.collection.cul.report}") String submitCollectionCULFtpLocation,
                                                        @Value("${ftp.submit.collection.nypl.report}") String submitCollectionNYPLFtpLocation,
                                                        @Value("${ftp.submit.collection.report}") String culftpRemoteServer,@Value("${ftp.knownHost}") String ftpKnownHost, @Value("${ftp.privateKey}") String ftpPrivateKey) {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.FTP_SUBMIT_COLLECTION_SUMMARY_REPORT_Q)
                            .routeId(RecapConstants.FTP_SUBMIT_COLLECTION_SUMMARY_REPORT_ID)
                            .marshal().bindy(BindyType.Csv, SubmitCollectionReportRecord.class)
                            .choice()
                                .when(header("fileName").contains(RecapConstants.PRINCETON))
                                    .to("sftp://" + ftpUserName + "@" + submitCollectionPULFtpLocation + "?privateKeyFile=" + ftpPrivateKey + "&knownHostsFile=" + ftpKnownHost + "&fileName=${in.header.fileName}-${date:now:ddMMMyyyyHHmmss}.csv&fileExist=append")
                                .when(header("fileName").contains(RecapConstants.COLUMBIA))
                                    .to("sftp://" + ftpUserName + "@" + submitCollectionCULFtpLocation + "?privateKeyFile=" + ftpPrivateKey + "&knownHostsFile=" + ftpKnownHost + "&fileName=${in.header.fileName}-${date:now:ddMMMyyyyHHmmss}.csv&fileExist=append")
                                .when(header("fileName").contains(RecapConstants.NYPL))
                                    .to("sftp://" + ftpUserName + "@" + submitCollectionCULFtpLocation + "?privateKeyFile=" + ftpPrivateKey + "&knownHostsFile=" + ftpKnownHost + "&fileName=${in.header.fileName}-${date:now:ddMMMyyyyHHmmss}.csv&fileExist=append")
                            ;
                }
            });

        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
    }

}
