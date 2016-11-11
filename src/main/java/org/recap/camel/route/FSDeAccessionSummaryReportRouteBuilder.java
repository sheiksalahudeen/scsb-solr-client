package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.recap.RecapConstants;
import org.recap.model.csv.DeAccessionSummaryRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by chenchulakshmig on 13/10/16.
 */
@Component
public class FSDeAccessionSummaryReportRouteBuilder {

    @Autowired
    public FSDeAccessionSummaryReportRouteBuilder(CamelContext context, @Value("${scsb.collection.report.directory}") String reportsDirectory) {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.FS_DE_ACCESSION_SUMMARY_REPORT_Q)
                            .routeId(RecapConstants.FS_DE_ACCESSION_SUMMARY_REPORT_ID)
                            .marshal().bindy(BindyType.Csv, DeAccessionSummaryRecord.class)
                            .to("file:" + reportsDirectory + File.separator + "?fileName=${in.header.fileName}-${date:now:ddMMMyyyyHHmmss}.csv&fileExist=append");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
