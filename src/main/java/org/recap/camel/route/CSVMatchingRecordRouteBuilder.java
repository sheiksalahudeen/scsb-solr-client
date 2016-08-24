package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.recap.RecapConstants;
import org.recap.model.csv.ReCAPCSVMatchingRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by angelind on 22/8/16.
 */

@Component
public class CSVMatchingRecordRouteBuilder {

    @Autowired
    public CSVMatchingRecordRouteBuilder(CamelContext context, @Value("${solr.report.directory}") String matchingReportsDirectory) {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.CSV_MATCHING_ALGO_REPORT_Q)
                            .routeId(RecapConstants.CSV_MATCHING_ALGO_REPORT_ROUTE_ID)
                            .marshal().bindy(BindyType.Csv, ReCAPCSVMatchingRecord.class)
                            .to("file:" + matchingReportsDirectory + File.separator + "?fileName=${in.header.fileName}-${date:now:ddMMMyyyy}.csv");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
