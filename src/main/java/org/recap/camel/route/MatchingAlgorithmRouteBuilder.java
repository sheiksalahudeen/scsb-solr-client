package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.recap.camel.processor.MatchingAlgorithmProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by angelind on 31/10/16.
 */

@Component
public class MatchingAlgorithmRouteBuilder {

    Logger logger = LoggerFactory.getLogger(MatchingAlgorithmRouteBuilder.class);

    @Autowired
    public MatchingAlgorithmRouteBuilder(CamelContext camelContext) {
        try {

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("scsbactivemq:queue:saveMatchingMatchPointsQ?concurrentConsumers=10")
                            .routeId("saveMatchingQ")
                            .bean(MatchingAlgorithmProcessor.class,"saveMatchingMatchPointEntity");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("scsbactivemq:queue:saveMatchingBibsQ?concurrentConsumers=10")
                            .bean(MatchingAlgorithmProcessor.class,"saveMatchingBibEntity");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("scsbactivemq:queue:saveMatchingReportsQ?concurrentConsumers=10")
                            .routeId("saveMatchingReportsQ")
                            .bean(MatchingAlgorithmProcessor.class,"saveMatchingReportEntity");
                }
            });

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
