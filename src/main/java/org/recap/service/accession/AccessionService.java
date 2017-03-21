package org.recap.service.accession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.marc4j.marc.Record;
import org.recap.RecapConstants;
import org.recap.converter.MarcToBibEntityConverter;
import org.recap.converter.SCSBToBibEntityConverter;
import org.recap.converter.XmlToBibEntityConverterInterface;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jaxb.JAXBHandler;
import org.recap.model.jaxb.marc.BibRecords;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.*;
import org.recap.service.partnerservice.ColumbiaService;
import org.recap.service.partnerservice.NYPLService;
import org.recap.service.partnerservice.PrincetonService;
import org.recap.util.MarcUtil;
import org.recap.util.OngoingMatchingAlgorithmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by chenchulakshmig on 20/10/16.
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AccessionService {

    private static final Logger logger = LoggerFactory.getLogger(AccessionService.class);

    @Autowired
    private MarcToBibEntityConverter marcToBibEntityConverter;

    @Autowired
    private SCSBToBibEntityConverter scsbToBibEntityConverter;

    @Autowired
    private CustomerCodeDetailsRepository customerCodeDetailsRepository;

    @Autowired
    private ReportDetailRepository reportDetailRepository;

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    @Autowired
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Autowired
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Autowired
    private PrincetonService princetonService;

    @Autowired
    private ColumbiaService columbiaService;

    @Autowired
    private NYPLService nyplService;

    @Autowired
    private MarcUtil marcUtil;

    @Autowired
    private SolrIndexService solrIndexService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private OngoingMatchingAlgorithmUtil ongoingMatchingAlgorithmUtil;

    private Map<String,Integer> institutionEntityMap;

    private Map<String,Integer> itemStatusMap;

    private Map<String,Integer> collectionGroupMap;

    public MarcUtil getMarcUtil() {
        return marcUtil;
    }

    public MarcToBibEntityConverter getMarcToBibEntityConverter() {
        return marcToBibEntityConverter;
    }

    public SCSBToBibEntityConverter getScsbToBibEntityConverter() {
        return scsbToBibEntityConverter;
    }

    public ReportDetailRepository getReportDetailRepository() {
        return reportDetailRepository;
    }

    public SolrIndexService getSolrIndexService() {
        return solrIndexService;
    }

    public CustomerCodeDetailsRepository getCustomerCodeDetailsRepository() {
        return customerCodeDetailsRepository;
    }

    public BibliographicDetailsRepository getBibliographicDetailsRepository() {
        return bibliographicDetailsRepository;
    }

    public InstitutionDetailsRepository getInstitutionDetailsRepository() {
        return institutionDetailsRepository;
    }

    public PrincetonService getPrincetonService() {
        return princetonService;
    }

    public void setPrincetonService(PrincetonService princetonService) {
        this.princetonService = princetonService;
    }

    public ColumbiaService getColumbiaService() {
        return columbiaService;
    }

    public void setColumbiaService(ColumbiaService columbiaService) {
        this.columbiaService = columbiaService;
    }

    public NYPLService getNyplService() {
        return nyplService;
    }

    public void setNyplService(NYPLService nyplService) {
        this.nyplService = nyplService;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public String getOwningInstitution(String customerCode) {
        String owningInstitution = null;
        try {
            CustomerCodeEntity customerCodeEntity = getCustomerCodeDetailsRepository().findByCustomerCode(customerCode);
            if (null != customerCodeEntity) {
                owningInstitution = customerCodeEntity.getInstitutionEntity().getInstitutionCode();
            }
        } catch (Exception e) {
            logger.error(RecapConstants.EXCEPTION,e);
        }
        return owningInstitution;
    }

    @Transactional
    public List<AccessionResponse> processRequest(List<AccessionRequest> accessionRequestList) {
        String response = null;
        List<AccessionResponse> accessionResponsesList = new ArrayList<>();
        String bibDataResponse;
        List<Map<String, String>> responseMapList = new ArrayList<>();
        String owningInstitution = null;
        List<ReportDataEntity> reportDataEntityList = new ArrayList<>();
        for (AccessionRequest accessionRequest : accessionRequestList) {
            owningInstitution = getOwningInstitution(accessionRequest.getCustomerCode());
            List<ItemEntity> itemEntityList = getItemEntityList(accessionRequest);
            boolean itemExists = checkItemBarcodeAlreadyExist(itemEntityList);
            boolean isDeaccessionedItem = isItemDeaccessioned(itemEntityList);
            AccessionResponse accessionResponse = new AccessionResponse();
            if (!itemExists) {
                if (owningInstitution == null) {
                    setAccessionResponse(accessionResponsesList, accessionRequest, accessionResponse, accessionRequest.getCustomerCode() + " " + RecapConstants.CUSTOMER_CODE_DOESNOT_EXIST);
                    reportDataEntityList.addAll(createReportDataEntityList(accessionRequest, RecapConstants.CUSTOMER_CODE_DOESNOT_EXIST));
                } else {
                    try {
                        if (owningInstitution != null && owningInstitution.equalsIgnoreCase(RecapConstants.PRINCETON)) {
                            StopWatch stopWatch = new StopWatch();
                            stopWatch.start();
                            bibDataResponse = getPrincetonService().getBibData(accessionRequest.getItemBarcode());
                            stopWatch.stop();
                            logger.info("Time taken to get bib data from ils : {}" ,stopWatch.getTotalTimeSeconds());
                            response = processAccessionForMarcXml(accessionResponsesList, bibDataResponse, responseMapList, owningInstitution, reportDataEntityList, accessionRequest, accessionResponse);
                        } else if (owningInstitution != null && owningInstitution.equalsIgnoreCase(RecapConstants.COLUMBIA)) {
                            StopWatch stopWatch = new StopWatch();
                            stopWatch.start();
                            bibDataResponse = getColumbiaService().getBibData(accessionRequest.getItemBarcode());
                            stopWatch.stop();
                            logger.info("Time taken to get bib data from ils : {}", stopWatch.getTotalTimeSeconds());
                            response = processAccessionForMarcXml(accessionResponsesList, bibDataResponse, responseMapList, owningInstitution, reportDataEntityList, accessionRequest, accessionResponse);
                        } else if (owningInstitution != null && owningInstitution.equalsIgnoreCase(RecapConstants.NYPL)) {
                            StopWatch stopWatch1 = new StopWatch();
                            stopWatch1.start();
                            bibDataResponse = getNyplService().getBibData(accessionRequest.getItemBarcode(), accessionRequest.getCustomerCode());
                            stopWatch1.stop();
                            logger.info("Total Time taken to get bib data from ils : {}", stopWatch1.getTotalTimeSeconds());
                            response = processAccessionForSCSBXml(accessionResponsesList, bibDataResponse, responseMapList, owningInstitution, reportDataEntityList, accessionRequest, accessionResponse);
                        }
                    } catch (Exception ex) {
                        logger.error(RecapConstants.LOG_ERROR, ex);
                        response = ex.getMessage();
                        setAccessionResponse(accessionResponsesList, accessionRequest, accessionResponse, response);
                        reportDataEntityList.addAll(createReportDataEntityList(accessionRequest, response));
                    }
                    //Create dummy record
                    createDummyRecordIfAny(response, owningInstitution, reportDataEntityList, accessionRequest, accessionResponse);
                    generateAccessionSummaryReport(responseMapList, owningInstitution);
                }
            } else if (isDeaccessionedItem) {
                response = reAccessionItem(itemEntityList);
                if (response.equals(RecapConstants.SUCCESS)) {
                    response = indexReaccessionedItem(itemEntityList);
                }
                setAccessionResponse(accessionResponsesList, accessionRequest, accessionResponse, response);
            } else {
                setAccessionResponse(accessionResponsesList, accessionRequest, accessionResponse, RecapConstants.ITEM_ALREADY_ACCESSIONED);
                reportDataEntityList.addAll(createReportDataEntityList(accessionRequest, RecapConstants.ITEM_ALREADY_ACCESSIONED));
            }
        }
        saveReportEntity(owningInstitution, reportDataEntityList);
        return accessionResponsesList;
    }

    private String processAccessionForMarcXml(List<AccessionResponse> accessionResponsesList, String bibDataResponse, List<Map<String, String>> responseMapList, String owningInstitution, List<ReportDataEntity> reportDataEntityList, AccessionRequest accessionRequest, AccessionResponse accessionResponse) {
        StopWatch stopWatch;
        String response = null;
        stopWatch = new StopWatch();
        stopWatch.start();
        List<Record> records = new ArrayList<>();
        if (StringUtils.isNotBlank(bibDataResponse)) {
            records = getMarcUtil().readMarcXml(bibDataResponse);
        }
        if (CollectionUtils.isNotEmpty(records)) {
            for (Record record : records) {
                response = updateData(record, owningInstitution, responseMapList, accessionRequest);
                setAccessionResponse(accessionResponsesList,accessionRequest,accessionResponse,response);
                reportDataEntityList.addAll(createReportDataEntityList(accessionRequest, response));
            }
            stopWatch.stop();
            logger.info("Total time taken to save records for accession : {}", stopWatch.getTotalTimeSeconds());
        }
        return response;
    }

    private String processAccessionForSCSBXml(List<AccessionResponse> accessionResponsesList, String bibDataResponse, List<Map<String, String>> responseMapList, String owningInstitution, List<ReportDataEntity> reportDataEntityList, AccessionRequest accessionRequest, AccessionResponse accessionResponse) throws Exception {
        String response = null;
        BibRecords bibRecords = (BibRecords) JAXBHandler.getInstance().unmarshal(bibDataResponse, BibRecords.class);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (BibRecord bibRecord : bibRecords.getBibRecords()) {
            response = updateData(bibRecord, owningInstitution, responseMapList, accessionRequest);
            setAccessionResponse(accessionResponsesList, accessionRequest, accessionResponse, response);
            reportDataEntityList.addAll(createReportDataEntityList(accessionRequest, response));
        }
        stopWatch.stop();
        logger.info("Total time taken to save records for accession : {}", stopWatch.getTotalTimeSeconds());
        return response;
    }

    private List<ItemEntity> getItemEntityList(AccessionRequest accessionRequest){
        return itemDetailsRepository.findByBarcodeAndCustomerCode(accessionRequest.getItemBarcode(),accessionRequest.getCustomerCode());
    }

    private boolean checkItemBarcodeAlreadyExist(List<ItemEntity> itemEntityList){
        boolean itemExists = false;
        if (itemEntityList != null && !itemEntityList.isEmpty()) {
            itemExists = true;
        }
        return itemExists;
    }

    private boolean isItemDeaccessioned(List<ItemEntity> itemEntityList){
        boolean itemDeleted = false;
        if (itemEntityList != null && !itemEntityList.isEmpty()) {
            for(ItemEntity itemEntity : itemEntityList){
                return itemEntity.isDeleted();
            }
        }
        return itemDeleted;
    }

    private void createDummyRecordIfAny(String response, String owningInstitution, List<ReportDataEntity> reportDataEntityList, AccessionRequest accessionRequest,AccessionResponse accessionResponse) {
        String responseString;
        if (response != null && response.equals(RecapConstants.ITEM_BARCODE_NOT_FOUND_MSG)) {
            BibliographicEntity fetchBibliographicEntity = getBibEntityUsingBarcodeForIncompleteRecord(accessionRequest.getItemBarcode());
            if (fetchBibliographicEntity == null) {
                String dummyRecordResponse = createDummyRecord(accessionRequest, owningInstitution);
                responseString = response+", "+dummyRecordResponse;
                accessionResponse.setMessage(responseString);
                reportDataEntityList.addAll(createReportDataEntityList(accessionRequest, responseString));
            } else {
                responseString = RecapConstants.ITEM_BARCODE_ALREADY_ACCESSIONED_MSG;
                reportDataEntityList.addAll(createReportDataEntityList(accessionRequest, responseString));
            }
        }
    }

    private void saveReportEntity(String owningInstitution, List<ReportDataEntity> reportDataEntityList) {
        ReportEntity reportEntity;
        reportEntity = getReportEntity(owningInstitution!=null ? owningInstitution : RecapConstants.UNKNOWN_INSTITUTION);
        reportEntity.setReportDataEntities(reportDataEntityList);
        reportDetailRepository.save(reportEntity);
    }

    private void setAccessionResponse(List<AccessionResponse> accessionResponseList,AccessionRequest accessionRequest,AccessionResponse accessionResponse, String message){
        if (!accessionResponseList.contains(accessionResponse)) {
            accessionResponse.setItemBarcode(accessionRequest.getItemBarcode());
            accessionResponse.setMessage(message);
            accessionResponseList.add(accessionResponse);
        }
    }

    private ReportEntity getReportEntity(String owningInstitution){
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapConstants.ACCESSION_REPORT);
        reportEntity.setType(RecapConstants.ONGOING_ACCESSION_REPORT);
        reportEntity.setInstitutionName(owningInstitution);
        reportEntity.setCreatedDate(new Date());
        return reportEntity;
    }

    private List<ReportDataEntity> createReportDataEntityList(AccessionRequest accessionRequest,String response){
        List<ReportDataEntity> reportDataEntityList = new ArrayList<>();
        ReportDataEntity reportDataEntityCustomerCode = new ReportDataEntity();
        reportDataEntityCustomerCode.setHeaderName(RecapConstants.CUSTOMER_CODE);
        reportDataEntityCustomerCode.setHeaderValue(accessionRequest.getCustomerCode());
        reportDataEntityList.add(reportDataEntityCustomerCode);
        ReportDataEntity reportDataEntityItemBarcode = new ReportDataEntity();
        reportDataEntityItemBarcode.setHeaderName(RecapConstants.ITEM_BARCODE);
        reportDataEntityItemBarcode.setHeaderValue(accessionRequest.getItemBarcode());
        reportDataEntityList.add(reportDataEntityItemBarcode);
        ReportDataEntity reportDataEntityMessage = new ReportDataEntity();
        reportDataEntityMessage.setHeaderName(RecapConstants.MESSAGE);
        reportDataEntityMessage.setHeaderValue(response);
        reportDataEntityList.add(reportDataEntityMessage);
        return reportDataEntityList;
    }

    private String createDummyRecord(AccessionRequest accessionRequest, String owningInstitution) {
        String response;
        Integer owningInstitutionId = (Integer) getInstitutionEntityMap().get(owningInstitution);
        BibliographicEntity dummyBibliographicEntity = createDummyDataAsIncomplete(owningInstitutionId,accessionRequest.getItemBarcode(),accessionRequest.getCustomerCode());
        solrIndexService.indexByBibliographicId(dummyBibliographicEntity.getBibliographicId());
        response = RecapConstants.ACCESSION_DUMMY_RECORD;
        return response;
    }

    private String updateData(Object record, String owningInstitution, List<Map<String, String>> responseMapList, AccessionRequest accessionRequest){
        String response = null;
        XmlToBibEntityConverterInterface xmlToBibEntityConverterInterface = getConverter(owningInstitution);
        if (null != xmlToBibEntityConverterInterface) {
            Map responseMap = xmlToBibEntityConverterInterface.convert(record, owningInstitution,accessionRequest.getCustomerCode());
            responseMapList.add(responseMap);
            BibliographicEntity bibliographicEntity = (BibliographicEntity) responseMap.get(RecapConstants.BIBLIOGRAPHICENTITY);
            List<ReportEntity> reportEntityList = (List<ReportEntity>) responseMap.get(RecapConstants.REPORTENTITIES);
            if (CollectionUtils.isNotEmpty(reportEntityList)) {
                reportDetailRepository.save(reportEntityList);
            }
            if (bibliographicEntity != null) {
                BibliographicEntity savedBibliographicEntity = updateBibliographicEntity(bibliographicEntity);
                if (null != savedBibliographicEntity) {
                    response = indexBibliographicRecord(savedBibliographicEntity.getBibliographicId());
                }
            }
        }
        return response;
    }

    private String indexBibliographicRecord(Integer bibliographicId) {
        String response;
        getSolrIndexService().indexByBibliographicId(bibliographicId);
        response = RecapConstants.SUCCESS;
        return response;
    }

    private BibliographicEntity createDummyDataAsIncomplete(Integer owningInstitutionId,String itemBarcode,String customerCode) {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        Date currentDate = new Date();
        try {
            bibliographicEntity.setContent(getXmlContent(RecapConstants.DUMMY_BIB_CONTENT_XML).getBytes());
            bibliographicEntity.setCreatedDate(currentDate);
            bibliographicEntity.setCreatedBy(RecapConstants.ACCESSION);
            bibliographicEntity.setLastUpdatedBy(RecapConstants.ACCESSION);
            bibliographicEntity.setLastUpdatedDate(currentDate);
            bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
            bibliographicEntity.setOwningInstitutionId(owningInstitutionId);
            bibliographicEntity.setCatalogingStatus(RecapConstants.INCOMPLETE_STATUS);

            HoldingsEntity holdingsEntity = new HoldingsEntity();
            holdingsEntity.setContent(getXmlContent(RecapConstants.DUMMY_HOLDING_CONTENT_XML).getBytes());
            holdingsEntity.setCreatedDate(currentDate);
            holdingsEntity.setCreatedBy(RecapConstants.ACCESSION);
            holdingsEntity.setLastUpdatedDate(currentDate);
            holdingsEntity.setOwningInstitutionId(owningInstitutionId);
            holdingsEntity.setLastUpdatedBy(RecapConstants.ACCESSION);

            holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

            ItemEntity itemEntity = new ItemEntity();
            itemEntity.setCallNumberType(RecapConstants.DUMMY_CALL_NUMBER);
            itemEntity.setCallNumber(RecapConstants.DUMMYCALLNUMBER);
            itemEntity.setCreatedDate(currentDate);
            itemEntity.setCreatedBy(RecapConstants.ACCESSION);
            itemEntity.setLastUpdatedDate(currentDate);
            itemEntity.setLastUpdatedBy(RecapConstants.ACCESSION);
            itemEntity.setBarcode(itemBarcode);
            itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
            itemEntity.setOwningInstitutionId(owningInstitutionId);
            itemEntity.setCollectionGroupId((Integer) getCollectionGroupMap().get(RecapConstants.SHARED_CGD));
            itemEntity.setCustomerCode(customerCode);
            itemEntity.setItemAvailabilityStatusId((Integer) getItemStatusMap().get(RecapConstants.NOT_AVAILABLE));
            itemEntity.setDeleted(false);
            itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
            itemEntity.setCatalogingStatus(RecapConstants.INCOMPLETE_STATUS);
            List<ItemEntity> itemEntityList = new ArrayList<>();
            itemEntityList.add(itemEntity);
            holdingsEntity.setItemEntities(itemEntityList);

            bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
            bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        return savedBibliographicEntity;
    }

    private void generateAccessionSummaryReport(List<Map<String,String>> responseMapList,String owningInstitution){
        int successBibCount = 0;
        int successItemCount = 0;
        int failedBibCount = 0;
        int failedItemCount = 0;
        int exitsBibCount = 0;
        String reasonForFailureBib = "";
        String reasonForFailureItem = "";

        for(Map responseMap : responseMapList){
            successBibCount = successBibCount + (responseMap.get(RecapConstants.SUCCESS_BIB_COUNT)!=null ? (Integer) responseMap.get(RecapConstants.SUCCESS_BIB_COUNT) : 0);
            failedBibCount = failedBibCount + (responseMap.get(RecapConstants.FAILED_BIB_COUNT)!=null ? (Integer) responseMap.get(RecapConstants.FAILED_BIB_COUNT) : 0);
            if(failedBibCount == 0){
                if(StringUtils.isEmpty((String)responseMap.get(RecapConstants.REASON_FOR_ITEM_FAILURE))){
                    successItemCount = 1;
                }else{
                    failedItemCount = 1;
                }
            }
            exitsBibCount = exitsBibCount + (responseMap.get(RecapConstants.EXIST_BIB_COUNT)!=null ? (Integer) responseMap.get(RecapConstants.EXIST_BIB_COUNT) : 0);

            if(!StringUtils.isEmpty((String)responseMap.get(RecapConstants.REASON_FOR_BIB_FAILURE)) && !reasonForFailureBib.contains(responseMap.get(RecapConstants.REASON_FOR_BIB_FAILURE).toString())){
                    reasonForFailureBib =  responseMap.get(RecapConstants.REASON_FOR_BIB_FAILURE).toString()+ "," +reasonForFailureBib;
                }
            if((!StringUtils.isEmpty((String)responseMap.get(RecapConstants.REASON_FOR_ITEM_FAILURE))) && StringUtils.isEmpty(reasonForFailureBib) &&
                    !reasonForFailureItem.contains((String)responseMap.get(RecapConstants.REASON_FOR_ITEM_FAILURE))) {
                reasonForFailureItem = responseMap.get(RecapConstants.REASON_FOR_ITEM_FAILURE) + "," + reasonForFailureItem;
            }
        }

        List<ReportEntity> reportEntityList = new ArrayList<>();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapConstants.ACCESSION_REPORT);
        reportEntity.setType(RecapConstants.ACCESSION_SUMMARY_REPORT);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName(owningInstitution);

        ReportDataEntity successBibCountReportDataEntity = new ReportDataEntity();
        successBibCountReportDataEntity.setHeaderName(RecapConstants.BIB_SUCCESS_COUNT);
        successBibCountReportDataEntity.setHeaderValue(String.valueOf(successBibCount));
        reportDataEntities.add(successBibCountReportDataEntity);

        ReportDataEntity successItemCountReportDataEntity = new ReportDataEntity();
        successItemCountReportDataEntity.setHeaderName(RecapConstants.ITEM_SUCCESS_COUNT);
        successItemCountReportDataEntity.setHeaderValue(String.valueOf(successItemCount));
        reportDataEntities.add(successItemCountReportDataEntity);

        ReportDataEntity existsBibCountReportDataEntity = new ReportDataEntity();
        existsBibCountReportDataEntity.setHeaderName(RecapConstants.NUMBER_OF_BIB_MATCHES);
        existsBibCountReportDataEntity.setHeaderValue(String.valueOf(exitsBibCount));
        reportDataEntities.add(existsBibCountReportDataEntity);

        ReportDataEntity failedBibCountReportDataEntity = new ReportDataEntity();
        failedBibCountReportDataEntity.setHeaderName(RecapConstants.BIB_FAILURE_COUNT);
        failedBibCountReportDataEntity.setHeaderValue(String.valueOf(failedBibCount));
        reportDataEntities.add(failedBibCountReportDataEntity);

        ReportDataEntity failedItemCountReportDataEntity = new ReportDataEntity();
        failedItemCountReportDataEntity.setHeaderName(RecapConstants.ITEM_FAILURE_COUNT);
        failedItemCountReportDataEntity.setHeaderValue(String.valueOf(failedItemCount));
        reportDataEntities.add(failedItemCountReportDataEntity);

        ReportDataEntity reasonForBibFailureReportDataEntity = new ReportDataEntity();
        reasonForBibFailureReportDataEntity.setHeaderName(RecapConstants.FAILURE_BIB_REASON);
        if(reasonForFailureBib.startsWith("\n")){
            reasonForFailureBib = reasonForFailureBib.substring(1,reasonForFailureBib.length()-1);
        }
        reasonForFailureBib = reasonForFailureBib.replaceAll("\n",",");
        reasonForFailureBib = reasonForFailureBib.replaceAll(",$", "");
        reasonForBibFailureReportDataEntity.setHeaderValue(reasonForFailureBib);
        reportDataEntities.add(reasonForBibFailureReportDataEntity);

        ReportDataEntity reasonForItemFailureReportDataEntity = new ReportDataEntity();
        reasonForItemFailureReportDataEntity.setHeaderName(RecapConstants.FAILURE_ITEM_REASON);
        if(reasonForFailureItem.startsWith("\n")){
            reasonForFailureItem = reasonForFailureItem.substring(1,reasonForFailureItem.length()-1);
        }
        reasonForFailureItem = reasonForFailureItem.replaceAll("\n",",");
        reasonForFailureItem = reasonForFailureItem.replaceAll(",$", "");
        reasonForItemFailureReportDataEntity.setHeaderValue(reasonForFailureItem);
        reportDataEntities.add(reasonForItemFailureReportDataEntity);

        reportEntity.setReportDataEntities(reportDataEntities);
        reportEntityList.add(reportEntity);
        getReportDetailRepository().save(reportEntityList);
    }

    public BibliographicEntity updateBibliographicEntity(BibliographicEntity bibliographicEntity) {
        BibliographicEntity savedBibliographicEntity=null;
        BibliographicEntity fetchBibliographicEntity = getBibliographicDetailsRepository().findByOwningInstitutionIdAndOwningInstitutionBibId(bibliographicEntity.getOwningInstitutionId(),bibliographicEntity.getOwningInstitutionBibId());
        if(fetchBibliographicEntity ==null) { // New Bib Record

            savedBibliographicEntity = getBibliographicDetailsRepository().saveAndFlush(bibliographicEntity);
            getEntityManager().refresh(savedBibliographicEntity);
        }else{ // Existing bib Record
            // Bib
            fetchBibliographicEntity.setContent(bibliographicEntity.getContent());
            fetchBibliographicEntity.setCreatedBy(bibliographicEntity.getCreatedBy());
            fetchBibliographicEntity.setCreatedDate(bibliographicEntity.getCreatedDate());
            fetchBibliographicEntity.setDeleted(bibliographicEntity.isDeleted());
            fetchBibliographicEntity.setLastUpdatedBy(bibliographicEntity.getLastUpdatedBy());
            fetchBibliographicEntity.setLastUpdatedDate(bibliographicEntity.getLastUpdatedDate());

            // Holding
            List<HoldingsEntity> fetchHoldingsEntities =fetchBibliographicEntity.getHoldingsEntities();
            List<HoldingsEntity> holdingsEntities = bibliographicEntity.getHoldingsEntities();

            logger.info("fetchHoldingsEntities = {}",fetchHoldingsEntities.size());
            logger.info("holdingsEntities = {}",holdingsEntities.size());

            for (Iterator iholdings = holdingsEntities.iterator(); iholdings.hasNext();) {
                HoldingsEntity holdingsEntity =(HoldingsEntity) iholdings.next();
                for (int j=0;j<fetchHoldingsEntities.size();j++) {
                    HoldingsEntity fetchHolding=fetchHoldingsEntities.get(j);
                    if(fetchHolding.getOwningInstitutionHoldingsId().equalsIgnoreCase(holdingsEntity.getOwningInstitutionHoldingsId())  && fetchHolding.getOwningInstitutionId().intValue() == holdingsEntity.getOwningInstitutionId().intValue()) {
                        copyHoldingsEntity(fetchHolding,holdingsEntity);
                        iholdings.remove();
                    }else{
                        // Added for Boundwith scenarios
                        List<ItemEntity> fetchedItemEntityList = fetchHolding.getItemEntities();
                        List<ItemEntity> itemEntityList = holdingsEntity.getItemEntities();
                        for(ItemEntity fetchedItemEntity : fetchedItemEntityList){
                            for(ItemEntity itemEntity : itemEntityList){
                                if(fetchedItemEntity.getOwningInstitutionItemId().equals(itemEntity.getOwningInstitutionItemId())){
                                    copyHoldingsEntity(fetchHolding,holdingsEntity);
                                    iholdings.remove();
                                }
                            }
                        }

                    }
                }
            }
            fetchHoldingsEntities.addAll(holdingsEntities);
            logger.info("Holding Final Count = {}",fetchHoldingsEntities.size());

            // Item
            List<ItemEntity> fetchItemsEntities =fetchBibliographicEntity.getItemEntities();
            List<ItemEntity> itemsEntities = bibliographicEntity.getItemEntities();

            logger.info("fetchHoldingsEntities = {}",fetchItemsEntities.size());
            logger.info("holdingsEntities = {}",itemsEntities.size());

            for (Iterator iItems=itemsEntities.iterator();iItems.hasNext();) {
                ItemEntity itemEntity =(ItemEntity) iItems.next();
                for (Iterator ifetchItems=fetchItemsEntities.iterator();ifetchItems.hasNext();) {
                    ItemEntity fetchItem=(ItemEntity) ifetchItems.next();
                    if(fetchItem.getOwningInstitutionItemId().equalsIgnoreCase(itemEntity.getOwningInstitutionItemId())  && fetchItem.getOwningInstitutionId().intValue() == itemEntity.getOwningInstitutionId().intValue()) {
                        copyItemEntity(fetchItem,itemEntity);
                        iItems.remove();
                    }
                }
            }
            fetchItemsEntities.addAll(itemsEntities);
            logger.info("Item Final Count = {}",fetchItemsEntities.size());

            fetchBibliographicEntity.setHoldingsEntities(fetchHoldingsEntities);
            fetchBibliographicEntity.setItemEntities(fetchItemsEntities);

            try {
                savedBibliographicEntity = getBibliographicDetailsRepository().saveAndFlush(fetchBibliographicEntity);
                getEntityManager().refresh(savedBibliographicEntity);
            } catch (Exception e) {
                logger.info(RecapConstants.EXCEPTION,e);
            }
        }
        return savedBibliographicEntity;
    }

    private String reAccessionItem(List<ItemEntity> itemEntityList){
        try {
            for(ItemEntity itemEntity:itemEntityList){
                itemEntity.setDeleted(false);
                for (HoldingsEntity holdingsEntity:itemEntity.getHoldingsEntities()) {
                    holdingsEntity.setDeleted(false);
                }
                for(BibliographicEntity bibliographicEntity:itemEntity.getBibliographicEntities()){
                    bibliographicEntity.setDeleted(false);
                }
            }
            itemDetailsRepository.save(itemEntityList);
            itemDetailsRepository.flush();

        } catch (Exception e) {
            logger.error(RecapConstants.EXCEPTION,e);
            return RecapConstants.FAILURE;
        }
        return RecapConstants.SUCCESS;
    }

    private String indexReaccessionedItem(List<ItemEntity> itemEntityList){
        try {
            for(ItemEntity itemEntity:itemEntityList){
                itemEntity.getBibliographicEntities();
                for (BibliographicEntity bibliographicEntity:itemEntity.getBibliographicEntities()) {
                    List<SolrInputDocument> solrInputDocumentList = new ArrayList<>();
                    indexBibliographicRecord(bibliographicEntity.getBibliographicId());
                }
            }
        } catch (Exception e) {
            logger.error(RecapConstants.EXCEPTION,e);
            return RecapConstants.FAILURE;
        }
        return RecapConstants.SUCCESS;
    }

    private BibliographicEntity getBibEntityUsingBarcodeForIncompleteRecord(String itemBarcode){
        List<String> itemBarcodeList = new ArrayList<>();
        itemBarcodeList.add(itemBarcode);
        List<ItemEntity> itemEntityList = itemDetailsRepository.findByBarcodeIn(itemBarcodeList);
        BibliographicEntity fetchedBibliographicEntity = null;
        if(itemEntityList != null && !itemEntityList.isEmpty() && itemEntityList.get(0).getBibliographicEntities() != null){
            fetchedBibliographicEntity = itemEntityList.get(0).getBibliographicEntities().get(0);
        }
        return fetchedBibliographicEntity;
    }

    private HoldingsEntity copyHoldingsEntity(HoldingsEntity fetchHoldingsEntity, HoldingsEntity holdingsEntity){
        fetchHoldingsEntity.setContent(holdingsEntity.getContent());
        fetchHoldingsEntity.setCreatedBy(holdingsEntity.getCreatedBy());
        fetchHoldingsEntity.setCreatedDate(holdingsEntity.getCreatedDate());
        fetchHoldingsEntity.setDeleted(holdingsEntity.isDeleted());
        fetchHoldingsEntity.setLastUpdatedBy(holdingsEntity.getLastUpdatedBy());
        fetchHoldingsEntity.setLastUpdatedDate(holdingsEntity.getLastUpdatedDate());
        fetchHoldingsEntity.getItemEntities().addAll(holdingsEntity.getItemEntities());
        return fetchHoldingsEntity;
    }

    private ItemEntity copyItemEntity(ItemEntity fetchItemEntity, ItemEntity itemEntity){
        fetchItemEntity.setBarcode(itemEntity.getBarcode());
        fetchItemEntity.setCreatedBy(itemEntity.getCreatedBy());
        fetchItemEntity.setCreatedDate(itemEntity.getCreatedDate());
        fetchItemEntity.setDeleted(itemEntity.isDeleted());
        fetchItemEntity.setLastUpdatedBy(itemEntity.getLastUpdatedBy());
        fetchItemEntity.setLastUpdatedDate(itemEntity.getLastUpdatedDate());
        fetchItemEntity.setCallNumber(itemEntity.getCallNumber());
        fetchItemEntity.setCustomerCode(itemEntity.getCustomerCode());
        fetchItemEntity.setCallNumberType(itemEntity.getCallNumberType());
        fetchItemEntity.setItemAvailabilityStatusId(itemEntity.getItemAvailabilityStatusId());
        fetchItemEntity.setCopyNumber(itemEntity.getCopyNumber());
        fetchItemEntity.setCollectionGroupId(itemEntity.getCollectionGroupId());
        fetchItemEntity.setUseRestrictions(itemEntity.getUseRestrictions());
        fetchItemEntity.setVolumePartYear(itemEntity.getVolumePartYear());
        return fetchItemEntity;
    }

    private XmlToBibEntityConverterInterface getConverter(String institutionId){
        if(institutionId.equalsIgnoreCase(RecapConstants.PRINCETON) || institutionId.equalsIgnoreCase(RecapConstants.COLUMBIA)){
            return getMarcToBibEntityConverter();
        } else if(institutionId.equalsIgnoreCase(RecapConstants.NYPL)){
            return getScsbToBibEntityConverter();
        }
        return null;
    }

    private Map getInstitutionEntityMap() {
        if (null == institutionEntityMap) {
            institutionEntityMap = new HashMap();
            try {
                Iterable<InstitutionEntity> institutionEntities = getInstitutionDetailsRepository().findAll();
                for (Iterator iterator = institutionEntities.iterator(); iterator.hasNext(); ) {
                    InstitutionEntity institutionEntity = (InstitutionEntity) iterator.next();
                    institutionEntityMap.put( institutionEntity.getInstitutionCode(),institutionEntity.getInstitutionId());
                }
            } catch (Exception e) {
                logger.error(RecapConstants.EXCEPTION,e);
            }
        }
        return institutionEntityMap;
    }

    private Map getItemStatusMap() {
        if (null == itemStatusMap) {
            itemStatusMap = new HashMap();
            try {
                Iterable<ItemStatusEntity> itemStatusEntities = itemStatusDetailsRepository.findAll();
                for (Iterator iterator = itemStatusEntities.iterator(); iterator.hasNext(); ) {
                    ItemStatusEntity itemStatusEntity = (ItemStatusEntity) iterator.next();
                    itemStatusMap.put(itemStatusEntity.getStatusCode(), itemStatusEntity.getItemStatusId());
                }
            } catch (Exception e) {
                logger.error(RecapConstants.EXCEPTION,e);
            }
        }
        return itemStatusMap;
    }

    private Map getCollectionGroupMap() {
        if (null == collectionGroupMap) {
            collectionGroupMap = new HashMap();
            try {
                Iterable<CollectionGroupEntity> collectionGroupEntities = collectionGroupDetailsRepository.findAll();
                for (Iterator iterator = collectionGroupEntities.iterator(); iterator.hasNext(); ) {
                    CollectionGroupEntity collectionGroupEntity = (CollectionGroupEntity) iterator.next();
                    collectionGroupMap.put(collectionGroupEntity.getCollectionGroupCode(), collectionGroupEntity.getCollectionGroupId());
                }
            } catch (Exception e) {
                logger.error(RecapConstants.EXCEPTION,e);
            }
        }
        return collectionGroupMap;
    }

    private String getXmlContent(String filename) {
        InputStream inputStream = getClass().getResourceAsStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder out = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    out.append("\n");
                } else {
                    out.append(line);
                    out.append("\n");
                }
            }
        } catch (IOException e) {
            logger.error(RecapConstants.EXCEPTION,e);
        }
        return out.toString();
    }
}