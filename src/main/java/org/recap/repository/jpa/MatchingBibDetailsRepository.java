package org.recap.repository.jpa;

import org.recap.model.jpa.MatchingBibEntity;
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

    @Query(value = "select count(distinct(bib_id)) from matching_bib_t where bib_id in (select bib_Id from matching_bib_t group by BIB_ID having count(bib_id) = 1)", nativeQuery = true)
    long getSingleMatchUniqueBibCount();

    @Query(value = "select bib_Id from matching_bib_t group by bib_id having count(bib_id) = 1 order by bib_id asc limit ?1,?2", nativeQuery = true)
    List<Integer> getSingleMatchedBibIdsBasedOnLimit(long from, long to);

    @Query(value = "select * from matching_bib_t where bib_id in (?1) order by bib_id", nativeQuery = true)
    List<MatchingBibEntity> getBibEntityBasedOnBibIds(List<Integer> bibIds);
    
}
