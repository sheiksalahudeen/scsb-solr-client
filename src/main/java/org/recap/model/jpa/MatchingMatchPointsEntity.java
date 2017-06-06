package org.recap.model.jpa;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by angelind on 27/10/16.
 */
@Entity
@Table(name = "MATCHING_MATCHPOINTS_T", schema = "RECAP", catalog = "")
public class MatchingMatchPointsEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "MATCH_CRITERIA")
    private String matchCriteria;

    @Column(name = "CRITERIA_VALUE")
    private String criteriaValue;

    @Column(name = "CRITERIA_VALUE_COUNT")
    private Integer criteriaValueCount;

    /**
     * Gets id.
     *
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets match criteria.
     *
     * @return the match criteria
     */
    public String getMatchCriteria() {
        return matchCriteria;
    }

    /**
     * Sets match criteria.
     *
     * @param matchCriteria the match criteria
     */
    public void setMatchCriteria(String matchCriteria) {
        this.matchCriteria = matchCriteria;
    }

    /**
     * Gets criteria value.
     *
     * @return the criteria value
     */
    public String getCriteriaValue() {
        return criteriaValue;
    }

    /**
     * Sets criteria value.
     *
     * @param criteriaValue the criteria value
     */
    public void setCriteriaValue(String criteriaValue) {
        this.criteriaValue = criteriaValue;
    }

    /**
     * Gets criteria value count.
     *
     * @return the criteria value count
     */
    public Integer getCriteriaValueCount() {
        return criteriaValueCount;
    }

    /**
     * Sets criteria value count.
     *
     * @param criteriaValueCount the criteria value count
     */
    public void setCriteriaValueCount(Integer criteriaValueCount) {
        this.criteriaValueCount = criteriaValueCount;
    }
}
