package org.recap.model.jpa;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by angelind on 2/5/2017.
 */
@Entity
@Table(name = "JOB_PARAM_DATA_T", schema = "RECAP", catalog = "")
public class JobParamDataEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "JOB_PARAM_DATA_ID")
    private Integer jobParamDataId;

    @Column(name = "PARAM_NAME")
    private String paramName;

    @Column(name = "PARAM_VALUE")
    private String paramValue;

    @Column(name = "RECORD_NUM")
    private String recordNum;

    public Integer getJobParamDataId() {
        return jobParamDataId;
    }

    public void setJobParamDataId(Integer jobParamDataId) {
        this.jobParamDataId = jobParamDataId;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getRecordNum() {
        return recordNum;
    }

    public void setRecordNum(String recordNum) {
        this.recordNum = recordNum;
    }

}
