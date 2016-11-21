package org.recap.repository.jpa;

import org.recap.model.jpa.MatchingMatchPointsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by angelind on 27/10/16.
 */
public interface MatchingMatchPointsDetailsRepository extends JpaRepository<MatchingMatchPointsEntity, Integer> {

    @Query(value = "select count(*) FROM recap.matching_matchpoints_t where MATCH_CRITERIA=?1", nativeQuery = true)
    long countBasedOnCriteria(String matchCriteria);

    @Query(value = "select * FROM recap.matching_matchpoints_t where MATCH_CRITERIA=?1 limit ?2,?3", nativeQuery = true)
    List<MatchingMatchPointsEntity> getMatchPointEntityByCriteria(String matchCriteria, long from, long to);
}
