package org.recap.model.jpa;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelind on 2/5/17.
 */
@Entity
@Table(name = "JOB_PARAM_T", schema = "RECAP", catalog = "")
public class JobParamEntity implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "RECORD_NUM")
    private Integer recordNumber;

    @Column(name = "JOB_NAME")
    private String jobName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "RECORD_NUM")
    private List<JobParamDataEntity> jobParamDataEntities = new ArrayList<>();

    public Integer getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(Integer recordNumber) {
        this.recordNumber = recordNumber;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public List<JobParamDataEntity> getJobParamDataEntities() {
        return jobParamDataEntities;
    }

    public void setJobParamDataEntities(List<JobParamDataEntity> jobParamDataEntities) {
        this.jobParamDataEntities = jobParamDataEntities;
    }

    public void addAll(List<JobParamDataEntity> jobParamDataEntities) {
        if(null == getJobParamDataEntities()){
            this.jobParamDataEntities = new ArrayList<>();
        }
        this.jobParamDataEntities.addAll(jobParamDataEntities);
    }
}
