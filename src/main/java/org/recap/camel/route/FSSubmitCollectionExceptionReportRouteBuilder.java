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

import java.io.File;

/**
 * Created by hemalathas on 20/12/16.
 */
@Component
public class FSSubmitCollectionExceptionReportRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(FSSubmitCollectionExceptionReportRouteBuilder.class);

    /**
     * This method instantiates a new route builder to generate submit collection exception report to the file system.
     *
     * @param context          the context
     * @param reportsDirectory the reports directory
     */
    @Autowired
    public FSSubmitCollectionExceptionReportRouteBuilder(CamelContext context, @Value("${submit.collection.report.directory}") String reportsDirectory) {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.FS_SUBMIT_COLLECTION_EXCEPTION_REPORT_Q)
                            .routeId(RecapConstants.FS_SUBMIT_COLLECTION_EXCEPTION_REPORT_ID)
                            .marshal().bindy(BindyType.Csv, SubmitCollectionReportRecord.class)
                            .to("file:" + reportsDirectory + File.separator + "?fileName=${in.header.fileName}-${date:now:ddMMMyyyyHHmmss}.csv&fileExist=append");
                }
            });
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
    }
}
