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

    /**
     * Gets job param data id.
     *
     * @return the job param data id
     */
    public Integer getJobParamDataId() {
        return jobParamDataId;
    }

    /**
     * Sets job param data id.
     *
     * @param jobParamDataId the job param data id
     */
    public void setJobParamDataId(Integer jobParamDataId) {
        this.jobParamDataId = jobParamDataId;
    }

    /**
     * Gets param name.
     *
     * @return the param name
     */
    public String getParamName() {
        return paramName;
    }

    /**
     * Sets param name.
     *
     * @param paramName the param name
     */
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    /**
     * Gets param value.
     *
     * @return the param value
     */
    public String getParamValue() {
        return paramValue;
    }

    /**
     * Sets param value.
     *
     * @param paramValue the param value
     */
    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    /**
     * Gets record num.
     *
     * @return the record num
     */
    public String getRecordNum() {
        return recordNum;
    }

    /**
     * Sets record num.
     *
     * @param recordNum the record num
     */
    public void setRecordNum(String recordNum) {
        this.recordNum = recordNum;
    }

}
