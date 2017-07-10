package org.recap.controller;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 3/7/17.
 */
public class ItemControllerUT extends BaseTestCase{

    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    ItemController mockedItemController;

    @Mock
    ItemDetailsRepository mockedItemDetailsRepository;

    @Test
    public void testItemController(){
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setItemEntities(Arrays.asList(new ItemEntity()));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(new HoldingsEntity()));
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setBarcode("3325656544854");
        itemEntity.setHoldingsEntities(Arrays.asList(new HoldingsEntity()));
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        ItemController itemController = new ItemController(itemDetailsRepository);
        Mockito.when(mockedItemController.getItemDetailsRepository()).thenReturn(mockedItemDetailsRepository);
        Mockito.when(mockedItemController.getItemDetailsRepository().findByBarcodeIn(Mockito.any())).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(mockedItemController.findByBarcodeIn("[3325656544854,334545888458]")).thenCallRealMethod();
        List<ItemEntity> itemEntityList = mockedItemController.findByBarcodeIn("[3325656544854,334545888458]");
        assertNotNull(itemEntityList);
        Mockito.when(mockedItemController.getItemDetailsRepository()).thenCallRealMethod();
        assertNotEquals(itemDetailsRepository,mockedItemController.getItemDetailsRepository());
    }

}