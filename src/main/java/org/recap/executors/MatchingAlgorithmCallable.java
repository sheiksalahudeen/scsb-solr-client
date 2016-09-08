package org.recap.executors;

import org.recap.RecapConstants;
import org.recap.matchingAlgorithm.service.MatchingAlgorithmHelperService;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.model.solr.Bib;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by angelind on 29/8/16.
 */
public class MatchingAlgorithmCallable implements Callable {

    private String fieldValue;
    private String fieldName;
    private String matchingFileName;
    private String exceptionReportFileName;
    private MatchingAlgorithmHelperService matchingAlgorithmHelperService;
    private Map<Integer, Map<String,ReportEntity>> matchingReportEntityMap = new HashMap();
    private Map<Integer, Map<String,ReportEntity>> exceptionReportEntityMap = new HashMap<>();

    public MatchingAlgorithmCallable(String fieldValue, String fieldName, String matchingFileName, String exceptionReportFileName, MatchingAlgorithmHelperService matchingAlgorithmHelperService,
                                     Map<Integer, Map<String, ReportEntity>> matchingReportEntityMap, Map<Integer, Map<String, ReportEntity>> exceptionReportEntityMap) {
        this.fieldValue = fieldValue;
        this.fieldName = fieldName;
        this.matchingFileName = matchingFileName;
        this.exceptionReportFileName = exceptionReportFileName;
        this.matchingAlgorithmHelperService = matchingAlgorithmHelperService;
        this.matchingReportEntityMap = matchingReportEntityMap;
        this.exceptionReportEntityMap = exceptionReportEntityMap;
    }

    @Override
    public Object call() throws Exception {
        Map<String, Object> responseMap = new HashMap<>();
        Set<Bib> matchingExceptionSet = new HashSet<>();
        Map<Integer, Map<String,ReportEntity>> reportEntityMatchingMap = new HashMap();
        Map<Integer, Map<String,ReportEntity>> reportEntityExceptionMap = new HashMap<>();
        Integer bibCount = 0;
        Integer itemCount = 0;
        try {
            List<Bib> bibs = matchingAlgorithmHelperService.getBibs(fieldName, fieldValue);
            Map<String, Set<Bib>> owningInstitutionMap = matchingAlgorithmHelperService.getMatchingBibsBasedOnTitle(bibs, matchingExceptionSet);
            if (owningInstitutionMap.size() > 1) {
                for (String owningInstitution : owningInstitutionMap.keySet()) {
                    Set<Bib> bibSet = owningInstitutionMap.get(owningInstitution);
                    for(Bib bib : bibSet) {
                        bibCount = bibCount + 1;
                        Integer bibId = bib.getBibId();
                        if(matchingReportEntityMap.containsKey(bibId)) {
                            Integer barcodeCount = addReportDataEntityToMap(fieldName, matchingReportEntityMap, fieldValue, bib, bibId, reportEntityMatchingMap);
                            itemCount = itemCount + barcodeCount;
                        } else {
                            Map<String, ReportEntity> barcodeReportEntityMap = matchingAlgorithmHelperService.populateReportEntity(fieldName, fieldValue, bib, matchingFileName,
                                    RecapConstants.MATCHING_TYPE);
                            itemCount = itemCount + barcodeReportEntityMap.size();
                            reportEntityMatchingMap.put(bibId, barcodeReportEntityMap);
                        }
                    }
                }
            }
            generateExceptionReport(fieldName, fieldValue, matchingExceptionSet, exceptionReportFileName, exceptionReportEntityMap, reportEntityExceptionMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseMap.put(RecapConstants.MATCHING_REPORT_ENTITY_MAP, reportEntityMatchingMap);
        responseMap.put(RecapConstants.EXCEPTION_REPORT_ENTITY_MAP, reportEntityExceptionMap);
        responseMap.put(RecapConstants.ITEM_COUNT, itemCount);
        responseMap.put(RecapConstants.BIB_COUNT, bibCount);
        return responseMap;
    }

    public void generateExceptionReport(String fieldName, String fieldValue, Set<Bib> matchingExceptionSet, String exceptionReportFileName, Map<Integer, Map<String, ReportEntity>> exceptionReportEntityMap, Map<Integer, Map<String, ReportEntity>> reportEntityExceptionMap) {
        if(!CollectionUtils.isEmpty(matchingExceptionSet)) {
            Map<String, Set<Bib>> owningInstitutionMap = matchingAlgorithmHelperService.getBibsForOwningInstitution(matchingExceptionSet);
            if(owningInstitutionMap.size() > 1) {
                for(String owningInstitution : owningInstitutionMap.keySet()) {
                    Set<Bib> bibSet = owningInstitutionMap.get(owningInstitution);
                    for(Bib bib : bibSet) {
                        Integer bibId = bib.getBibId();
                        if(exceptionReportEntityMap.containsKey(bibId)) {
                            addReportDataEntityToMap(fieldName, exceptionReportEntityMap, fieldValue, bib, bibId, reportEntityExceptionMap);
                        } else {
                            Map<String, ReportEntity> barcodeReportEntityMap = matchingAlgorithmHelperService.populateReportEntity(fieldName, fieldValue, bib, exceptionReportFileName,
                                    RecapConstants.EXCEPTION_TYPE);
                            reportEntityExceptionMap.put(bibId, barcodeReportEntityMap);
                        }
                    }
                }
            }
        }
    }

    private Integer addReportDataEntityToMap(String fieldName, Map<Integer, Map<String, ReportEntity>> reportEntityMap, String fieldValue, Bib bib, Integer bibId, Map<Integer, Map<String, ReportEntity>> entityMap) {
        Map<String, ReportEntity> barcodeReportEntityMap = reportEntityMap.get(bibId);
        for(String barcode : barcodeReportEntityMap.keySet()) {
            ReportEntity reportEntity = barcodeReportEntityMap.get(barcode);
            ReportDataEntity reportDataEntity = matchingAlgorithmHelperService.getMatchingFieldEntity(fieldName, fieldValue);
            reportEntity.addAll(Arrays.asList(reportDataEntity));
            barcodeReportEntityMap.put(barcode, reportEntity);
            entityMap.put(bib.getBibId(), barcodeReportEntityMap);
        }
        return barcodeReportEntityMap.size();
    }
}
