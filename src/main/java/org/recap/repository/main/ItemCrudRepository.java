package org.recap.repository.main;

import org.recap.model.Item;
import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 * Created by angelind on 15/6/16.
 */
public interface ItemCrudRepository extends SolrCrudRepository<Item, String> {

    Item findByBarcode(String barcode);
}
