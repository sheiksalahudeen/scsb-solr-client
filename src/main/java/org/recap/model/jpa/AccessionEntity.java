package org.recap.model.jpa;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by rajeshbabuk on 8/5/17.
 */
@Entity
@Table(name = "ACCESSION_T", schema = "recap", catalog = "")
public class AccessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ACCESSION_ID")
    private Integer accessionId;

    @Column(name = "ACCESSION_REQUEST")
    private String accessionRequest;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "ACCESSION_STATUS")
    private String accessionStatus;

    /**
     * Gets accession id.
     *
     * @return the accession id
     */
    public Integer getAccessionId() {
        return accessionId;
    }

    /**
     * Sets accession id.
     *
     * @param accessionId the accession id
     */
    public void setAccessionId(Integer accessionId) {
        this.accessionId = accessionId;
    }

    /**
     * Gets accession request.
     *
     * @return the accession request
     */
    public String getAccessionRequest() {
        return accessionRequest;
    }

    /**
     * Sets accession request.
     *
     * @param accessionRequest the accession request
     */
    public void setAccessionRequest(String accessionRequest) {
        this.accessionRequest = accessionRequest;
    }

    /**
     * Gets created date.
     *
     * @return the created date
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets created date.
     *
     * @param createdDate the created date
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Gets accession status.
     *
     * @return the accession status
     */
    public String getAccessionStatus() {
        return accessionStatus;
    }

    /**
     * Sets accession status.
     *
     * @param accessionStatus the accession status
     */
    public void setAccessionStatus(String accessionStatus) {
        this.accessionStatus = accessionStatus;
    }
}
