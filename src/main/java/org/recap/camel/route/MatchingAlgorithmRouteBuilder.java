package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.recap.RecapConstants;
import org.recap.camel.processor.MatchingReportProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by angelind on 22/8/16.
 */
@Component
public class MatchingAlgorithmRouteBuilder {

    Logger logger = LoggerFactory.getLogger(CSVMatchingRecordRouteBuilder.class);

    @Autowired
    public MatchingAlgorithmRouteBuilder(CamelContext camelContext, MatchingReportProcessor matchingReportProcessor) {
        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.MATCHING_ALGO_Q)
                            .routeId(RecapConstants.MATCHING_ALGO_ROUTE_ID)
                            .process(matchingReportProcessor);
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
