package org.recap.service;

import org.apache.commons.collections.CollectionUtils;
import org.recap.RecapConstants;
import org.recap.model.ItemAvailabilityResponse;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public List<ItemAvailabilityResponse> getItemStatusByBarcodeAndIsDeletedFalseList(List<String> barcodeList){
        List<ItemAvailabilityResponse> itemAvailabilityResponses = new ArrayList<>();
        List<ItemEntity> itemEntityList = itemDetailsRepository.getItemStatusByBarcodeAndIsDeletedFalseList(barcodeList);
            for (ItemEntity itemEntity : itemEntityList) {
                ItemAvailabilityResponse itemAvailabilityResponse = new ItemAvailabilityResponse();
                if(itemEntity.getItemAvailabilityStatusId() == 1) {
                    itemAvailabilityResponse.setItemBarcode(itemEntity.getBarcode());
                    itemAvailabilityResponse.setItemAvailabilityStatus(itemEntity.getItemStatusEntity().getStatusDescription());
                }
                if(itemEntity.getItemAvailabilityStatusId() == 2){
                    itemAvailabilityResponse.setItemBarcode(itemEntity.getBarcode());
                    itemAvailabilityResponse.setItemAvailabilityStatus(itemEntity.getItemStatusEntity().getStatusDescription());
                }
                itemAvailabilityResponses.add(itemAvailabilityResponse);
            }
        if(barcodeList.size() != itemEntityList.size()){
            List<String> barcode = new ArrayList<>();
            for (ItemEntity itemEntity : itemEntityList) {
                barcode.add(itemEntity.getBarcode());
            }
            Collection barcodesNotInDbList = CollectionUtils.subtract(barcodeList,barcode);
            for (Object barcodeNotInDb : barcodesNotInDbList) {
                ItemAvailabilityResponse itemAvailabilityResponse = new ItemAvailabilityResponse();
                itemAvailabilityResponse.setItemBarcode((String)barcodeNotInDb);
                itemAvailabilityResponse.setItemAvailabilityStatus(RecapConstants.ITEM_BARCDE_DOESNOT_EXIST);
                itemAvailabilityResponses.add(itemAvailabilityResponse);
            }
        }
        return itemAvailabilityResponses;
    }

}
