package org.recap.repository.solr.main;

import org.recap.model.solr.Holdings;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.List;

/**
 * Created by rajeshbabuk on 13/9/16.
 */
public interface HoldingsSolrCrudRepository extends SolrCrudRepository<Holdings, String> {

    Holdings findByHoldingsId(Integer holdingsId);

    void deleteByHoldingsId(Integer holdingsId);

    void deleteByHoldingsIdIn(List<Integer> holdingsIds);
}
