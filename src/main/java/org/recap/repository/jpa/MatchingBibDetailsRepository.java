package org.recap.repository.jpa;

import org.recap.model.jpa.MatchingBibEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by angelind on 31/10/16.
 */
public interface MatchingBibDetailsRepository extends JpaRepository<MatchingBibEntity, Integer> {

    @Query(value = "select count(distinct(bib_id)) from matching_bib_t where bib_id in (select bib_Id from matching_bib_t group by BIB_ID having count(bib_id) > 1)", nativeQuery = true)
    long getMultipleMatchUniqueBibCount();

    @Query(value = "select bib_Id from matching_bib_t group by bib_id having count(bib_id) > 1 order by bib_id asc limit ?1,?2", nativeQuery = true)
    List<Integer> getMultipleMatchedBibIdsBasedOnLimit(long from, long to);

    @Query(value = "select count(*) from matching_bib_t where bib_id in (select bib_Id from matching_bib_t group by BIB_ID having count(bib_id) = 1) and matching =?1", nativeQuery = true)
    long getSingleMatchBibCountBasedOnMatching(String matching);

    @Query(value = "select * from matching_bib_t where bib_id in (select bib_Id from matching_bib_t group by BIB_ID having count(bib_id) = 1) and matching =?1 limit ?2,?3", nativeQuery = true)
    List<MatchingBibEntity> getSingleMatchBibEntities(String matching, long from, long to);

    @Query(value = "select * from matching_bib_t where bib_id in (?1) order by bib_id", nativeQuery = true)
    List<MatchingBibEntity> getBibEntityBasedOnBibIds(List<Integer> bibIds);

    @Query(value = "select MB1 from MatchingBibEntity MB1 where MB1.bibId in (?1) and MB1.matching in (?2, ?3) order by MB1.bibId")
    List<MatchingBibEntity> getMultiMatchBibEntitiesBasedOnBibIds(List<Integer> bibIds, String matchingCriteria1, String matchingCriteria2);

    @Query(value = "select bib_id from matching_bib_t where bib_id in (select bib_Id from matching_bib_t group by BIB_ID having count(bib_id) = 1) and matching =?1", nativeQuery = true)
    List<Integer> getSingleMatchBibIdsBasedOnMatching(String matching);

    @Query(value = "select distinct bib_id from matching_bib_t where bib_id in (select bib_id from matching_bib_t " +
            "where MATCHING in ('OCLCNumber','ISBN') group by bib_id having count(bib_id) > 1) and " +
            "MATCHING in ('OCLCNumber','ISBN')", nativeQuery = true)
    List<Integer> getMultiMatchBibIdsForOclcAndIsbn();

    @Query(value = "select distinct bib_id from matching_bib_t where " +
            "bib_id in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISSN') group by bib_id having count(bib_id) > 1) and " +
            "bib_id not in ((select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISBN') group by bib_id having count(bib_id) > 1)) and " +
            "MATCHING in ('OCLCNumber','ISSN') order by bib_id", nativeQuery = true)
    List<Integer> getMultiMatchBibIdsForOclcAndIssn();

    @Query(value = "select distinct bib_id from matching_bib_t where " +
            "bib_id in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','LCCN') group by bib_id having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISSN') group by bib_id having count(bib_id) > 1) and " +
            "bib_id not in ((select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISBN') group by bib_id having count(bib_id) > 1)) and " +
            "MATCHING in ('OCLCNumber','LCCN') order by bib_id", nativeQuery = true)
    List<Integer> getMultiMatchBibIdsForOclcAndLccn();

    @Query(value = "select distinct bib_id from matching_bib_t where " +
            "bib_id in (select bib_id from matching_bib_t where MATCHING in ('ISBN','ISSN') group by bib_id having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','LCCN') group by bib_id having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISSN') group by bib_id having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISBN') group by bib_id having count(bib_id) > 1) and " +
            "MATCHING in ('ISBN','ISSN') order by bib_id", nativeQuery = true)
    List<Integer> getMultiMatchBibIdsForIsbnAndIssn();

    @Query(value = "select distinct bib_id from matching_bib_t where " +
            "bib_id in (select bib_id from matching_bib_t where MATCHING in ('ISBN','LCCN') group by BIB_ID having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('ISBN','ISSN') group by BIB_ID having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','LCCN') group by BIB_ID having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISSN') group by BIB_ID having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISBN') group by BIB_ID having count(bib_id) > 1) and " +
            "MATCHING in ('ISBN','ISSN') order by bib_id", nativeQuery = true)
    List<Integer> getMultiMatchBibIdsForIsbnAndLccn();

    @Query(value = "select distinct bib_id from matching_bib_t where " +
            "bib_id in (select bib_id from matching_bib_t where MATCHING in ('ISSN','LCCN') group by BIB_ID having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISBN') group by BIB_ID having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('ISBN','LCCN') group by BIB_ID having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('ISBN','ISSN') group by BIB_ID having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','LCCN') group by BIB_ID having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISSN') group by BIB_ID having count(bib_id) > 1) and " +
            "MATCHING in ('ISBN','ISSN') order by bib_id", nativeQuery = true)
    List<Integer> getMultiMatchBibIdsForIssnAndLccn();
}
