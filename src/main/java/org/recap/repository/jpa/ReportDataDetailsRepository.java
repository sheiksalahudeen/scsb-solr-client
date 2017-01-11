package org.recap.repository.jpa;

import org.recap.model.jpa.ReportDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by angelind on 9/1/17.
 */
public interface ReportDataDetailsRepository extends JpaRepository<ReportDataEntity, Integer> {

    @Query(value = "select count(*) from report_data_t where " +
            "record_num in (select distinct RECORD_NUM from report_data_t " +
            "where HEADER_NAME = 'MaterialType' and HEADER_VALUE like 'Monograph%' " +
            "and RECORD_NUM in (select record_num from report_t where type in ('SingleMatch','MultiMatch'))) and header_name=?1", nativeQuery = true)
    long getCountOfRecordNumForMatchingMonograph(String headerName);

    @Query(value = "select * from report_data_t where record_num in (select distinct RECORD_NUM from report_data_t " +
            "where HEADER_NAME = 'MaterialType' and HEADER_VALUE like 'Monograph%' " +
            "and RECORD_NUM in (select record_num from report_t where type in ('SingleMatch','MultiMatch'))) " +
            "and header_name=?1 order by record_num limit ?2,?3", nativeQuery = true)
    List<ReportDataEntity> getReportDataEntityForMatchingMonographs(String headerName, long from, long batchsize);

    @Query(value = "select * from report_data_t where record_num in (?1) and HEADER_NAME=?2", nativeQuery = true)
    List<ReportDataEntity> getReportDataEntityByRecordNumIn(List<Integer> recordNum, String headerName);
}

