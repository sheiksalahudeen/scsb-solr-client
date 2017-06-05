package org.recap.controller;

import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.JobParamDataEntity;
import org.recap.model.jpa.JobParamEntity;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.report.ReportGenerator;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.JobParamDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by angelind on 28/4/17.
 */
@RestController
@RequestMapping("/generateReportService")
public class GenerateReportRestController {

    private static final Logger logger = LoggerFactory.getLogger(GenerateReportRestController.class);

    @Autowired
    private ReportGenerator reportGenerator;

    @Autowired
    private JobParamDetailRepository jobParamDetailRepository;

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    @RequestMapping(value = "/generateReports", method = RequestMethod.POST,  consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    private String generateReportsJob(@RequestBody SolrIndexRequest solrIndexRequest) {
        String status;
        Date fromDate = solrIndexRequest.getCreatedDate();
        Date toDate = new Date();
        JobParamEntity jobParamEntity = jobParamDetailRepository.findByJobName(solrIndexRequest.getProcessType());
        Map<String, String> jobParamMap = new HashMap<>();
        for(JobParamDataEntity jobParamDataEntity : jobParamEntity.getJobParamDataEntities()) {
            jobParamMap.put(jobParamDataEntity.getParamName(), jobParamDataEntity.getParamValue());
        }
        String transmissionType = jobParamMap.get(RecapConstants.TRANSMISSION_TYPE);
        String reportType = jobParamMap.get(RecapConstants.REPORT_TYPE);
        String fileName = jobParamMap.get(RecapConstants.JOB_PARAM_DATA_FILE_NAME);
        StringBuilder generateReportFileName = new StringBuilder();
        Iterable<InstitutionEntity> institutionEntities = institutionDetailsRepository.findByInstitutionCodeNotIn(Arrays.asList("HTC"));

        for (Iterator<InstitutionEntity> iterator = institutionEntities.iterator(); iterator.hasNext(); ) {
            InstitutionEntity institutionEntity = iterator.next();
            String generatedFileName = reportGenerator.generateReport(fileName, institutionEntity.getInstitutionCode(), reportType, transmissionType, fromDate, toDate);
            if(StringUtils.isNotBlank(generatedFileName)) {
                generateReportFileName.append(generatedFileName);
                if(iterator.hasNext()) {
                    generateReportFileName.append("\n");
                }
            }
        }
        if(StringUtils.isNotBlank(generateReportFileName.toString())) {
            logger.info("Created report fileNames : {}", generateReportFileName);
            status = "Report generated Successfully in FTP";
        } else {
            logger.info("No report files generated.");
            status = "There is no report to generate or Report Generation Failed";
        }

        return status;
    }

    @ResponseBody
    @RequestMapping(value="/generateSubmitCollectionReport", method = RequestMethod.POST)
    public String generateSubmitCollectionReport(@RequestBody List<Integer> reportRecordNumberList) {
       return reportGenerator.generateReportBasedOnReportRecordNum(reportRecordNumberList,RecapConstants.SUBMIT_COLLECTION,RecapConstants.FTP);
    }

}
