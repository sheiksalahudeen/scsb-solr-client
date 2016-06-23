package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by hemalathas on 21/6/16.
 */
public class HoldingsEntityTest extends BaseTestCase{

    @Test
    public void saveHoldings() throws Exception{
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("Mock holding content");
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setBibliographicId(1);
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setOwningInstitutionHoldingsId(".c10899406");
        HoldingsEntity entity = holdingDetailRepository.save(holdingsEntity);
        assertNotNull(entity);
        System.out.println(entity.getHoldingsId());
        assertEquals(entity.getContent(),"Mock holding content");
        assertEquals(entity.getBibliographicId().toString(),"1");
        assertEquals(entity.getOwningInstitutionHoldingsId(),".c10899406");
        System.out.println("Created date -->"+entity.getCreatedDate() +""+ "last updated date-->"+entity.getLastUpdatedDate());
        holdingDetailRepository.delete(holdingsEntity);
    }

}