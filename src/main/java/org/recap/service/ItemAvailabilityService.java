package org.recap.service;

import org.recap.repository.jpa.ItemDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by premkb on 10/11/16.
 */
@Service
public class ItemAvailabilityService {

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    public String getItemStatusByBarcodeAndIsDeletedFalse(String barcode) {
        return itemDetailsRepository.getItemStatusByBarcodeAndIsDeletedFalse(barcode);
    }

}
