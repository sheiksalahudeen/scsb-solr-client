package org.recap.repository.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.CustomerCodeEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by rajeshbabuk on 18/10/16.
 */
public class CustomerCodeDetailsRepositoryUT extends BaseTestCase {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void findByCustomerCode() throws Exception {

        CustomerCodeEntity customerCodeEntity = new CustomerCodeEntity();
        customerCodeEntity.setCustomerCode("ZZ");
        customerCodeEntity.setDescription("Desc ZZ");
        customerCodeEntity.setOwningInstitutionId(3);
        customerCodeEntity.setDeliveryRestrictions("ZZ,CC");

        CustomerCodeEntity saveCustomerCodeEntity = customerCodeDetailsRepository.saveAndFlush(customerCodeEntity);
        entityManager.refresh(saveCustomerCodeEntity);
        assertNotNull(saveCustomerCodeEntity);
        assertNotNull(saveCustomerCodeEntity.getCustomerCodeId());
        assertNotNull(saveCustomerCodeEntity.getCustomerCode());

        CustomerCodeEntity byCustomerCode = customerCodeDetailsRepository.findByCustomerCode(saveCustomerCodeEntity.getCustomerCode());
        assertNotNull(byCustomerCode);
        assertNotNull(byCustomerCode.getCustomerCode());
        assertEquals("ZZ", byCustomerCode.getCustomerCode());
        assertNotNull(byCustomerCode.getInstitutionEntity());
    }
}
