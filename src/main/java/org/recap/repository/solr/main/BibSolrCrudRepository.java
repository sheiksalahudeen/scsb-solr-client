package org.recap.repository.solr.main;

import org.recap.model.solr.Bib;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.List;

/**
 * Created by pvsubrah on 6/11/16.
 */
@RepositoryRestResource(collectionResourceRel = "bibSolr", path = "bibSolr")
public interface BibSolrCrudRepository extends SolrCrudRepository<Bib, String> {

    /**
     * Finds Bib based on the given bib id.
     *
     * @param bibId the bib id
     * @return the bib
     */
    Bib findByBibId(@Param("bibId") Integer bibId);

    /**
     * Finds a list of bib based on the given list of oclc number.
     *
     * @param oclcNumber the oclc number
     * @return the list
     */
    List<Bib> findByOclcNumber(String oclcNumber);

    /**
     * Finds a list of bib based on the given list of isbn.
     *
     * @param isbn the isbn
     * @return the list
     */
    List<Bib> findByIsbn(String isbn);

    /**
     * Find a list of bib based on the given list of  issn.
     *
     * @param issn the issn
     * @return the list
     */
    List<Bib> findByIssn(String issn);

    /**
     * Finds a list of bib based on the given list of lccn.
     *
     * @param lccn the lccn
     * @return the list
     */
    List<Bib> findByLccn(String lccn);

    /**
     * Counts the number of bibs for the given bib id.
     *
     * @param bibId the bib id
     * @return the long
     */
    Long countByBibId(Integer bibId);

    /**
     * Finds bib based on the given title display and list of oclc number.
     *
     * @param titleDisplay the title display
     * @param oclcNumber   the oclc number
     * @return the list
     */
    List<Bib> findByTitleDisplayAndOclcNumber(String titleDisplay, String oclcNumber);

    /**
     * Finds bib based on the given title display and list of isbn.
     *
     * @param titleDisplay the title display
     * @param isbn         the isbn
     * @return the list
     */
    List<Bib> findByTitleDisplayAndIsbn(String titleDisplay, String isbn);

    /**
     * Finds a list of bib based on the given title display and list of issn.
     *
     * @param titleDisplay the title display
     * @param issn         the issn
     * @return the list
     */
    List<Bib> findByTitleDisplayAndIssn(String titleDisplay, String issn);

    /**
     * Finds a list of bib based on the given title display and list of lccn.
     *
     * @param titleDisplay the title display
     * @param lccn         the lccn
     * @return the list
     */
    List<Bib> findByTitleDisplayAndLccn(String titleDisplay, String lccn);

    /**
     * Counts the number of bibs based on the given doc type.
     *
     * @param docType the doc type
     * @return the long
     */
    Long countByDocType(String docType);

    /**
     * Deletes bib based on the given list of bib ids.
     *
     * @param bibId the bib id
     * @return the int
     */
    int deleteByBibId(@Param("bibId") Integer bibId);

    /**
     * Deletes bib based on the given list of bib ids.
     *
     * @param bibIds the bib ids
     * @return the int
     */
    int deleteByBibIdIn(@Param("bibIds") List<Integer> bibIds);
}
