package org.recap.matchingalgorithm.service;

import com.google.common.collect.Lists;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.recap.RecapConstants;
import org.recap.matchingalgorithm.MatchingCounter;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.model.matchingReports.MatchingSerialAndMVMReports;
import org.recap.model.matchingReports.MatchingSummaryReport;
import org.recap.model.matchingReports.TitleExceptionReport;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.util.CsvUtil;
import org.recap.util.DateUtil;
import org.recap.util.OngoingMatchingAlgorithmReportGenerator;
import org.recap.util.SolrQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by angelind on 21/6/17.
 */
@Service
public class OngoingMatchingReportsService {

    private static final Logger logger= LoggerFactory.getLogger(OngoingMatchingReportsService.class);

    @Autowired
    private ReportDetailRepository reportDetailRepository;

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    private CsvUtil csvUtil;

    @Value("${ongoing.matching.report.directory}")
    private String matchingReportsDirectory;

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private SolrQueryBuilder solrQueryBuilder;

    /**
     * The Camel context.
     */
    @Autowired
    CamelContext camelContext;

    /**
     * The Institution details repository.
     */
    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    /**
     * The Producer template.
     */
    @Autowired
    ProducerTemplate producerTemplate;

    /**
     * Generate title exception report string.
     *
     * @param createdDate the created date
     * @param batchSize   the batch size
     * @return the string
     */
    public String generateTitleExceptionReport(Date createdDate, Integer batchSize) {
        Page<ReportEntity> reportEntityPage = reportDetailRepository.findByFileAndTypeAndDateRangeWithPaging(new PageRequest(0, batchSize), RecapConstants.ONGOING_MATCHING_ALGORITHM, RecapConstants.TITLE_EXCEPTION_TYPE,
                dateUtil.getFromDate(createdDate), dateUtil.getToDate(createdDate));
        int totalPages = reportEntityPage.getTotalPages();
        List<TitleExceptionReport> titleExceptionReports = new ArrayList<>();
        int maxTitleCount = 0;
        maxTitleCount = getTitleExceptionReport(reportEntityPage.getContent(), titleExceptionReports, maxTitleCount);
        for(int pageNum=1; pageNum<totalPages; pageNum++) {
            reportEntityPage = reportDetailRepository.findByFileAndTypeAndDateRangeWithPaging(new PageRequest(pageNum, batchSize), RecapConstants.ONGOING_MATCHING_ALGORITHM, RecapConstants.TITLE_EXCEPTION_TYPE,
                    dateUtil.getFromDate(createdDate), dateUtil.getToDate(createdDate));
            maxTitleCount = getTitleExceptionReport(reportEntityPage.getContent(), titleExceptionReports, maxTitleCount);
        }
        File file = null;
        if(CollectionUtils.isNotEmpty(titleExceptionReports)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(RecapConstants.DATE_FORMAT_FOR_FILE_NAME);
                String formattedDate = sdf.format(createdDate);
                String fileNameWithExtension = matchingReportsDirectory + File.separator + RecapConstants.TITLE_EXCEPTION_REPORT + RecapConstants.UNDER_SCORE + formattedDate + RecapConstants.CSV_EXTENSION;
                file = csvUtil.createTitleExceptionReportFile(fileNameWithExtension, maxTitleCount, titleExceptionReports);
                camelContext.startRoute(RecapConstants.FTP_TITLE_EXCEPTION_REPORT_ROUTE_ID);
            } catch (Exception e) {
                logger.error("Exception : {}", e);
            }
        }
        return file != null ? file.getName() : null;
    }


    private int getTitleExceptionReport(List<ReportEntity> reportEntities, List<TitleExceptionReport> titleExceptionReports, int maxTitleCount) {
        if(CollectionUtils.isNotEmpty(reportEntities)) {
            for(ReportEntity reportEntity : reportEntities) {
                List<ReportDataEntity> reportDataEntities = new ArrayList<>();
                List<String> titleList = new ArrayList<>();
                for(ReportDataEntity reportDataEntity : reportEntity.getReportDataEntities()) {
                    String headerName = reportDataEntity.getHeaderName();
                    String headerValue = reportDataEntity.getHeaderValue();
                    if(headerName.contains("Title")) {
                        titleList.add(headerValue);
                    } else {
                        reportDataEntities.add(reportDataEntity);
                    }
                }
                int size = titleList.size();
                if(maxTitleCount < size) {
                    maxTitleCount = size;
                }
                OngoingMatchingAlgorithmReportGenerator ongoingMatchingAlgorithmReportGenerator = new OngoingMatchingAlgorithmReportGenerator();
                TitleExceptionReport titleExceptionReport = ongoingMatchingAlgorithmReportGenerator.prepareTitleExceptionReportRecord(reportDataEntities);
                titleExceptionReport.setTitleList(titleList);
                titleExceptionReports.add(titleExceptionReport);
            }
        }
        return maxTitleCount;
    }

    /**
     * Generate serial and mv ms report.
     *
     * @param serialMvmBibIds the serial mvm bib ids
     */
    public void generateSerialAndMVMsReport(List<Integer> serialMvmBibIds) {
        List<MatchingSerialAndMVMReports> matchingSerialAndMvmReports = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(serialMvmBibIds)) {
            List<List<Integer>> bibIdLists = Lists.partition(serialMvmBibIds, 100);
            for(List<Integer> bibIds : bibIdLists) {
                String bibIdQuery = RecapConstants.BIB_ID + ":" + "(" + StringUtils.join(bibIds, " ") + ")";
                SolrQuery solrQuery = new SolrQuery(bibIdQuery);
                String[] fieldNameList = {RecapConstants.TITLE_SUBFIELD_A, RecapConstants.BIB_ID, RecapConstants.BIB_OWNING_INSTITUTION, RecapConstants.OWNING_INST_BIB_ID, RecapConstants.ROOT};
                solrQuery.setFields(fieldNameList);
                solrQuery.setRows(100);
                try {
                    QueryResponse queryResponse = solrTemplate.getSolrClient().query(solrQuery);
                    SolrDocumentList solrDocumentList = queryResponse.getResults();
                    for (Iterator<SolrDocument> iterator = solrDocumentList.iterator(); iterator.hasNext(); ) {
                        SolrDocument solrDocument = iterator.next();
                        matchingSerialAndMvmReports.addAll(getMatchingSerialAndMvmReports(solrDocument));
                    }
                } catch (Exception e) {
                    logger.error("Exception : " + e);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(matchingSerialAndMvmReports)) {
            try {
                camelContext.startRoute(RecapConstants.FTP_SERIAL_MVM_REPORT_ROUTE_ID);
                producerTemplate.sendBodyAndHeader(RecapConstants.FTP_SERIAL_MVM_REPORT_Q, matchingSerialAndMvmReports, RecapConstants.FILE_NAME, RecapConstants.MATCHING_SERIAL_MVM_REPORT);
            } catch (Exception e) {
                logger.error("Exception : {}", e);
            }
        }
    }

    private List<MatchingSerialAndMVMReports> getMatchingSerialAndMvmReports(SolrDocument solrDocument) {

        List<MatchingSerialAndMVMReports> matchingSerialAndMVMReportsList = new ArrayList<>();
        SolrQuery solrQueryForChildDocuments = solrQueryBuilder.getSolrQueryForBibItem(RecapConstants.ROOT + ":" + solrDocument.getFieldValue(RecapConstants.ROOT));
        solrQueryForChildDocuments.setFilterQueries(RecapConstants.DOCTYPE + ":" + "(\"" + RecapConstants.HOLDINGS + "\" \"" + RecapConstants.ITEM + "\")");
        String[] fieldNameList = {RecapConstants.VOLUME_PART_YEAR, RecapConstants.HOLDING_ID, RecapConstants.SUMMARY_HOLDINGS, RecapConstants.BARCODE,
                RecapConstants.USE_RESTRICTION_DISPLAY, RecapConstants.ITEM_ID, RecapConstants.ROOT, RecapConstants.DOCTYPE, RecapConstants.HOLDINGS_ID};
        solrQueryForChildDocuments.setFields(fieldNameList);
        solrQueryForChildDocuments.setSort(RecapConstants.DOCTYPE, SolrQuery.ORDER.asc);
        QueryResponse queryResponse = null;
        try {
            queryResponse = solrTemplate.getSolrClient().query(solrQueryForChildDocuments);
            SolrDocumentList solrDocuments = queryResponse.getResults();
            if (solrDocuments.getNumFound() > 10) {
                solrQueryForChildDocuments.setRows((int) solrDocuments.getNumFound());
                queryResponse = solrTemplate.getSolrClient().query(solrQueryForChildDocuments);
                solrDocuments = queryResponse.getResults();
            }
            Map<Integer, String> holdingsMap = new HashMap<>();
            for (Iterator<SolrDocument> iterator = solrDocuments.iterator(); iterator.hasNext(); ) {
                SolrDocument solrChildDocument =  iterator.next();
                String docType = (String) solrChildDocument.getFieldValue(RecapConstants.DOCTYPE);
                if(docType.equalsIgnoreCase(RecapConstants.HOLDINGS)) {
                    holdingsMap.put((Integer) solrChildDocument.getFieldValue(RecapConstants.HOLDING_ID),
                            String.valueOf(solrChildDocument.getFieldValue(RecapConstants.SUMMARY_HOLDINGS)));
                }
                if(docType.equalsIgnoreCase(RecapConstants.ITEM)) {
                    MatchingSerialAndMVMReports matchingSerialAndMVMReports = new MatchingSerialAndMVMReports();
                    matchingSerialAndMVMReports.setTitle(String.valueOf(solrDocument.getFieldValue(RecapConstants.TITLE_SUBFIELD_A)));
                    matchingSerialAndMVMReports.setBibId(String.valueOf(solrDocument.getFieldValue(RecapConstants.BIB_ID)));
                    matchingSerialAndMVMReports.setOwningInstitutionId(String.valueOf(solrDocument.getFieldValue(RecapConstants.BIB_OWNING_INSTITUTION)));
                    matchingSerialAndMVMReports.setOwningInstitutionBibId(String.valueOf(solrDocument.getFieldValue(RecapConstants.OWNING_INST_BIB_ID)));
                    matchingSerialAndMVMReports.setBarcode(String.valueOf(solrChildDocument.getFieldValue(RecapConstants.BARCODE)));
                    matchingSerialAndMVMReports.setVolumePartYear(String.valueOf(solrChildDocument.getFieldValue(RecapConstants.VOLUME_PART_YEAR)));
                    matchingSerialAndMVMReports.setUseRestriction(String.valueOf(solrChildDocument.getFieldValue(RecapConstants.USE_RESTRICTION_DISPLAY)));
                    List<Integer> holdingsIds = (List<Integer>) solrChildDocument.getFieldValue(RecapConstants.HOLDINGS_ID);
                    Integer holdingsId = holdingsIds.get(0);
                    matchingSerialAndMVMReports.setSummaryHoldings(holdingsMap.get(holdingsId));
                    matchingSerialAndMVMReportsList.add(matchingSerialAndMVMReports);
                }
            }
        }catch (Exception e) {
            logger.error("Exception : {}", e);
        }
        return matchingSerialAndMVMReportsList;
    }

    /**
     * Populate summary report list.
     *
     * @return the list
     */
    public List<MatchingSummaryReport> populateSummaryReport() {
        List<MatchingSummaryReport> matchingSummaryReports = new ArrayList<>();
        Iterable<InstitutionEntity> institutionEntities = institutionDetailsRepository.findByInstitutionCodeNotIn(Arrays.asList("HTC"));
        for (Iterator<InstitutionEntity> iterator = institutionEntities.iterator(); iterator.hasNext(); ) {
            InstitutionEntity institutionEntity = iterator.next();
            String institutionCode = institutionEntity.getInstitutionCode();
            MatchingSummaryReport matchingSummaryReport = new MatchingSummaryReport();
            matchingSummaryReport.setInstitution(institutionCode);
            if(institutionCode.equalsIgnoreCase(RecapConstants.PRINCETON)) {
                matchingSummaryReport.setOpenItemsBeforeMatching(String.valueOf(MatchingCounter.getPulOpenCount()));
                matchingSummaryReport.setSharedItemsBeforeMatching(String.valueOf(MatchingCounter.getPulSharedCount()));
            } else if(institutionCode.equalsIgnoreCase(RecapConstants.COLUMBIA)) {
                matchingSummaryReport.setSharedItemsBeforeMatching(String.valueOf(MatchingCounter.getCulSharedCount()));
                matchingSummaryReport.setOpenItemsBeforeMatching(String.valueOf(MatchingCounter.getCulOpenCount()));
            } else if(institutionCode.equalsIgnoreCase(RecapConstants.NYPL)) {
                matchingSummaryReport.setOpenItemsBeforeMatching(String.valueOf(MatchingCounter.getNyplOpenCount()));
                matchingSummaryReport.setSharedItemsBeforeMatching(String.valueOf(MatchingCounter.getNyplSharedCount()));
            }
            matchingSummaryReports.add(matchingSummaryReport);
        }
        return matchingSummaryReports;
    }

    /**
     * Generate summary report.
     *
     * @param matchingSummaryReports the matching summary reports
     */
    public void generateSummaryReport(List<MatchingSummaryReport> matchingSummaryReports) {
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        Integer bibCount = 0;
        Integer itemCount = 0;
        SolrQuery bibCountQuery = solrQueryBuilder.getCountQueryForParentAndChildCriteria(searchRecordsRequest);
        SolrQuery itemCountQuery = solrQueryBuilder.getCountQueryForChildAndParentCriteria(searchRecordsRequest);
        bibCountQuery.setRows(0);
        itemCountQuery.setRows(0);
        try {
            QueryResponse queryResponseForBib = solrTemplate.getSolrClient().query(bibCountQuery);
            QueryResponse queryResponseForItem = solrTemplate.getSolrClient().query(itemCountQuery);
            bibCount = Math.toIntExact(queryResponseForBib.getResults().getNumFound());
            itemCount = Math.toIntExact(queryResponseForItem.getResults().getNumFound());
        } catch (Exception e) {
            logger.error("Exception : {}", e);
        }
        for(MatchingSummaryReport matchingSummaryReport : matchingSummaryReports) {
            matchingSummaryReport.setTotalBibs(String.valueOf(bibCount));
            matchingSummaryReport.setTotalItems(String.valueOf(itemCount));
            if(matchingSummaryReport.getInstitution().equalsIgnoreCase(RecapConstants.PRINCETON)) {
                matchingSummaryReport.setOpenItemsAfterMatching(String.valueOf(MatchingCounter.getPulOpenCount()));
                matchingSummaryReport.setSharedItemsAfterMatching(String.valueOf(MatchingCounter.getPulSharedCount()));
            } else if(matchingSummaryReport.getInstitution().equalsIgnoreCase(RecapConstants.COLUMBIA)) {
                matchingSummaryReport.setOpenItemsAfterMatching(String.valueOf(MatchingCounter.getCulOpenCount()));
                matchingSummaryReport.setSharedItemsAfterMatching(String.valueOf(MatchingCounter.getCulSharedCount()));
            } else if(matchingSummaryReport.getInstitution().equalsIgnoreCase(RecapConstants.NYPL)) {
                matchingSummaryReport.setOpenItemsAfterMatching(String.valueOf(MatchingCounter.getNyplOpenCount()));
                matchingSummaryReport.setSharedItemsAfterMatching(String.valueOf(MatchingCounter.getNyplSharedCount()));
            }
        }
        try {
            camelContext.startRoute(RecapConstants.FTP_MATCHING_SUMMARY_REPORT_ROUTE_ID);
            producerTemplate.sendBodyAndHeader(RecapConstants.FTP_MATCHING_SUMMARY_REPORT_Q, matchingSummaryReports, RecapConstants.FILE_NAME, RecapConstants.MATCHING_SUMMARY_REPORT);
        } catch (Exception e) {
            logger.error("Exception : {}", e);
        }
    }
}
