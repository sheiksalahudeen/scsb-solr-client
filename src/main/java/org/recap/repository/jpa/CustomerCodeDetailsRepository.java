package org.recap.repository.jpa;

import org.recap.model.jpa.CustomerCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by rajeshbabuk on 18/10/16.
 */
public interface CustomerCodeDetailsRepository extends JpaRepository<CustomerCodeEntity, Integer> {

    CustomerCodeEntity findByCustomerCode(String customerCode);

    List<CustomerCodeEntity> findByCustomerCodeIn(List<String> customerCodes);
}
