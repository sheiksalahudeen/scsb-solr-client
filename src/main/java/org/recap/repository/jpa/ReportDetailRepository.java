package org.recap.repository.jpa;

import org.recap.model.jpa.ReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Date;
import java.util.List;

/**
 * Created by SheikS on 8/8/2016.
 */
@RepositoryRestResource(collectionResourceRel = "report", path = "report")
public interface ReportDetailRepository extends JpaRepository<ReportEntity, Integer> {

    /**
     * Finds a list of report entities based on the given list of record numbers.
     *
     * @param recordNumbers the record numbers
     * @return the list
     */
    List<ReportEntity> findByRecordNumberIn(List<Integer> recordNumbers);

    /**
     * Finds ReportEntity based on the given file name.
     *
     * @param fileName the file name
     * @return the list
     */
    List<ReportEntity> findByFileName(String fileName);

    /**
     * Finds ReportEntity based on the given file name and type.
     *
     * @param fileName the file name
     * @param type     the type
     * @return the list
     */
    List<ReportEntity> findByFileNameAndType(String fileName, String type);

    /**
     * Finds a list of report entities based on the given file name and institution name.
     *
     * @param fileName        the file name
     * @param institutionName the institution name
     * @return the list
     */
    List<ReportEntity> findByFileNameAndInstitutionName(String fileName, String institutionName);

    /**
     * Finds ReportEntity based on the given file name,institution name and type.
     *
     * @param fileName        the file name
     * @param institutionName the institution name
     * @param type            the type
     * @return the list
     */
    List<ReportEntity> findByFileNameAndInstitutionNameAndType(String fileName, String institutionName, String type);

    /**
     * Finds a list of ReportEntity based on the given file and date range.
     *
     * @param fileName the file name
     * @param from     the from
     * @param to       the to
     * @return the list
     */
    @Query(value = "select * from report_t where FILE_NAME=?1 and CREATED_DATE >= ?2 and CREATED_DATE <= ?3",  nativeQuery = true)
    List<ReportEntity> findByFileAndDateRange(String fileName, Date from, Date to);

    /**
     * Finds a list of ReportEntities based on the given type and date range.
     *
     * @param type the type
     * @param from the from
     * @param to   the to
     * @return the list
     */
    @Query(value = "select * from report_t where TYPE=?1 and CREATED_DATE >= ?2 and CREATED_DATE <= ?3", nativeQuery = true)
    List<ReportEntity> findByTypeAndDateRange(@Param("type") String type, @Param("from") String from, @Param("to") String to);

    /**
     * Finds ReportEntity based on the given file,type and date range.
     *
     * @param fileName the file name
     * @param type     the type
     * @param from     the from
     * @param to       the to
     * @return the list
     */
    @Query(value = "select * from report_t where FILE_NAME=?1 and TYPE=?2 and CREATED_DATE >= ?3 and CREATED_DATE <= ?4", nativeQuery = true)
    List<ReportEntity> findByFileAndTypeAndDateRange(String fileName, String type, Date from, Date to);

    /**
     * Find by file and type and date range with paging page.
     *
     * @param pageable the pageable
     * @param fileName the file name
     * @param type     the type
     * @param from     the from
     * @param to       the to
     * @return the page
     */
    @Query(value = "select reportEntity from ReportEntity reportEntity where fileName=?1 and type=?2 and createdDate between ?3 and ?4")
    Page<ReportEntity> findByFileAndTypeAndDateRangeWithPaging(Pageable pageable, String fileName, String type, Date from, Date to);

    /**
     * Finds ReportEntity based on the given filename,type and date range.
     *
     * @param fileName the file name
     * @param type     the type
     * @param from     the from
     * @param to       the to
     * @return the list
     */
    @Query(value = "select * from report_t where FILE_NAME like ?1 and TYPE=?2 and CREATED_DATE >= ?3 and CREATED_DATE <= ?4", nativeQuery = true)
    List<ReportEntity> findByFileLikeAndTypeAndDateRange(String fileName, String type, Date from, Date to);

    /**
     * Finds a list of report entities based on the given filename ,institution ,type and date range.
     *
     * @param fileName        the file name
     * @param institutionName the institution name
     * @param type            the type
     * @param from            the from
     * @param to              the to
     * @return the list
     */
    @Query(value = "select * from report_t where FILE_NAME=?1 and INSTITUTION_NAME=?2 and TYPE=?3 and CREATED_DATE >= ?4 and CREATED_DATE <= ?5", nativeQuery = true)
    List<ReportEntity> findByFileAndInstitutionAndTypeAndDateRange(String fileName, String institutionName, String type, Date from, Date to);

    /**
     * Finds a list of report entities based on the given institution,type and date range.
     *
     * @param institutionName the institution name
     * @param type            the type
     * @param from            the from
     * @param to              the to
     * @return the list
     */
    @Query(value = "select * from report_t where INSTITUTION_NAME=?1 and TYPE=?2 and CREATED_DATE >= ?3 and CREATED_DATE <= ?4", nativeQuery = true)
    List<ReportEntity> findByInstitutionAndTypeAndDateRange(String institutionName, String type, Date from, Date to);

    /**
     * Deletes report data entities for the record num based on the given list of types.
     *
     * @param types the types
     */
    @Query(value = "delete from report_data_t where record_num in (select record_num from report_t where type in (?1))", nativeQuery = true)
    void deleteReportDataEntitiesByTypeAndFileName(List<String> types);

    /**
     * Delete report entities based on the given list of types.
     *
     * @param types the types
     */
    @Query(value = "delete from report_t where type in (?1)", nativeQuery = true)
    void deleteReportEntitiesByTypeAndFileName(List<String> types);

    /**
     * Finds count on 'report_t' table based on the given file name and type.
     *
     * @param fileName the file name
     * @param type     the type
     * @return the long
     */
    @Query(value = "select count(*) from report_t where FILE_NAME=?1 and TYPE=?2", nativeQuery = true)
    long findCountByFileNameAndType(String fileName, String type);

    /**
     * Finds list of report entities based on the given file name,type and range.
     *
     * @param fileName the file name
     * @param type     the type
     * @param from     the from
     * @param to       the to
     * @return the list
     */
    @Query(value = "select * from report_t where FILE_NAME=?1 and TYPE=?2 limit ?3,?4", nativeQuery = true)
    List<ReportEntity> findByFileNameAndTypeAndRange(String fileName, String type, long from, long to);

    /**
     * Gets the count of record number based on the given list of types.
     *
     * @param typeList the type list
     * @return the count by type
     */
    @Query(value = "SELECT COUNT(recordNumber) FROM ReportEntity WHERE TYPE IN (?1)")
    Integer getCountByType(List<String> typeList);

    /**
     * Gets the count of record number based on the given list of type,file name and date range.
     *
     * @param typeList the type list
     * @param fileName the file name
     * @param from     the from
     * @param to       the to
     * @return the count by type and file name and date range
     */
    @Query(value = "SELECT COUNT(recordNumber) FROM ReportEntity WHERE type IN (?1) AND fileName = ?2 AND createdDate between ?3 and ?4")
    Integer getCountByTypeAndFileNameAndDateRange(List<String> typeList, String fileName, Date from, Date to);

    /**
     * Gets record number for the given list of types.
     *
     * @param pageable the pageable
     * @param typeList the type list
     * @return the record num by type
     */
    @Query(value = "SELECT recordNumber FROM ReportEntity WHERE TYPE IN (?1)")
    Page<Integer> getRecordNumByType(Pageable pageable,List<String> typeList);

    /**
     * Gets record number based on the given type ,file name and date range.
     *
     * @param pageable the pageable
     * @param typeList the type list
     * @param fileName the file name
     * @param from     the from
     * @param to       the to
     * @return the record num by type and file name and date range
     */
    @Query(value = "SELECT recordNumber from ReportEntity WHERE type IN (?1) AND fileName = ?2 AND createdDate between ?3 and ?4")
    Page<Integer> getRecordNumByTypeAndFileNameAndDateRange(Pageable pageable, List<String> typeList, String fileName, Date from, Date to);

    /**
     * Finds list of report entities based on the given institution,file name,report type and date range.
     *
     * @param fileName        the file name
     * @param institutionName the institution name
     * @param type            the type
     * @param from            the from
     * @param to              the to
     * @return the list
     */
    @Query(value = "select * from report_t where FILE_NAME like ?1 and INSTITUTION_NAME=?2 and TYPE=?3 and CREATED_DATE >= ?4 and CREATED_DATE <= ?5", nativeQuery = true)
    List<ReportEntity> findByFileLikeAndInstitutionAndTypeAndDateRange(String fileName, String institutionName, String type, Date from, Date to);
}
