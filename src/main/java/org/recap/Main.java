package org.recap;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.solr.core.SolrTemplate;

@SpringBootApplication
public class Main {

	@Value("${solr.url}")
	String solrUrl;

	@Bean
	public SolrServer solrServer() {
		return new HttpSolrServer(solrUrl);
	}

	@Bean
	public SolrTemplate solrTemplate(SolrServer server) throws Exception {
		SolrTemplate solrTemplate = new SolrTemplate(server);
		return solrTemplate;
	}

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
}
