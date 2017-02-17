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
public class FTPSubmitCollectionExceptionReportRouteBuilder {

    Logger logger = LoggerFactory.getLogger(CSVMatchingRecordRouteBuilder.class);

    @Autowired
    public FTPSubmitCollectionExceptionReportRouteBuilder(CamelContext context,
                                                          @Value("${ftp.userName}") String ftpUserName, @Value("${ftp.submit.collection.report}") String ftpRemoteServer,
                                                          @Value("${ftp.knownHost}") String ftpKnownHost, @Value("${ftp.privateKey}") String ftpPrivateKey) {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.FTP_SUBMIT_COLLECTION_EXCEPTION_REPORT_Q)
                            .routeId(RecapConstants.FTP_SUBMIT_COLLECTION_EXCEPTION_REPORT_ID)
                            .marshal().bindy(BindyType.Csv, SubmitCollectionReportRecord.class)
                            .to("sftp://" + ftpUserName + "@" + ftpRemoteServer + "?privateKeyFile=" + ftpPrivateKey + "&knownHostsFile=" + ftpKnownHost + "&fileName=${in.header.fileName}-${date:now:ddMMMyyyyHHmmss}.csv&fileExist=append");
                }
            });
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
    }

}
