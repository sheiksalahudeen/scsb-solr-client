package org.recap.repository.jpa;

import org.recap.model.jpa.MatchingBibEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by angelind on 31/10/16.
 */
public interface MatchingBibDetailsRepository extends JpaRepository<MatchingBibEntity, Integer> {

    /**
     * Gets the count of multiple match unique bibs.
     *
     * @return the multiple match unique bib count
     */
    @Query(value = "select count(distinct(bib_id)) from matching_bib_t where bib_id in (select bib_Id from matching_bib_t group by BIB_ID having count(bib_id) > 1)", nativeQuery = true)
    long getMultipleMatchUniqueBibCount();

    /**
     * Gets bib ids for multiple matched bibs based on limit given in the parameters.
     *
     * @param from the from
     * @param to   the to
     * @return the multiple matched bib ids based on limit
     */
    @Query(value = "select bib_Id from matching_bib_t group by bib_id having count(bib_id) > 1 order by bib_id asc limit ?1,?2", nativeQuery = true)
    List<Integer> getMultipleMatchedBibIdsBasedOnLimit(long from, long to);

    /**
     * Gets the count of single matched bib based on the given matching criteria.
     *
     * @param matching the matching
     * @return the single match bib count based on matching
     */
    @Query(value = "select count(*) from matching_bib_t where bib_id in (select bib_Id from matching_bib_t group by BIB_ID having count(bib_id) = 1) and matching =?1", nativeQuery = true)
    long getSingleMatchBibCountBasedOnMatching(String matching);

    /**
     * Gets a list of matching bib entities for single match bib based on the given matching and limit values.
     *
     * @param matching the matching
     * @param from     the from
     * @param to       the to
     * @return the single match bib entities
     */
    @Query(value = "select * from matching_bib_t where bib_id in (select bib_Id from matching_bib_t group by BIB_ID having count(bib_id) = 1) and matching =?1 limit ?2,?3", nativeQuery = true)
    List<MatchingBibEntity> getSingleMatchBibEntities(String matching, long from, long to);

    /**
     * Gets a list of bib entities based on the given list of bib ids.
     *
     * @param bibIds the bib ids
     * @return the bib entity based on bib ids
     */
    @Query(value = "select * from matching_bib_t where bib_id in (?1) order by bib_id", nativeQuery = true)
    List<MatchingBibEntity> getBibEntityBasedOnBibIds(List<Integer> bibIds);

    /**
     * Gets a list of multi match bib entities based on the given bib ids and matching criterias.
     *
     * @param bibIds            the bib ids
     * @param matchingCriteria1 the matching criteria 1
     * @param matchingCriteria2 the matching criteria 2
     * @return the multi match bib entities based on bib ids
     */
    @Query(value = "select MB1 from MatchingBibEntity MB1 where MB1.bibId in (?1) and MB1.matching in (?2, ?3) order by MB1.bibId")
    List<MatchingBibEntity> getMultiMatchBibEntitiesBasedOnBibIds(List<Integer> bibIds, String matchingCriteria1, String matchingCriteria2);

    /**
     * Gets a list of bib ids which are single match bibs based on the given matching criteria.
     *
     * @param matching the matching
     * @return the single match bib ids based on matching
     */
    @Query(value = "select bib_id from matching_bib_t where bib_id in (select bib_Id from matching_bib_t group by BIB_ID having count(bib_id) = 1) and matching =?1", nativeQuery = true)
    List<Integer> getSingleMatchBibIdsBasedOnMatching(String matching);

    /**
     * Gets a list of bib ids which are multi match bibs for oclc and isbn matching criterias.
     *
     * @return the multi match bib ids for oclc and isbn
     */
    @Query(value = "select distinct bib_id from matching_bib_t where bib_id in (select bib_id from matching_bib_t " +
            "where MATCHING in ('OCLCNumber','ISBN') group by bib_id having count(bib_id) > 1) and " +
            "MATCHING in ('OCLCNumber','ISBN')", nativeQuery = true)
    List<Integer> getMultiMatchBibIdsForOclcAndIsbn();

    /**
     * Gets a list of bib ids which are multi match bibs for oclc and issn matching criterias.
     *
     * @return the multi match bib ids for oclc and issn
     */
    @Query(value = "select distinct bib_id from matching_bib_t where " +
            "bib_id in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISSN') group by bib_id having count(bib_id) > 1) and " +
            "bib_id not in ((select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISBN') group by bib_id having count(bib_id) > 1)) and " +
            "MATCHING in ('OCLCNumber','ISSN') order by bib_id", nativeQuery = true)
    List<Integer> getMultiMatchBibIdsForOclcAndIssn();

    /**
     * Gets a list of bib ids which are multi match bib for oclc and lccn matching criterias.
     *
     * @return the multi match bib ids for oclc and lccn
     */
    @Query(value = "select distinct bib_id from matching_bib_t where " +
            "bib_id in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','LCCN') group by bib_id having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISSN') group by bib_id having count(bib_id) > 1) and " +
            "bib_id not in ((select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISBN') group by bib_id having count(bib_id) > 1)) and " +
            "MATCHING in ('OCLCNumber','LCCN') order by bib_id", nativeQuery = true)
    List<Integer> getMultiMatchBibIdsForOclcAndLccn();

    /**
     * Gets a list of bib ids which are multi match bib ids for isbn and issn matching criterias.
     *
     * @return the multi match bib ids for isbn and issn
     */
    @Query(value = "select distinct bib_id from matching_bib_t where " +
            "bib_id in (select bib_id from matching_bib_t where MATCHING in ('ISBN','ISSN') group by bib_id having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','LCCN') group by bib_id having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISSN') group by bib_id having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISBN') group by bib_id having count(bib_id) > 1) and " +
            "MATCHING in ('ISBN','ISSN') order by bib_id", nativeQuery = true)
    List<Integer> getMultiMatchBibIdsForIsbnAndIssn();

    /**
     * Gets a list of bib ids which are multi match bib ids for isbn and lccn matching criterias.
     *
     * @return the multi match bib ids for isbn and lccn
     */
    @Query(value = "select distinct bib_id from matching_bib_t where " +
            "bib_id in (select bib_id from matching_bib_t where MATCHING in ('ISBN','LCCN') group by BIB_ID having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('ISBN','ISSN') group by BIB_ID having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','LCCN') group by BIB_ID having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISSN') group by BIB_ID having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISBN') group by BIB_ID having count(bib_id) > 1) and " +
            "MATCHING in ('ISBN','ISSN') order by bib_id", nativeQuery = true)
    List<Integer> getMultiMatchBibIdsForIsbnAndLccn();

    /**
     * Gets a list of bib ids which are multi match bib ids for issn and lccn matching criterias.
     *
     * @return the multi match bib ids for issn and lccn
     */
    @Query(value = "select distinct bib_id from matching_bib_t where " +
            "bib_id in (select bib_id from matching_bib_t where MATCHING in ('ISSN','LCCN') group by BIB_ID having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISBN') group by BIB_ID having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('ISBN','LCCN') group by BIB_ID having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('ISBN','ISSN') group by BIB_ID having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','LCCN') group by BIB_ID having count(bib_id) > 1) and " +
            "bib_id not in (select bib_id from matching_bib_t where MATCHING in ('OCLCNumber','ISSN') group by BIB_ID having count(bib_id) > 1) and " +
            "MATCHING in ('ISBN','ISSN') order by bib_id", nativeQuery = true)
    List<Integer> getMultiMatchBibIdsForIssnAndLccn();

    /**
     * Update status int.
     *
     * @param status the status
     * @param bibIds the bib ids
     * @return the int
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE MatchingBibEntity mbe SET mbe.status = :status WHERE mbe.bibId in :bibIds")
    int updateStatusBasedOnBibs(@Param("status") String status, @Param("bibIds") List<Integer> bibIds);

     /**
     * Update status int.
     *
     * @param statusToUpdate the status to update
     * @param actualStatus the actual status
     * @return the int
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE MatchingBibEntity mbe SET mbe.status = :statusToUpdate WHERE mbe.status in :actualStatus")
    int updateStatus(@Param("statusToUpdate") String statusToUpdate,@Param("actualStatus") String actualStatus);

    /**
     * Find by status page.
     *
     * @param pageable the pageable
     * @param status   the status
     * @return the page
     */
    Page<MatchingBibEntity> findByStatus(Pageable pageable, String status);

    /**
     * Find by bib id in list.
     *
     * @param bibIds the bib ids
     * @return the list
     */
    List<MatchingBibEntity> findByBibIdIn(List<Integer> bibIds);

    /**
     * Find by matching and bib id in list.
     *
     * @param matching the matching
     * @param bibIds   the bib ids
     * @return the list
     */
    List<MatchingBibEntity> findByMatchingAndBibIdIn(String matching, List<Integer> bibIds);
}
