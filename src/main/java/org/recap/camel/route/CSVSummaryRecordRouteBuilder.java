package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.recap.RecapConstants;
import org.recap.model.csv.SummaryReportReCAPCSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by angelind on 31/8/16.
 */

@Component
public class CSVSummaryRecordRouteBuilder {

    @Autowired
    public CSVSummaryRecordRouteBuilder(CamelContext context, @Value("${solr.report.directory}") String matchingReportsDirectory) {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.CSV_SUMMARY_ALGO_REPORT_Q)
                            .routeId(RecapConstants.CSV_SUMMARY_ALGO_REPORT_ROUTE_ID)
                            .marshal().bindy(BindyType.Csv, SummaryReportReCAPCSVRecord.class)
                            .to("file:" + matchingReportsDirectory + File.separator + "?fileName=${in.header.fileName}-${date:now:ddMMMyyyy}.csv")
                            .onCompletion().log("File has been created successfully.");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
