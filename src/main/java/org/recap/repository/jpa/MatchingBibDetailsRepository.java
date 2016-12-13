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

    @Query(value = "select MBE from MatchingBibEntity as MBE where \n" +
            "MBE.bibId in (select MBE1.bibId from MatchingBibEntity as MBE1 group by MBE1.bibId having count(MBE1.bibId) > 1) and\n" +
            "MBE.matching in (?1,?2) and\n" +
            "MBE.oclc in (select MBE2.oclc from MatchingBibEntity as MBE2 where MBE2.matching=?1) and\n" +
            "MBE.isbn in (select MBE3.isbn from MatchingBibEntity as MBE3 where MBE3.matching=?2)")
    Page<MatchingBibEntity> getMultiMatchBibEntitiesForOCLCAndISBN(Pageable pageable, String matchCriteria1, String matchCriteria2);

}
