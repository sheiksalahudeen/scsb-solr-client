package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.recap.RecapConstants;
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

    private static final Logger logger = LoggerFactory.getLogger(MatchingAlgorithmRouteBuilder.class);

    /**
     * This method instantiates a new route builder to save matching reports in database.
     *
     * @param camelContext the camel context
     */
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
                            .routeId("saveMatchingReportsQ").threads(10)
                            .bean(MatchingAlgorithmProcessor.class,"saveMatchingReportEntity");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("scsbactivemq:queue:updateItemsQ?concurrentConsumers=10")
                            .routeId("updateItemsQ")
                            .bean(MatchingAlgorithmProcessor.class, "updateItemEntity");
                }
            });

            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("scsbactivemq:queue:updateMatchingBibEntityQ?concurrentConsumers=10")
                            .routeId("updateMatchingBibQ")
                            .bean(MatchingAlgorithmProcessor.class, "updateMatchingBibEntity");
                }
            });

        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
    }
}
