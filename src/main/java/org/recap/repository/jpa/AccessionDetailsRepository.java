package org.recap.repository.jpa;

import org.recap.model.jpa.AccessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by rajeshbabuk on 8/5/17.
 */
public interface AccessionDetailsRepository extends JpaRepository<AccessionEntity, Integer> {

    @Query(value = "select * from accession_t where CREATED_DATE between ?1 and ?2", nativeQuery = true)
    List<AccessionEntity> getAccessionEntityByDate(Date fromDate, Date toDate);
}
