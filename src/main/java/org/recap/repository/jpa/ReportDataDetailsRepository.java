package org.recap.repository.jpa;

import org.recap.model.jpa.ReportDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by angelind on 9/1/17.
 */
public interface ReportDataDetailsRepository extends JpaRepository<ReportDataEntity, Integer> {

    /**
     * Gets count of record num for matching monograph based on the given header name.
     *
     * @param headerName the header name
     * @return the count of record num for matching monograph
     */
    @Query(value = "select count(*) from report_data_t where " +
            "record_num in (select distinct RECORD_NUM from report_data_t " +
            "where HEADER_NAME = 'MaterialType' and HEADER_VALUE like 'Monograph,%' " +
            "and RECORD_NUM in (select record_num from report_t where type in ('SingleMatch','MultiMatch') and file_name not in ('PendingBibMatches'))) and header_name=?1", nativeQuery = true)
    long getCountOfRecordNumForMatchingMonograph(String headerName);

    /**
     * Gets count of record num for matching serials.
     *
     * @param headerName the header name
     * @return the count of record num for matching serials
     */
    @Query(value = "select count(*) from report_data_t where " +
            "record_num in (select distinct RECORD_NUM from report_data_t " +
            "where HEADER_NAME = 'MaterialType' and HEADER_VALUE like 'Serial,%' " +
            "and RECORD_NUM in (select record_num from report_t where type in ('SingleMatch','MultiMatch'))) and header_name=?1", nativeQuery = true)
    long getCountOfRecordNumForMatchingSerials(String headerName);

    /**
     * Gets count of record num for matching mv ms.
     *
     * @param headerName the header name
     * @return the count of record num for matching mv ms
     */
    @Query(value = "select count(*) from report_data_t where " +
            "record_num in (select distinct RECORD_NUM from report_data_t " +
            "where HEADER_NAME = 'MaterialType' and HEADER_VALUE like 'MonographicSet,%' " +
            "and RECORD_NUM in (select record_num from report_t where type in ('SingleMatch','MultiMatch'))) and header_name=?1", nativeQuery = true)
    long getCountOfRecordNumForMatchingMVMs(String headerName);

    /**
     * Gets count of record num for matching pending monograph.
     *
     * @param headerName the header name
     * @return the count of record num for matching pending monograph
     */
    @Query(value = "select count(*) from report_data_t where " +
            "record_num in (select distinct RECORD_NUM from report_data_t " +
            "where HEADER_NAME = 'MaterialType' and HEADER_VALUE like 'Monograph,%' " +
            "and RECORD_NUM in (select record_num from report_t where type in ('SingleMatch','MultiMatch') and file_name in ('PendingBibMatches'))) and header_name=?1", nativeQuery = true)
    long getCountOfRecordNumForMatchingPendingMonograph(String headerName);

    /**
     * Gets a list of report data entities for matching monographs based on the given header name and limit values.
     *
     * @param headerName the header name
     * @param from       the from
     * @param batchsize  the batchsize
     * @return the report data entity for matching monographs
     */
    @Query(value = "select * from report_data_t where record_num in (select distinct RECORD_NUM from report_data_t " +
            "where HEADER_NAME = 'MaterialType' and HEADER_VALUE like 'Monograph,%' " +
            "and RECORD_NUM in (select record_num from report_t where type in ('SingleMatch','MultiMatch') and file_name not in ('PendingBibMatches'))) " +
            "and header_name=?1 order by record_num limit ?2,?3", nativeQuery = true)
    List<ReportDataEntity> getReportDataEntityForMatchingMonographs(String headerName, long from, long batchsize);

    /**
     * Gets report data entity for pending matching monographs.
     *
     * @param headerName the header name
     * @param from       the from
     * @param batchsize  the batchsize
     * @return the report data entity for pending matching monographs
     */
    @Query(value = "select * from report_data_t where record_num in (select distinct RECORD_NUM from report_data_t " +
            "where HEADER_NAME = 'MaterialType' and HEADER_VALUE like 'Monograph,%' " +
            "and RECORD_NUM in (select record_num from report_t where type in ('SingleMatch','MultiMatch') and file_name in ('PendingBibMatches'))) " +
            "and header_name=?1 order by record_num limit ?2,?3", nativeQuery = true)
    List<ReportDataEntity> getReportDataEntityForPendingMatchingMonographs(String headerName, long from, long batchsize);

    /**
     * Gets a list of report data entities based on the given list of record nums and header name.
     *
     * @param recordNum  the record num
     * @param headerName the header name
     * @return the report data entity by record num in
     */
    @Query(value = "select * from report_data_t where record_num in (?1) and HEADER_NAME=?2", nativeQuery = true)
    List<ReportDataEntity> getReportDataEntityByRecordNumIn(List<Integer> recordNum, String headerName);

    /**
     * Gets a list of report data entities based on the given list of record nums and list of header names.
     *
     * @param recordNumList  the record num list
     * @param headerNameList the header name list
     * @return the records for matching bib info
     */
    @Query(value = "SELECT RDE FROM ReportDataEntity RDE WHERE recordNum IN (?1) AND headerName IN (?2)")
    List<ReportDataEntity> getRecordsForMatchingBibInfo(List<String> recordNumList,List<String> headerNameList);

    /**
     * Gets a list of report data entities for matching serials based on the given header name and limit values.
     *
     * @param headerName the header name
     * @param from       the from
     * @param batchsize  the batchsize
     * @return the report data entity for matching serials
     */
    @Query(value = "select * from report_data_t where record_num in (select distinct RECORD_NUM from report_data_t " +
            "where HEADER_NAME = 'MaterialType' and HEADER_VALUE like 'Serial,%' " +
            "and RECORD_NUM in (select record_num from report_t where type in ('SingleMatch','MultiMatch'))) " +
            "and header_name=?1 order by record_num limit ?2,?3", nativeQuery = true)
    List<ReportDataEntity> getReportDataEntityForMatchingSerials(String headerName, long from, long batchsize);

    /**
     * Gets a list of report data entities for matching monographicSet based on the given header name and limit values.
     *
     * @param headerName the header name
     * @param from       the from
     * @param batchsize  the batchsize
     * @return the report data entity for matching monographicSet
     */
    @Query(value = "select * from report_data_t where record_num in (select distinct RECORD_NUM from report_data_t " +
            "where HEADER_NAME = 'MaterialType' and HEADER_VALUE like 'MonographicSet,%'" +
            "and RECORD_NUM in (select record_num from report_t where type in ('SingleMatch','MultiMatch'))) " +
            "and header_name=?1 order by record_num limit ?2,?3", nativeQuery = true)
    List<ReportDataEntity> getReportDataEntityForMatchingMVMs(String headerName, long from, long batchsize);
}

