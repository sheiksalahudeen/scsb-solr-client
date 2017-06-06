package org.recap.repository.jpa;

import org.recap.model.jpa.MatchingMatchPointsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by angelind on 27/10/16.
 */
public interface MatchingMatchPointsDetailsRepository extends JpaRepository<MatchingMatchPointsEntity, Integer> {

    /**
     * Counts the number of matching match points based on the given criteria.
     *
     * @param matchCriteria the match criteria
     * @return the long
     */
    @Query(value = "select count(*) FROM recap.matching_matchpoints_t where MATCH_CRITERIA=?1", nativeQuery = true)
    long countBasedOnCriteria(String matchCriteria);

    /**
     * Gets a list of match point entities based on the given criteria.
     *
     * @param matchCriteria the match criteria
     * @param from          the from
     * @param to            the to
     * @return the match point entity by criteria
     */
    @Query(value = "select * FROM recap.matching_matchpoints_t where MATCH_CRITERIA=?1 limit ?2,?3", nativeQuery = true)
    List<MatchingMatchPointsEntity> getMatchPointEntityByCriteria(String matchCriteria, long from, long to);
}
