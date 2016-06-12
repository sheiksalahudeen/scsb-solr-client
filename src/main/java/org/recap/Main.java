package org.recap;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

@SpringBootApplication
@EnableSolrRepositories("org.recap.repository")
public class Main {

	@Value("${solr.url}")
	String solrUrl;

	@Bean
	public SolrClient solrClient() {
		return new HttpSolrClient(solrUrl);
	}

	@Bean
	public SolrTemplate solrTemplate(SolrClient solrClient) throws Exception {
		SolrTemplate solrTemplate = new SolrTemplate(solrClient);
		return solrTemplate;
	}

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
}
