package org.recap.model.jpa;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by rajeshbabuk on 4/4/17.
 */
@Entity
@Table(name = "JOB_T", schema = "recap", catalog = "")
public class JobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "JOB_ID")
    private Integer jobId;

    @Column(name = "JOB_NAME")
    private String jobName;

    @Column(name = "JOB_DESC")
    private String jobDescription;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_EXECUTED_TIME")
    private Date lastExecutedTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "NEXT_RUN_TIME")
    private Date nextRunTime;

    @Column(name = "CRON_EXP")
    private String cronExpression;

    @Column(name = "STATUS")
    private String status;

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public Date getLastExecutedTime() {
        return lastExecutedTime;
    }

    public void setLastExecutedTime(Date lastExecutedTime) {
        this.lastExecutedTime = lastExecutedTime;
    }

    public Date getNextRunTime() {
        return nextRunTime;
    }

    public void setNextRunTime(Date nextRunTime) {
        this.nextRunTime = nextRunTime;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
