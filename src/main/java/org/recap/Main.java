package org.recap;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

import java.io.File;

@SpringBootApplication
@EnableSolrRepositories(value = "org.recap.repository.solr.main", multicoreSupport = true)
public class Main {

	@Value("${solr.url}")
	String solrUrl;

	@Value("${solr.parent.core}")
	String solrParentCore;

	@Bean
	public SolrClient solrAdminClient() {
		return new HttpSolrClient(solrUrl);
	}

	@Bean
	public SolrClient solrClient() {
		String baseURLForParentCore = solrUrl + File.separator + solrParentCore;
		return new HttpSolrClient(baseURLForParentCore);
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
