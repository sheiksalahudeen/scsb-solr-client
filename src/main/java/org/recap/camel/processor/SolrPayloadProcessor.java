package org.recap.camel.processor;

import org.apache.camel.*;
import org.apache.camel.component.solr.SolrConstants;
import org.apache.camel.impl.DefaultExchange;

/**
 * Created by rajeshbabuk on 30/8/16.
 */
public class SolrPayloadProcessor implements Processor {

    private String solrUri;
    private String solrCore;
    private String solrRouterURI;
    private ProducerTemplate producerTemplate;
    private CamelContext camelContext;

    public SolrPayloadProcessor(String solrUri, String solrCore, String solrRouterURI, ProducerTemplate producerTemplate, CamelContext camelContext) {
        this.solrUri = solrUri;
        this.solrCore = solrCore;
        this.solrRouterURI = solrRouterURI;
        this.producerTemplate = producerTemplate;
        this.camelContext = camelContext;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Exchange exchangeWithBody = createExchangeWithBody(camelContext, exchange.getIn().getBody());
        exchangeWithBody.getIn().setHeader(SolrConstants.OPERATION, SolrConstants.OPERATION_ADD_BEANS);
        producerTemplate.send("solr:" + solrUri + "/" + solrCore, exchangeWithBody);
    }

    protected Exchange createExchangeWithBody(CamelContext camelContext, Object body) {
        DefaultExchange exchange = new DefaultExchange(camelContext);
        Message message = exchange.getIn();
        message.setBody(body);
        return exchange;
    }
}
