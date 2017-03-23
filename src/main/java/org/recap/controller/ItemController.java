package org.recap.controller;


import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by chenchulakshmig on 6/13/16.
 */
@RestController
@RequestMapping("/item")
public class ItemController {
    private final ItemDetailsRepository itemDetailsRepository;

    @Autowired
    public ItemController(ItemDetailsRepository itemDetailsRepository) {
        this.itemDetailsRepository = itemDetailsRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value ="/findByBarcodeIn")
    public List<ItemEntity> findByBarcodeIn(String barcodes){

        List<ItemEntity> itemEntityList = itemDetailsRepository.findByBarcodeIn(splitStringAndGetList(barcodes));
        for (Iterator<ItemEntity> itemEntityIterator = itemEntityList.iterator(); itemEntityIterator.hasNext(); ) {
            ItemEntity itemEntity = itemEntityIterator.next();
            for(BibliographicEntity bibliographicEntity : itemEntity.getBibliographicEntities()){
                bibliographicEntity.setItemEntities(null);
                bibliographicEntity.setHoldingsEntities(null);
            }
            itemEntity.setHoldingsEntities(null);
        }
        return itemEntityList;
    }

    private List<String> splitStringAndGetList(String itemBarcodes){
        String itemBarcodesString = itemBarcodes.replaceAll("\\[","").replaceAll("\\]","");
        String[] splittedString = itemBarcodesString.split(",");
        List<String> stringList = new ArrayList<>();
        for(String barcode : splittedString){
            stringList.add(barcode.trim());
        }
        return stringList;
    }
}
