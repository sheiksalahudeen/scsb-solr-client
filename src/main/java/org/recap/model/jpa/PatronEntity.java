package org.recap.model.jpa;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by rajeshbabuk on 26/10/16.
 */
@Entity
@Table(name = "patron_t", schema = "recap", catalog = "")
public class PatronEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PATRON_ID")
    private Integer patronId;

    @Column(name = "INST_IDENTIFIER")
    private String institutionIdentifier;

    @Column(name = "INST_ID")
    private Integer institutionId;

    @Column(name = "EMAIL_ID")
    private String emailId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "INST_ID", insertable = false, updatable = false)
    private InstitutionEntity institutionEntity;

    /**
     * Gets patron id.
     *
     * @return the patron id
     */
    public Integer getPatronId() {
        return patronId;
    }

    /**
     * Sets patron id.
     *
     * @param patronId the patron id
     */
    public void setPatronId(Integer patronId) {
        this.patronId = patronId;
    }

    /**
     * Gets institution identifier.
     *
     * @return the institution identifier
     */
    public String getInstitutionIdentifier() {
        return institutionIdentifier;
    }

    /**
     * Sets institution identifier.
     *
     * @param institutionIdentifier the institution identifier
     */
    public void setInstitutionIdentifier(String institutionIdentifier) {
        this.institutionIdentifier = institutionIdentifier;
    }

    /**
     * Gets institution id.
     *
     * @return the institution id
     */
    public Integer getInstitutionId() {
        return institutionId;
    }

    /**
     * Sets institution id.
     *
     * @param institutionId the institution id
     */
    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    /**
     * Gets email id.
     *
     * @return the email id
     */
    public String getEmailId() {
        return emailId;
    }

    /**
     * Sets email id.
     *
     * @param emailId the email id
     */
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    /**
     * Gets institution entity.
     *
     * @return the institution entity
     */
    public InstitutionEntity getInstitutionEntity() {
        return institutionEntity;
    }

    /**
     * Sets institution entity.
     *
     * @param institutionEntity the institution entity
     */
    public void setInstitutionEntity(InstitutionEntity institutionEntity) {
        this.institutionEntity = institutionEntity;
    }
}
