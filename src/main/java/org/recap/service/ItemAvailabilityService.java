package org.recap.service;

import org.recap.RecapConstants;
import org.recap.model.BibItemAvailabityStatusRequest;
import org.recap.model.ItemAvailabilityResponse;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
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

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    public String getItemStatusByBarcodeAndIsDeletedFalse(String barcode) {
        return itemDetailsRepository.getItemStatusByBarcodeAndIsDeletedFalse(barcode);
    }

    public List<ItemAvailabilityResponse> getItemStatusByBarcodeAndIsDeletedFalseList(List<String> barcodeList) {
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

    public List<ItemAvailabilityResponse> getbibItemAvaiablityStatus(BibItemAvailabityStatusRequest bibItemAvailabityStatusRequest) {
        List<ItemAvailabilityResponse> itemAvailabilityResponses = new ArrayList<>();
        BibliographicEntity bibliographicEntity;
        if (bibItemAvailabityStatusRequest.getInstitutionId().equalsIgnoreCase(RecapConstants.SCSB)) {
            bibliographicEntity = bibliographicDetailsRepository.findByBibliographicId(Integer.parseInt(bibItemAvailabityStatusRequest.getBibliographicId()));
        } else {
            InstitutionEntity institutionEntity = institutionDetailsRepository.findByInstitutionCode(bibItemAvailabityStatusRequest.getInstitutionId());
            bibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(institutionEntity.getInstitutionId(), bibItemAvailabityStatusRequest.getBibliographicId());
        }
        for (ItemEntity itemEntity : bibliographicEntity.getItemEntities()) {
            ItemAvailabilityResponse itemAvailabilityResponse = new ItemAvailabilityResponse();
            itemAvailabilityResponse.setItemBarcode(itemEntity.getBarcode());
            itemAvailabilityResponse.setItemAvailabilityStatus(itemEntity.getItemStatusEntity().getStatusCode());
            itemAvailabilityResponses.add(itemAvailabilityResponse);
        }
        return itemAvailabilityResponses;
    }
}
