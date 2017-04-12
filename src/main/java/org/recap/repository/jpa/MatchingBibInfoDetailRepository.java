package org.recap.repository.jpa;

import org.recap.model.jpa.MatchingBibInfoDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by premkb on 29/1/17.
 */
public interface MatchingBibInfoDetailRepository extends JpaRepository<MatchingBibInfoDetail, Integer> {

    List<MatchingBibInfoDetail> findByBibId(String bibId);

    @Query(value = "select distinct LATEST_RECORD_NUM from matching_bib_info_detail_t where BIB_ID in ?1", nativeQuery = true)
    List<Integer> findRecordNumByBibIds(List<String> bibIds);

    List<MatchingBibInfoDetail> findByRecordNumIn(List<Integer> recordNums);
}
