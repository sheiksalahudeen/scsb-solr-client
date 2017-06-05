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

    /**
     * Gets a list of accession entity using from date, to date and status.
     *
     * @param fromDate the from date
     * @param toDate   the to date
     * @param status   the status
     * @return the accession entity by date and status
     */
    @Query(value = "select * from accession_t where CREATED_DATE between ?1 and ?2 and ACCESSION_STATUS=?3", nativeQuery = true)
    List<AccessionEntity> getAccessionEntityByDateAndStatus(Date fromDate, Date toDate, String status);

    /**
     * Find by accession status and return a list of accession entity.
     *
     * @param accessionStatus the accession status
     * @return the list
     */
    List<AccessionEntity> findByAccessionStatus(String accessionStatus);
}
