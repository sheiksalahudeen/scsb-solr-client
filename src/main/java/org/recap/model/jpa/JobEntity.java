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

    @Column(name = "JOB_INSTANCE_ID")
    private Integer jobInstanceId;

    /**
     * Gets job id.
     *
     * @return the job id
     */
    public Integer getJobId() {
        return jobId;
    }

    /**
     * Sets job id.
     *
     * @param jobId the job id
     */
    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    /**
     * Gets job name.
     *
     * @return the job name
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Sets job name.
     *
     * @param jobName the job name
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * Gets job description.
     *
     * @return the job description
     */
    public String getJobDescription() {
        return jobDescription;
    }

    /**
     * Sets job description.
     *
     * @param jobDescription the job description
     */
    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    /**
     * Gets last executed time.
     *
     * @return the last executed time
     */
    public Date getLastExecutedTime() {
        return lastExecutedTime;
    }

    /**
     * Sets last executed time.
     *
     * @param lastExecutedTime the last executed time
     */
    public void setLastExecutedTime(Date lastExecutedTime) {
        this.lastExecutedTime = lastExecutedTime;
    }

    /**
     * Gets next run time.
     *
     * @return the next run time
     */
    public Date getNextRunTime() {
        return nextRunTime;
    }

    /**
     * Sets next run time.
     *
     * @param nextRunTime the next run time
     */
    public void setNextRunTime(Date nextRunTime) {
        this.nextRunTime = nextRunTime;
    }

    /**
     * Gets cron expression.
     *
     * @return the cron expression
     */
    public String getCronExpression() {
        return cronExpression;
    }

    /**
     * Sets cron expression.
     *
     * @param cronExpression the cron expression
     */
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets job instance id.
     *
     * @return the job instance id
     */
    public Integer getJobInstanceId() {
        return jobInstanceId;
    }

    /**
     * Sets job instance id.
     *
     * @param jobInstanceId the job instance id
     */
    public void setJobInstanceId(Integer jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }
}
