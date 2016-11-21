package org.recap.model.jpa;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by pvsubrah on 6/11/16.
 */

@Entity
@Table(name = "request_item_t", schema = "recap", catalog = "")
public class RequestItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "REQUEST_ID")
    private Integer requestId;

    @Column(name = "ITEM_ID")
    private Integer itemId;

    @Column(name = "REQUEST_TYPE_ID")
    private Integer requestTypeId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REQ_EXP_DATE")
    private Date requestExpirationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED_DATE")
    private Date lastUpdatedDate;

    @Column(name = "PATRON_ID")
    private Integer patronId;

    @Column(name = "REQUEST_POSITION")
    private Integer requestPosition;

    @Column(name = "STOP_CODE")
    private String stopCode;

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getRequestTypeId() {
        return requestTypeId;
    }

    public void setRequestTypeId(Integer requestTypeId) {
        this.requestTypeId = requestTypeId;
    }

    public Date getRequestExpirationDate() {
        return requestExpirationDate;
    }

    public void setRequestExpirationDate(Date requestExpirationDate) {
        this.requestExpirationDate = requestExpirationDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Integer getPatronId() {
        return patronId;
    }

    public void setPatronId(Integer patronId) {
        this.patronId = patronId;
    }

    public Integer getRequestPosition() {
        return requestPosition;
    }

    public void setRequestPosition(Integer requestPosition) {
        this.requestPosition = requestPosition;
    }

    public String getStopCode() {
        return stopCode;
    }

    public void setStopCode(String stopCode) {
        this.stopCode = stopCode;
    }
}
