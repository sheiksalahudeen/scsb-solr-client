package org.recap;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;

/**
 * The Main class is used to lanuch the spring boot application.
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableSolrRepositories(value = "org.recap.repository.solr.main", multicoreSupport = true)
public class Main {

    /**
     * The Solr server protocol.
     */
    @Value("${solr.server.protocol}")
	String solrServerProtocol;

    /**
     * The Solr url.
     */
    @Value("${solr.url}")
	String solrUrl;

    /**
     * The Solr parent core.
     */
    @Value("${solr.parent.core}")
	String solrParentCore;

    /**
     * The Tomcat max parameter count.
     */
    @Value("${tomcat.maxParameterCount}")
	Integer tomcatMaxParameterCount;

    /**
     * Solr admin client.
     *
     * @return the solr client
     */
    @Bean
	public SolrClient solrAdminClient() {
		return new HttpSolrClient(solrServerProtocol + solrUrl);
	}

    /**
     * Instantiates http solr client.
     *
     * @return the solr client
     */
    @Bean
	public SolrClient solrClient() {
		String baseURLForParentCore = solrServerProtocol + solrUrl + File.separator + solrParentCore;
		return new HttpSolrClient(baseURLForParentCore);
	}

    /**
     * Instantiates solr template.
     *
     * @param solrClient the solr client
     * @return the solr template
     * @throws Exception the exception
     */
    @Bean
	public SolrTemplate solrTemplate(SolrClient solrClient) throws Exception {
		return new SolrTemplate(solrClient);
	}

    /**
     * Gets tomcat embedded servlet container factory.
     *
     * @return the embedded servlet container factory
     */
    @Bean
	public EmbeddedServletContainerFactory servletContainerFactory() {
		TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
		factory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> connector.setMaxParameterCount(tomcatMaxParameterCount));
		return factory;
	}

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
}
