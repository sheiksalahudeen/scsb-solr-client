package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.recap.RecapConstants;
import org.recap.model.csv.MatchingReportReCAPCSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by angelind on 23/8/16.
 */
@Component
public class FTPMatchingRecordRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(FTPMatchingRecordRouteBuilder.class);

    /**
     * This method instantiates a new route builder to generate matching records from the matching algorithm to the FTP.
     *
     * @param context         the context
     * @param ftpUserName     the ftp user name
     * @param ftpRemoteServer the ftp remote server
     * @param ftpKnownHost    the ftp known host
     * @param ftpPrivateKey   the ftp private key
     */
    @Autowired
    public FTPMatchingRecordRouteBuilder(CamelContext context,
                                          @Value("${ftp.userName}") String ftpUserName, @Value("${ftp.matchingAlgorithm.remote.server}") String ftpRemoteServer,
                                          @Value("${ftp.knownHost}") String ftpKnownHost, @Value("${ftp.privateKey}") String ftpPrivateKey) {

        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.FTP_MATCHING_ALGO_REPORT_Q)
                            .routeId(RecapConstants.FTP_MATCHING_ALGO_REPORT_ROUTE_ID)
                            .marshal().bindy(BindyType.Csv, MatchingReportReCAPCSVRecord.class)
                            .to("sftp://" + ftpUserName + "@" + ftpRemoteServer + "?privateKeyFile=" + ftpPrivateKey + "&knownHostsFile=" + ftpKnownHost + "&fileName=${in.header.fileName}-${date:now:ddMMMyyyy}.csv")
                            .onCompletion().log("File has been uploaded to ftp successfully.");
                }
            });
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
    }
}
