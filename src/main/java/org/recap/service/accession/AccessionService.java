package org.recap.service.accession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.Record;
import org.recap.RecapConstants;
import org.recap.converter.MarcToBibEntityConverter;
import org.recap.converter.SCSBToBibEntityConverter;
import org.recap.converter.XmlToBibEntityConverterInterface;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.accession.AccessionSummary;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jaxb.marc.BibRecords;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.*;
import org.recap.service.accession.resolver.BibDataResolver;
import org.recap.service.accession.resolver.CULBibDataResolver;
import org.recap.service.accession.resolver.NYPLBibDataResolver;
import org.recap.service.accession.resolver.PULBibDataResolver;
import org.recap.service.partnerservice.ColumbiaService;
import org.recap.service.partnerservice.NYPLService;
import org.recap.service.partnerservice.PrincetonService;
import org.recap.util.AccessionHelperUtil;
import org.recap.util.MarcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * Created by chenchulakshmig on 20/10/16.
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AccessionService {

    private static final Logger logger = LoggerFactory.getLogger(AccessionService.class);

    @Autowired
    protected AccessionHelperUtil accessionHelperUtil;

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
    private PrincetonService princetonService;

    @Autowired
    private ColumbiaService columbiaService;

    @Autowired
    private NYPLService nyplService;

    @Autowired
    private MarcUtil marcUtil;

    @Autowired
    private SolrIndexService solrIndexService;

    @Autowired
    private DummyDataService dummyDataService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Autowired
    private AccessionDetailsRepository accessionDetailsRepository;

    @Autowired
    private ItemBarcodeHistoryDetailsRepository itemBarcodeHistoryDetailsRepository;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private AccessionValidationService accessionValidationService;

    @Autowired
    AccessionDAO accessionDAO;

    @Autowired
    private PULBibDataResolver pulBibDataResolver;

    @Autowired
    private CULBibDataResolver culBibDataResolver;

    @Autowired
    private NYPLBibDataResolver nyplBibDataResolver;

    private List<BibDataResolver> bibDataResolvers;


    private Map<String,Integer> institutionEntityMap;

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

    public ColumbiaService getColumbiaService() {
        return columbiaService;
    }

    public NYPLService getNyplService() {
        return nyplService;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public AccessionDetailsRepository getAccessionDetailsRepository() {
        return accessionDetailsRepository;
    }

    /**
     * This method is used to find the owning institution code based on the customer code parameter value.
     *
     * @param customerCode
     * @return
     */
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

    /**
     * This method saves the accession request in database and returns the status message.
     *
     * @param accessionRequestList
     * @return
     */
    @Transactional
    public String saveRequest(List<AccessionRequest> accessionRequestList) {
        List<AccessionRequest> trimmedAccessionRequests = getTrimmedAccessionRequests(accessionRequestList);
        String status = null;
        try {
            AccessionEntity accessionEntity = new AccessionEntity();
            accessionEntity.setAccessionRequest(convertJsonToString(trimmedAccessionRequests));
            accessionEntity.setCreatedDate(new Date());
            accessionEntity.setAccessionStatus(RecapConstants.PENDING);
            accessionDetailsRepository.save(accessionEntity);
            status = RecapConstants.ACCESSION_SAVE_SUCCESS_STATUS;
        } catch (Exception ex) {
            logger.error(RecapConstants.LOG_ERROR, ex);
            status = RecapConstants.ACCESSION_SAVE_FAILURE_STATUS + RecapConstants.EXCEPTION_MSG + " : " + ex.getMessage();
        }
        return status;
    }

    /**
     * This method is used to find the list of accession entity based on the accession status.
     *
     * @param accessionStatus
     * @return
     */
    public List<AccessionEntity> getAccessionEntities(String accessionStatus) {
        return accessionDetailsRepository.findByAccessionStatus(accessionStatus);
    }

    /**
     * This method is used to get the accession request for the given accession list.
     *
     * @param accessionEntityList
     * @return
     */
    public List<AccessionRequest> getAccessionRequest(List<AccessionEntity> accessionEntityList) {
        List<AccessionRequest> accessionRequestList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(accessionEntityList)) {
            try {
                for(AccessionEntity accessionEntity : accessionEntityList) {
                    TypeReference<List<AccessionRequest>> typeReference = new TypeReference<List<AccessionRequest>>() {};
                    accessionRequestList.addAll(new ObjectMapper().readValue(accessionEntity.getAccessionRequest(), typeReference));
                }
            } catch(Exception e) {
                logger.error(RecapConstants.LOG_ERROR, e);
            }
        }
        return accessionRequestList;
    }

    public void updateStatusForAccessionEntities(List<AccessionEntity> accessionEntities, String status) {
        for(AccessionEntity accessionEntity : accessionEntities) {
            accessionEntity.setAccessionStatus(status);
        }
        getAccessionDetailsRepository().save(accessionEntities);
    }


    public List<AccessionResponse> doAccession(List<AccessionRequest> accessionRequestList, AccessionSummary accessionSummary) {

        // Trim accession request
        List<AccessionRequest> trimmedAccessionRequests = getTrimmedAccessionRequests(accessionRequestList);

        // Remove duplicate barcodes
        trimmedAccessionRequests = getAccessionHelperUtil().removeDuplicateRecord(trimmedAccessionRequests);
        int requestedCount = accessionRequestList.size();
        int duplicateCount = requestedCount - trimmedAccessionRequests.size();

        accessionSummary.setRequestedRecords(requestedCount);
        accessionSummary.setDuplicateRecords(duplicateCount);

        Set<AccessionResponse> accessionResponses = new HashSet<>();
        List<Map<String, String>> responseMaps = new ArrayList<>();

        // iterate Request
        for (Iterator<AccessionRequest> iterator = trimmedAccessionRequests.iterator(); iterator.hasNext(); ) {
            AccessionRequest accessionRequest = iterator.next();
            List<ReportDataEntity> reportDataEntitys = new ArrayList<>();

            // validate empty barcode ,customer code and owning institution
            String itemBarcode = accessionRequest.getItemBarcode();
            String customerCode = accessionRequest.getCustomerCode();
            AccessionValidationResponse accessionValidationResponse = validateBarcodeOrCustomerCode(itemBarcode, customerCode);

            String owningInstitution = accessionValidationResponse.getOwningInstitution();

            if(!accessionValidationResponse.isValid()) {
                String message = accessionValidationResponse.getMessage();
                accessionHelperUtil.setAccessionResponse(accessionResponses, itemBarcode, message);
                reportDataEntitys.addAll(accessionHelperUtil.createReportDataEntityList(accessionRequest, message));
                continue;
            }
            accessionHelperUtil.processRecords(accessionResponses, responseMaps, accessionRequest, reportDataEntitys, owningInstitution, true);

        }

        prepareSummary(accessionSummary, accessionResponses);

        return new ArrayList<>(accessionResponses);
    }

    public AccessionValidationResponse validateBarcodeOrCustomerCode(String itemBarcode, String customerCode) {
        AccessionValidationResponse accessionValidationResponse = validateItemBarcode( itemBarcode);
        if(null == accessionValidationResponse) {
            accessionValidationResponse = validateCustomerCode(customerCode);
        }
        return accessionValidationResponse;
    }

    private AccessionValidationResponse validateItemBarcode(String itemBarcode) {
        if(StringUtils.isBlank(itemBarcode)) {
            return getAccessionValidationResponse(false, RecapConstants.ITEM_BARCODE_EMPTY);
        } else {
            // todo : Validate barcode length
            if(itemBarcode.length() > 45) {
                return getAccessionValidationResponse(false ,RecapConstants.INVALID_BARCODE_LENGTH);
            }
        }

        return null;
    }


    private AccessionValidationResponse validateCustomerCode(String customerCode) {
        if(StringUtils.isBlank(customerCode)) {
            return getAccessionValidationResponse(false, RecapConstants.CUSTOMER_CODE_EMPTY);
        } else {
            String owningInstitution = getOwningInstitution(customerCode);
            if(StringUtils.isBlank(owningInstitution)) {
                return getAccessionValidationResponse(false, RecapConstants.CUSTOMER_CODE_DOESNOT_EXIST);
            }
            AccessionValidationResponse accessionValidationResponse = getAccessionValidationResponse(true, "");
            accessionValidationResponse.setOwningInstitution(owningInstitution);
            return accessionValidationResponse;
        }
    }

    private AccessionValidationResponse getAccessionValidationResponse(boolean valid, String message) {
        AccessionValidationResponse accessionValidationResponse = new AccessionValidationResponse();
        accessionValidationResponse.setValid(valid);
        accessionValidationResponse.setMessage(message);
        return accessionValidationResponse;
    }

    /**
     * This method is used to process the accession request, where it calls the appropriate partners ils service
     * and get the xml response which is used to insert record into scsb.
     *
     * @param accessionRequestList
     * @return
     */
    @Transactional
    public List<AccessionResponse> processRequest(List<AccessionRequest> accessionRequestList) {
        String response = null;
        List<AccessionRequest> trimmedAccessionRequests = getTrimmedAccessionRequests(accessionRequestList);
        Set<AccessionResponse> accessionResponsesList = new HashSet<>();
        String bibDataResponse;
        List<Map<String, String>> responseMapList = new ArrayList<>();
        for (AccessionRequest accessionRequest : trimmedAccessionRequests) {
            List<ReportDataEntity> reportDataEntityList = new ArrayList<>();
            String owningInstitution = getOwningInstitution(accessionRequest.getCustomerCode());
            List<ItemEntity> itemEntityList = getItemEntityList(accessionRequest);
            boolean isItemBarcodeEmpty = isItemBarcodeEmpty(accessionRequest);
            boolean itemExists = checkItemBarcodeAlreadyExist(itemEntityList);
            boolean isDeaccessionedItem = isItemDeaccessioned(itemEntityList);
            if (isItemBarcodeEmpty) {
                accessionHelperUtil.setAccessionResponse(accessionResponsesList, accessionRequest.getItemBarcode(), RecapConstants.ITEM_BARCODE_EMPTY);
                reportDataEntityList.addAll(accessionHelperUtil.createReportDataEntityList(accessionRequest, RecapConstants.ITEM_BARCODE_EMPTY));
            } else if (!itemExists) {
                if (owningInstitution == null) {
                    accessionHelperUtil.setAccessionResponse(accessionResponsesList, accessionRequest.getItemBarcode(), accessionRequest.getCustomerCode() + " " + RecapConstants.CUSTOMER_CODE_DOESNOT_EXIST);
                    reportDataEntityList.addAll(accessionHelperUtil.createReportDataEntityList(accessionRequest, RecapConstants.CUSTOMER_CODE_DOESNOT_EXIST));
                } else if (accessionRequest.getItemBarcode().length() > 45) {
                    accessionHelperUtil.setAccessionResponse(accessionResponsesList, accessionRequest.getItemBarcode(), RecapConstants.INVALID_BARCODE_LENGTH);
                    reportDataEntityList.addAll(accessionHelperUtil.createReportDataEntityList(accessionRequest, RecapConstants.INVALID_BARCODE_LENGTH));
                } else {
                    try {
                        if (owningInstitution != null && owningInstitution.equalsIgnoreCase(RecapConstants.PRINCETON)) {
                            StopWatch stopWatch = new StopWatch();
                            stopWatch.start();
                            bibDataResponse = getPrincetonService().getBibData(accessionRequest.getItemBarcode());
                            stopWatch.stop();
                            logger.info("Time taken to get bib data from ils : {}" ,stopWatch.getTotalTimeSeconds());
                            response = processAccessionForMarcXml(accessionResponsesList, bibDataResponse, responseMapList, owningInstitution, reportDataEntityList, accessionRequest);
                        } else if (owningInstitution != null && owningInstitution.equalsIgnoreCase(RecapConstants.COLUMBIA)) {
                            StopWatch stopWatch = new StopWatch();
                            stopWatch.start();
                            bibDataResponse = getColumbiaService().getBibData(accessionRequest.getItemBarcode());
                            stopWatch.stop();
                            logger.info("Time taken to get bib data from ils : {}", stopWatch.getTotalTimeSeconds());
                            response = processAccessionForMarcXml(accessionResponsesList, bibDataResponse, responseMapList, owningInstitution, reportDataEntityList, accessionRequest);
                        } else if (owningInstitution != null && owningInstitution.equalsIgnoreCase(RecapConstants.NYPL)) {
                            StopWatch stopWatch1 = new StopWatch();
                            stopWatch1.start();
                            bibDataResponse = getNyplService().getBibData(accessionRequest.getItemBarcode(), accessionRequest.getCustomerCode());
                            stopWatch1.stop();
                            logger.info("Total Time taken to get bib data from ils : {}", stopWatch1.getTotalTimeSeconds());
                            response = processAccessionForSCSBXml(accessionResponsesList, bibDataResponse, responseMapList, owningInstitution, reportDataEntityList, accessionRequest);
                        }
                    } catch (Exception ex) {
                        accessionHelperUtil.processException(accessionResponsesList, accessionRequest, reportDataEntityList, owningInstitution, ex);
                    }
                    accessionHelperUtil.generateAccessionSummaryReport(responseMapList, owningInstitution);
                }
            } else if (isDeaccessionedItem) {
                response = reAccessionItem(itemEntityList);
                if (response.equals(RecapConstants.SUCCESS)) {
                    response = indexReaccessionedItem(itemEntityList);
                    saveItemChangeLogEntity(RecapConstants.REACCESSION,RecapConstants.ITEM_ISDELETED_TRUE_TO_FALSE,itemEntityList);
                }
                accessionHelperUtil.setAccessionResponse(accessionResponsesList, accessionRequest.getItemBarcode(), response);
                reportDataEntityList.addAll(accessionHelperUtil.createReportDataEntityList(accessionRequest, response));
            } else {
                String itemAreadyAccessionedOwnInstBibId = itemEntityList.get(0).getBibliographicEntities() != null ? itemEntityList.get(0).getBibliographicEntities().get(0).getOwningInstitutionBibId() : " ";
                String itemAreadyAccessionedOwnInstHoldingId = itemEntityList.get(0).getHoldingsEntities() != null ? itemEntityList.get(0).getHoldingsEntities().get(0).getOwningInstitutionHoldingsId() : " ";
                String itemAreadyAccessionedMessage = RecapConstants.ITEM_ALREADY_ACCESSIONED + RecapConstants.OWN_INST_BIB_ID + itemAreadyAccessionedOwnInstBibId + RecapConstants.OWN_INST_HOLDING_ID + itemAreadyAccessionedOwnInstHoldingId + RecapConstants.OWN_INST_ITEM_ID + itemEntityList.get(0).getOwningInstitutionItemId();
                accessionHelperUtil.setAccessionResponse(accessionResponsesList, accessionRequest.getItemBarcode(), itemAreadyAccessionedMessage);
                reportDataEntityList.addAll(accessionHelperUtil.createReportDataEntityList(accessionRequest, itemAreadyAccessionedMessage));
            }
            saveReportEntity(owningInstitution, reportDataEntityList);
        }
        return new ArrayList<>(accessionResponsesList);
    }

    /**
     * This method is used to check whether the AccessionRequest's itemBarcode is blank or not.
     * @param accessionRequest
     * @return
     */
    private boolean isItemBarcodeEmpty(AccessionRequest accessionRequest) {
        if(StringUtils.isBlank(accessionRequest.getItemBarcode())) {
            return true;
        }
        return false;
    }

    /**
     *This method is used to process and save accession for MarcXML input
     *
     * @param accessionResponsesList
     * @param object
     * @param responseMapList
     * @param owningInstitution
     * @param reportDataEntityList
     * @param accessionRequest
     * @return
     */
    public String processAccessionForMarcXml(Set<AccessionResponse> accessionResponsesList, Object object,
                                             List<Map<String, String>> responseMapList, String owningInstitution,
                                             List<ReportDataEntity> reportDataEntityList, AccessionRequest accessionRequest) {
        StopWatch stopWatch;
        String response = null;
        stopWatch = new StopWatch();
        stopWatch.start();
        List<Record> records = (List<Record>) object;
        boolean isBoundWithItem = isBoundWithItemForMarcRecord(records);
        boolean isValidBoundWithRecord = true;
        if(isBoundWithItem) {
            isValidBoundWithRecord = accessionValidationService.validateBoundWithMarcRecordFromIls(records);
        }
        if ((!isBoundWithItem) || (isBoundWithItem && isValidBoundWithRecord)) {
            if (CollectionUtils.isNotEmpty(records)) {
                int count=1;
                for (Record record : records) {
                    boolean isFirstRecord = false;
                    if(count==1){
                        isFirstRecord=true;
                    }
                    response = updateData(record, owningInstitution, responseMapList, accessionRequest,isValidBoundWithRecord,isFirstRecord);
                    accessionHelperUtil.setAccessionResponse(accessionResponsesList,accessionRequest.getItemBarcode(),response);
                    reportDataEntityList.addAll(accessionHelperUtil.createReportDataEntityList(accessionRequest, response));
                    count++;
                }
            }
        } else {
            response = RecapConstants.INVALID_BOUNDWITH_RECORD;
            accessionHelperUtil.setAccessionResponse(accessionResponsesList,accessionRequest.getItemBarcode(),response);
            reportDataEntityList.addAll(accessionHelperUtil.createReportDataEntityList(accessionRequest, response));
        }
        stopWatch.stop();
        logger.info("Total time taken to save records for accession : {}", stopWatch.getTotalTimeSeconds());
        return response;
    }

    private boolean isBoundWithItemForMarcRecord(List<Record> recordList){
        if(recordList.size() > 1){
            return true;
        }
        return false;
    }

    private boolean isBoundWithItemForScsbRecord(List<BibRecord> bibRecordList){
        if(bibRecordList.size() > 1){
            return true;
        }
        return false;
    }

    /**
     * This method is used to process and save accession for scsb xml format.
     *
     * @param accessionResponsesList
     * @param object
     * @param responseMapList
     * @param owningInstitution
     * @param reportDataEntityList
     * @param accessionRequest
     * @return
     * @throws Exception
     */
    public String processAccessionForSCSBXml(Set<AccessionResponse> accessionResponsesList, Object object,
                                              List<Map<String, String>> responseMapList, String owningInstitution,
                                              List<ReportDataEntity> reportDataEntityList, AccessionRequest accessionRequest) throws Exception {
        String response = null;
        BibRecords bibRecords = (BibRecords) object;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        boolean isBoundWithItem = isBoundWithItemForScsbRecord(bibRecords.getBibRecordList());
        boolean isValidBoundWithRecord = true;
        if(isBoundWithItem) {
            isValidBoundWithRecord = accessionValidationService.validateBoundWithScsbRecordFromIls(bibRecords.getBibRecordList());
        }
        if ((!isBoundWithItem) || (isBoundWithItem && isValidBoundWithRecord)) {
            int count = 1;
            for (BibRecord bibRecord : bibRecords.getBibRecordList()) {
                boolean isFirstRecord = false;
                if(count==1){
                    isFirstRecord=true;
                }
                response = updateData(bibRecord, owningInstitution, responseMapList, accessionRequest,isValidBoundWithRecord,isFirstRecord);
                accessionHelperUtil.setAccessionResponse(accessionResponsesList, accessionRequest.getItemBarcode(), response);
                reportDataEntityList.addAll(accessionHelperUtil.createReportDataEntityList(accessionRequest, response));
            }
        } else {
            response = RecapConstants.INVALID_BOUNDWITH_RECORD;
            accessionHelperUtil.setAccessionResponse(accessionResponsesList,accessionRequest.getItemBarcode(),response);
            reportDataEntityList.addAll(accessionHelperUtil.createReportDataEntityList(accessionRequest, response));
        }
        stopWatch.stop();
        logger.info("Total time taken to save records for accession : {}", stopWatch.getTotalTimeSeconds());
        return response;
    }

    /**
     * This method is used to get the ItemEntity based on accessionRequest's barcode and customerCode.
     * @param accessionRequest
     * @return
     */
    private List<ItemEntity> getItemEntityList(AccessionRequest accessionRequest){
        return itemDetailsRepository.findByBarcodeAndCustomerCode(accessionRequest.getItemBarcode(),accessionRequest.getCustomerCode());
    }

    /**
     * This method is used to check whether the items already exists or not.
     * @param itemEntityList
     * @return
     */
    private boolean checkItemBarcodeAlreadyExist(List<ItemEntity> itemEntityList){
        boolean itemExists = false;
        if (itemEntityList != null && !itemEntityList.isEmpty()) {
            itemExists = true;
        }
        return itemExists;
    }

    /**
     * This method is used to return itemEntity's isDeleted status.
     * @param itemEntityList
     * @return
     */
    private boolean isItemDeaccessioned(List<ItemEntity> itemEntityList){
        boolean itemDeleted = false;
        if (itemEntityList != null && !itemEntityList.isEmpty()) {
            for(ItemEntity itemEntity : itemEntityList){
                return itemEntity.isDeleted();
            }
        }
        return itemDeleted;
    }

    /**
     * This method is used to create dummy record if the item barcode is not found.
     * @param response
     * @param owningInstitution
     * @param reportDataEntityList
     * @param accessionRequest
     */
    public String createDummyRecordIfAny(String response, String owningInstitution, List<ReportDataEntity> reportDataEntityList, AccessionRequest accessionRequest) {
        String message = response;
        if (response != null && response.equals(RecapConstants.ITEM_BARCODE_NOT_FOUND_MSG)) {
            BibliographicEntity fetchBibliographicEntity = getBibEntityUsingBarcodeForIncompleteRecord(accessionRequest.getItemBarcode());
            if (fetchBibliographicEntity == null) {
                String dummyRecordResponse = createDummyRecord(accessionRequest, owningInstitution);
                message = response+", "+dummyRecordResponse;
                reportDataEntityList.addAll(accessionHelperUtil.createReportDataEntityList(accessionRequest, message));
            } else {
                message = RecapConstants.ITEM_BARCODE_ALREADY_ACCESSIONED_MSG;
                reportDataEntityList.addAll(accessionHelperUtil.createReportDataEntityList(accessionRequest, message));
            }
        }
        return message;
    }

    /**
     * This method is used to save the ReportEntity in the database.
     * @param owningInstitution
     * @param reportDataEntityList
     */
    @Transactional
    public void saveReportEntity(String owningInstitution, List<ReportDataEntity> reportDataEntityList) {
        ReportEntity reportEntity;
        reportEntity = getReportEntity(owningInstitution!=null ? owningInstitution : RecapConstants.UNKNOWN_INSTITUTION);
        reportEntity.setReportDataEntities(reportDataEntityList);
        producerTemplate.sendBody(RecapConstants.REPORT_Q, reportEntity);
    }

    private ReportEntity getReportEntity(String owningInstitution){
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapConstants.ACCESSION_REPORT);
        reportEntity.setType(RecapConstants.ONGOING_ACCESSION_REPORT);
        reportEntity.setInstitutionName(owningInstitution);
        reportEntity.setCreatedDate(new Date());
        return reportEntity;
    }

    public void saveItemChangeLogEntity(String operationType, String message, List<ItemEntity> itemEntityList) {
        List<ItemChangeLogEntity> itemChangeLogEntityList = new ArrayList<>();
        for (ItemEntity itemEntity:itemEntityList) {
            ItemChangeLogEntity itemChangeLogEntity = new ItemChangeLogEntity();
            itemChangeLogEntity.setOperationType(RecapConstants.ACCESSION);
            itemChangeLogEntity.setUpdatedBy(operationType);
            itemChangeLogEntity.setUpdatedDate(new Date());
            itemChangeLogEntity.setRecordId(itemEntity.getItemId());
            itemChangeLogEntity.setNotes(message);
            itemChangeLogEntityList.add(itemChangeLogEntity);
        }
        itemChangeLogDetailsRepository.save(itemChangeLogEntityList);
    }

    /**
     * This method is used to create dummy record for bib in the database and index them in Solr.
     * @param accessionRequest
     * @param owningInstitution
     * @return
     */
    public String createDummyRecord(AccessionRequest accessionRequest, String owningInstitution) {
        String response;
        Integer owningInstitutionId = (Integer) getInstitutionEntityMap().get(owningInstitution);
        BibliographicEntity dummyBibliographicEntity = dummyDataService.createDummyDataAsIncomplete(owningInstitutionId,accessionRequest.getItemBarcode(),accessionRequest.getCustomerCode());
        solrIndexService.indexByBibliographicId(dummyBibliographicEntity.getBibliographicId());
        response = RecapConstants.ACCESSION_DUMMY_RECORD;
        return response;
    }

    /**
     * This method is used to update the incoming data to the existing bib or create a new bib and save them in database,
     * Once saved in database they are indexed in Solr.
     * @param record
     * @param owningInstitution
     * @param responseMapList
     * @param accessionRequest
     * @return
     */
    @Transactional
    private String updateData(Object record, String owningInstitution, List<Map<String, String>> responseMapList, AccessionRequest accessionRequest, boolean isValidBoundWithRecord,boolean isFirstRecord){
        String response = null;
        String incompleteResponse = new String();
        XmlToBibEntityConverterInterface xmlToBibEntityConverterInterface = getConverter(owningInstitution);
        if (null != xmlToBibEntityConverterInterface) {
            Map responseMap = xmlToBibEntityConverterInterface.convert(record, owningInstitution,accessionRequest);
            responseMapList.add(responseMap);
            BibliographicEntity bibliographicEntity = (BibliographicEntity) responseMap.get(RecapConstants.BIBLIOGRAPHICENTITY);
            List<ReportEntity> reportEntityList = (List<ReportEntity>) responseMap.get(RecapConstants.REPORTENTITIES);
            incompleteResponse = (String) responseMap.get(RecapConstants.INCOMPLETE_RESPONSE);
            if (CollectionUtils.isNotEmpty(reportEntityList)) {
                reportDetailRepository.save(reportEntityList);
            }
            if (bibliographicEntity != null) {
                StringBuilder errorMessage = new StringBuilder();
                boolean isValidItemAndHolding = accessionValidationService.validateItemAndHolding(bibliographicEntity,isValidBoundWithRecord,isFirstRecord,errorMessage);
                if (isValidItemAndHolding) {
                    BibliographicEntity savedBibliographicEntity = updateBibliographicEntity(bibliographicEntity);
                    if (null != savedBibliographicEntity) {
                        response = indexBibliographicRecord(savedBibliographicEntity.getBibliographicId());
                    }
                } else {
                    response = errorMessage.toString();
                }
            }
        }
        if (StringUtils.isNotEmpty(response) && StringUtils.isNotEmpty(incompleteResponse) && RecapConstants.SUCCESS.equalsIgnoreCase(response)){
            return RecapConstants.SUCCESS_INCOMPLETE_RECORD;
        }
        return response;
    }

    /**
     * This method is used to index Bibliographic Record in solr and return a response.
     * @param bibliographicId
     * @return
     */
    private String indexBibliographicRecord(Integer bibliographicId) {
        String response;
        getSolrIndexService().indexByBibliographicId(bibliographicId);
        response = RecapConstants.SUCCESS;
        return response;
    }

    /**
     *This method is used to update bibs if exists or create and save the bibs.
     * @param bibliographicEntity
     * @return
     */
    @Transactional
    public BibliographicEntity updateBibliographicEntity(BibliographicEntity bibliographicEntity) {
        BibliographicEntity savedBibliographicEntity=null;
        BibliographicEntity fetchBibliographicEntity = getBibliographicDetailsRepository().findByOwningInstitutionIdAndOwningInstitutionBibId(bibliographicEntity.getOwningInstitutionId(),bibliographicEntity.getOwningInstitutionBibId());
        if(fetchBibliographicEntity ==null) { // New Bib Record
            savedBibliographicEntity = accessionDAO.saveBibRecord(bibliographicEntity);
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

            logger.info("Owning Inst Bib Id :  = {}",bibliographicEntity.getOwningInstitutionBibId());
            logger.info("Fetched Item Entities = {}",fetchHoldingsEntities.size());
            logger.info("Incoming Item Entities = {}",holdingsEntities.size());

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
                        if(CollectionUtils.isNotEmpty(itemEntityList)) {
                            List<ItemEntity> itemsToProcess = new ArrayList<>(itemEntityList);
                            for(ItemEntity fetchedItemEntity : fetchedItemEntityList){
                                for(ItemEntity itemEntity : itemsToProcess){
                                    if(fetchedItemEntity.getOwningInstitutionItemId().equals(itemEntity.getOwningInstitutionItemId())){
                                        copyHoldingsEntity(fetchHolding,holdingsEntity);
                                        iholdings.remove();
                                    }
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
            List<ItemEntity> incomingItemsEntities = bibliographicEntity.getItemEntities();

            logger.info("Fetched Item Entities = {}",CollectionUtils.isNotEmpty(fetchItemsEntities) ? fetchItemsEntities.size() : 0);
            logger.info("Incoming Item Entities = {}",CollectionUtils.isNotEmpty(incomingItemsEntities) ? incomingItemsEntities.size() : 0);

            List<ItemEntity> finalItemEntities = new ArrayList<>();

            for (Iterator<HoldingsEntity> iterator = fetchHoldingsEntities.iterator(); iterator.hasNext(); ) {
                HoldingsEntity holdingsEntity =  iterator.next();
                finalItemEntities.addAll(holdingsEntity.getItemEntities());
            }

            logger.info("Item Final Count = {}",finalItemEntities.size());

            fetchBibliographicEntity.setHoldingsEntities(fetchHoldingsEntities);
            fetchBibliographicEntity.setItemEntities(finalItemEntities);

            savedBibliographicEntity = saveBibRecord(fetchBibliographicEntity);
        }
        return savedBibliographicEntity;
    }

    private void processItems(List<ItemEntity> fetchItemsEntities, List<ItemEntity> itemsEntities) {
        for (Iterator iItems = itemsEntities.iterator(); iItems.hasNext();) {
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
    }

    public BibliographicEntity saveBibRecord(BibliographicEntity fetchBibliographicEntity) {
        try {
            return accessionDAO.saveBibRecord(fetchBibliographicEntity);
        } catch (Exception e) {
            logger.info(RecapConstants.EXCEPTION,e);
        }
        return null;
    }

    /**
     * This method is used to re-accession the item for the item which is de-accessioned.
     * @param itemEntityList
     * @return
     */
    public String reAccessionItem(List<ItemEntity> itemEntityList){
        try {
            for(ItemEntity itemEntity:itemEntityList){
                itemEntity.setDeleted(false);
                Date currentDateTime = new Date();
                itemEntity.setCreatedDate(currentDateTime);
                itemEntity.setCreatedBy(RecapConstants.REACCESSION);
                itemEntity.setLastUpdatedDate(currentDateTime);
                itemEntity.setLastUpdatedBy(RecapConstants.REACCESSION);

                for (HoldingsEntity holdingsEntity:itemEntity.getHoldingsEntities()) {
                    holdingsEntity.setDeleted(false);
                    itemEntity.setCreatedDate(currentDateTime);
                    itemEntity.setCreatedBy(RecapConstants.REACCESSION);
                    holdingsEntity.setLastUpdatedDate(currentDateTime);
                    holdingsEntity.setLastUpdatedBy(RecapConstants.REACCESSION);
                }
                for(BibliographicEntity bibliographicEntity:itemEntity.getBibliographicEntities()) {
                    bibliographicEntity.setDeleted(false);
                    itemEntity.setCreatedDate(currentDateTime);
                    itemEntity.setCreatedBy(RecapConstants.REACCESSION);
                    bibliographicEntity.setLastUpdatedDate(currentDateTime);
                    bibliographicEntity.setLastUpdatedBy(RecapConstants.REACCESSION);
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

    /**
     * This method is used to index the re-accessioned item in solr.
     * @param itemEntityList
     * @return
     */
    public String indexReaccessionedItem(List<ItemEntity> itemEntityList){
        try {
            for(ItemEntity itemEntity:itemEntityList){
                itemEntity.getBibliographicEntities();
                for (BibliographicEntity bibliographicEntity:itemEntity.getBibliographicEntities()) {
                    indexBibliographicRecord(bibliographicEntity.getBibliographicId());
                }
            }
        } catch (Exception e) {
            logger.error(RecapConstants.EXCEPTION,e);
            return RecapConstants.FAILURE;
        }
        return RecapConstants.SUCCESS;
    }

    /**
     * This method is used to get the BibliographicEntity from ItemEntity list using item barcode.
     * @param itemBarcode
     * @return
     */
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
        List<ItemEntity> fetchedItemEntities = fetchHoldingsEntity.getItemEntities();

        processItems(fetchedItemEntities, holdingsEntity.getItemEntities());
        fetchHoldingsEntity.setItemEntities(fetchedItemEntities);

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

    /**
     * This method converts the json object to string.
     * @param objJson
     * @return
     */
    private String convertJsonToString(Object objJson) {
        String strJson = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            strJson = objectMapper.writeValueAsString(objJson);
        } catch (JsonProcessingException ex) {
            logger.error(RecapConstants.LOG_ERROR, ex);
        }
        return strJson;
    }

    public List<AccessionRequest> getTrimmedAccessionRequests(List<AccessionRequest> accessionRequestList) {
        List<AccessionRequest> trimmedAccessionRequests = new ArrayList<>();
        for (AccessionRequest accessionRequest : accessionRequestList) {
            AccessionRequest request = new AccessionRequest();
            request.setItemBarcode(accessionRequest.getItemBarcode().trim());
            request.setCustomerCode(accessionRequest.getCustomerCode().trim());
            trimmedAccessionRequests.add(request);
        }
        return trimmedAccessionRequests;
    }

    public AccessionHelperUtil getAccessionHelperUtil() {
        return accessionHelperUtil;
    }


    public List<BibDataResolver> getBibDataResolvers() {
        if(CollectionUtils.isEmpty(bibDataResolvers)) {
            bibDataResolvers = new ArrayList<>();
            bibDataResolvers.add(pulBibDataResolver);
            bibDataResolvers.add(culBibDataResolver);
            bibDataResolvers.add(nyplBibDataResolver);
        }
        return bibDataResolvers;
    }

    public void prepareSummary(AccessionSummary accessionSummary, Object object) {
        Set<AccessionResponse> accessionResponses = (Set<AccessionResponse>) object;
        if(CollectionUtils.isNotEmpty(accessionResponses)) {
            for (Iterator<AccessionResponse> iterator = accessionResponses.iterator(); iterator.hasNext(); ) {
                AccessionResponse accessionResponse = iterator.next();
                String message = accessionResponse.getMessage();
                addCountToSummary(accessionSummary, message);
            }
        }
    }

    protected void addCountToSummary(AccessionSummary accessionSummary, String message) {
        if(message.contains(RecapConstants.SUCCESS)) {
            accessionSummary.addSuccessRecord(1);
        }else if(message.contains(RecapConstants.ITEM_ALREADY_ACCESSIONED)) {
            accessionSummary.addAlreadyAccessioned(1);
        } else if(message.contains(RecapConstants.ACCESSION_DUMMY_RECORD)) {
            accessionSummary.addDummyRecords(1);
        } else if(message.contains(RecapConstants.EXCEPTION)) {
            accessionSummary.addException(1);
        } else if(StringUtils.equalsIgnoreCase(RecapConstants.INVALID_BARCODE_LENGTH, message)) {
            accessionSummary.addInvalidLenghBarcode(1);
        } else if(StringUtils.equalsIgnoreCase(RecapConstants.OWNING_INST_EMPTY, message)) {
            accessionSummary.addEmptyOwningInst(1);
        } else if(StringUtils.equalsIgnoreCase(RecapConstants.ITEM_BARCODE_EMPTY, message)) {
            accessionSummary.addEmptyBarcodes(1);
        } else if(StringUtils.equalsIgnoreCase(RecapConstants.CUSTOMER_CODE_EMPTY, message)) {
            accessionSummary.addEmptyCustomerCode(1);
        }  else if(StringUtils.equalsIgnoreCase(RecapConstants.CUSTOMER_CODE_DOESNOT_EXIST, message)) {
            accessionSummary.addCustomerCodeDoesNotExist(1);
        } else {
            accessionSummary.addFailure(1);
        }
    }

    public void createSummaryReport(String summary, String type) {
        List<ReportDataEntity> reportDataEntityList = new ArrayList<>();
        ReportDataEntity reportDataEntityMessage = new ReportDataEntity();
        reportDataEntityMessage.setHeaderName(type);
        reportDataEntityMessage.setHeaderValue(summary);
        reportDataEntityList.add(reportDataEntityMessage);
        saveReportEntity(null, reportDataEntityList);
    }

    class AccessionValidationResponse {
        private boolean valid;
        private String owningInstitution;
        private String message;

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getOwningInstitution() {
            return owningInstitution;
        }

        public void setOwningInstitution(String owningInstitution) {
            this.owningInstitution = owningInstitution;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}