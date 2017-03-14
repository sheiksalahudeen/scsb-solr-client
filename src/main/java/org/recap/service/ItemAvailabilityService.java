package org.recap.service;

import org.recap.RecapConstants;
import org.recap.model.ItemAvailabilityResponse;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public  List<ItemAvailabilityResponse> getItemStatusByBarcodeAndIsDeletedFalseList(List<String> barcodeList) {
        List<String> barcodes = new ArrayList<>();
        for (String barcode : barcodeList) {
            barcodes.add(barcode.trim());
        }
        Map<String, String> barcodeStatusMap = new HashMap<>();
        List<ItemAvailabilityResponse> itemAvailabilityResponses = new ArrayList<>();
        List<ItemEntity> itemEntityList = itemDetailsRepository.getItemStatusByBarcodeAndIsDeletedFalseList(barcodes);
        for (ItemEntity itemEntity : itemEntityList) {
            barcodeStatusMap.put(itemEntity.getBarcode(), itemEntity.getItemStatusEntity().getStatusDescription());
        }
        for (String requestedBarcode : barcodes) {
            ItemAvailabilityResponse itemAvailabilityResponse = new ItemAvailabilityResponse();
            if (barcodeStatusMap.containsKey(requestedBarcode)) {
                itemAvailabilityResponse.setItemBarcode(requestedBarcode);
                itemAvailabilityResponse.setItemAvailabilityStatus(barcodeStatusMap.get(requestedBarcode));
            } else {
                itemAvailabilityResponse.setItemBarcode(requestedBarcode);
                itemAvailabilityResponse.setItemAvailabilityStatus(RecapConstants.ITEM_BARCDE_DOESNOT_EXIST);
            }
            itemAvailabilityResponses.add(itemAvailabilityResponse);
        }
        return itemAvailabilityResponses;
    }

}
