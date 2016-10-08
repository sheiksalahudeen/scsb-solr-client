package org.recap.repository.solr.main;

import org.recap.model.solr.Holdings;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.List;

/**
 * Created by rajeshbabuk on 13/9/16.
 */
@RepositoryRestResource(collectionResourceRel = "holdingsSolr", path = "holdingsSolr")
public interface HoldingsSolrCrudRepository extends SolrCrudRepository<Holdings, String> {

    Holdings findByHoldingsId(Integer holdingsId);

    int deleteByHoldingsId(@Param("holdingsId") Integer holdingsId);

    int deleteByHoldingsIdIn(@Param("holdingsIds") List<Integer> holdingsIds);
}
