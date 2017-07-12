package org.recap.repository.jpa;

import org.recap.model.jpa.ItemBarcodeHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by sheiks on 07/07/17.
 */
public interface ItemBarcodeHistoryDetailsRepository extends JpaRepository<ItemBarcodeHistoryEntity, Integer> {
}
