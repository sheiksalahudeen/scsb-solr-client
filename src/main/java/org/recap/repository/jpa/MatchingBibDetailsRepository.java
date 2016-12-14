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

    @Query(value = "select MB2.bibId from MatchingBibEntity MB2, MatchingBibEntity MB3\n" +
            "where MB2.oclc = MB3.oclc and MB2.isbn = MB3.isbn and MB2.id = MB3.id \n" +
            "and MB2.matching in (?1, ?2)\n" +
            "group by MB2.bibId having count(MB2.bibId) > 1")
    List<Integer> getMultiMatchBibIdsForOclcAndIsbn(String matchingCriteria1, String matchingCriteria2);


    @Query(value = "select MB1 from MatchingBibEntity MB1 where MB1.bibId in (?1) and MB1.matching in (?2, ?3) order by MB1.bibId")
    List<MatchingBibEntity> getMultiMatchBibEntitiesBasedOnBibIds(List<Integer> bibIds, String matchingCriteria1, String matchingCriteria2);


}
