package org.recap.repository.jpa;

import org.recap.model.jpa.ItemChangeLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by rajeshbabuk on 18/10/16.
 */
public interface ItemChangeLogDetailsRepository extends JpaRepository<ItemChangeLogEntity, Integer> {

     @Query(value = "select item from ItemChangeLogEntity item where item.recordId=:recordId and item.operationType=:operationType order by item.updatedDate desc")
     List<ItemChangeLogEntity> findByRecordIdAndOperationTypeAndOrderByUpdatedDateDesc(@Param("recordId") Integer recordId, @Param("operationType") String operationType);

     @Query(value = "select icl.recordId from ItemChangeLogEntity icl where icl.operationType=?1")
     Page<Integer> getRecordIdByOperationType(Pageable pageable, String operationType);
}
