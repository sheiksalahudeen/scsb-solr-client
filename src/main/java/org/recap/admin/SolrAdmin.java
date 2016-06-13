package org.recap.admin;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by pvsubrah on 6/12/16.
 */
@Component
public class SolrAdmin {

    Logger logger = LoggerFactory.getLogger(SolrAdmin.class);

    private CoreAdminRequest coreAdminRequest;

    @Value("${solr.instance.dir}")
    String instanceDir;

    @Autowired
    private SolrClient solrAdminClient;


    public CoreAdminResponse createSolrCore(String coreName) {
        CoreAdminRequest coreAdminRequest = getCoreAdminRequest();
        CoreAdminResponse coreAdminResponse = null;
        try {
            coreAdminResponse = coreAdminRequest.createCore(coreName, instanceDir, solrAdminClient);
            if (coreAdminResponse.getStatus() == 0) {
                logger.info("Created Solr core with name: " + coreName);
            } else {
                logger.error("Error in creating Solr core with name: " + coreName);
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return coreAdminResponse;
    }

    public CoreAdminRequest getCoreAdminRequest() {
        if (null == coreAdminRequest) {
            coreAdminRequest = new CoreAdminRequest();
        }
        return coreAdminRequest;
    }
}
