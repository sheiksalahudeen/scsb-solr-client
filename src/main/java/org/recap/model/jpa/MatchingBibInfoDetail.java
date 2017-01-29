package org.recap.model.jpa;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by premkb on 28/1/17.
 */
@Entity
@Table(name="MATCHING_BIB_INFO_DETAIL_T",schema="recap",catalog="")
public class MatchingBibInfoDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MATCHING_BIB_INFO_DATA_DUMP_ID")
    private Integer matchingBibInfoDetailId;

    @Column(name = "BIB_ID")
    private String bibId;

    @Column(name = "OWNING_INST_BIB_ID")
    private String owningInstitutionBibId;

    @Column(name = "OWNING_INST")
    private String owningInstitution;

    @Column(name = "RECORD_NUM")
    private Integer recordNum;


    public Integer getMatchingBibInfoDetailId() {
        return matchingBibInfoDetailId;
    }

    public void setMatchingBibInfoDetailId(Integer matchingBibInfoDetailId) {
        this.matchingBibInfoDetailId = matchingBibInfoDetailId;
    }

    public String getBibId() {
        return bibId;
    }

    public void setBibId(String bibId) {
        this.bibId = bibId;
    }

    public String getOwningInstitutionBibId() {
        return owningInstitutionBibId;
    }

    public void setOwningInstitutionBibId(String owningInstitutionBibId) {
        this.owningInstitutionBibId = owningInstitutionBibId;
    }

    public String getOwningInstitution() {
        return owningInstitution;
    }

    public void setOwningInstitution(String owningInstitution) {
        this.owningInstitution = owningInstitution;
    }

    public Integer getRecordNum() {
        return recordNum;
    }

    public void setRecordNum(Integer recordNum) {
        this.recordNum = recordNum;
    }
}
