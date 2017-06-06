package org.recap.repository.jpa;

import org.recap.model.jpa.MatchingBibInfoDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by premkb on 29/1/17.
 */
public interface MatchingBibInfoDetailRepository extends JpaRepository<MatchingBibInfoDetail, Integer> {

    /**
     * Finds MatchingBibInfoDetail by using bib id.
     *
     * @param bibId the bib id
     * @return the list
     */
    List<MatchingBibInfoDetail> findByBibId(String bibId);

    /**
     * Finds a list of last record num by using a list bib ids.
     *
     * @param bibIds the bib ids
     * @return the list
     */
    @Query(value = "select distinct LATEST_RECORD_NUM from matching_bib_info_detail_t where BIB_ID in ?1", nativeQuery = true)
    List<Integer> findRecordNumByBibIds(List<String> bibIds);

    /**
     * Finds a list of MatchingBibInfoDetail by using a list of record nums.
     *
     * @param recordNums the record nums
     * @return the list
     */
    List<MatchingBibInfoDetail> findByRecordNumIn(List<Integer> recordNums);
}
