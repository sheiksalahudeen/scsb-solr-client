package org.recap.repository.jpa;

import org.recap.model.jpa.ItemChangeLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by rajeshbabuk on 18/10/16.
 */
public interface ItemChangeLogDetailsRepository extends JpaRepository<ItemChangeLogEntity, Integer> {

     ItemChangeLogEntity findByRecordIdAndOperationType(Integer recordId,String operationType);

     @Query(value = "select icl.recordId from ItemChangeLogEntity icl where icl.operationType=?1")
     Page<Integer> getRecordIdByOperationType(Pageable pageable, String operationType);
}
