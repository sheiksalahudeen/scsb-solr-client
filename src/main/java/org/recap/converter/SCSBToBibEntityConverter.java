package org.recap.converter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jaxb.Holding;
import org.recap.model.jaxb.Holdings;
import org.recap.model.jaxb.Items;
import org.recap.model.jaxb.marc.CollectionType;
import org.recap.model.jaxb.marc.ContentType;
import org.recap.model.jaxb.marc.LeaderFieldType;
import org.recap.model.jaxb.marc.RecordType;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.CollectionGroupDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ItemStatusDetailsRepository;
import org.recap.util.DBReportUtil;
import org.recap.util.MarcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by premkb on 15/12/16.
 */
@Service
public class SCSBToBibEntityConverter implements XmlToBibEntityConverterInterface {

    private static final Logger logger = LoggerFactory.getLogger(SCSBToBibEntityConverter.class);

    @Autowired
    private DBReportUtil dbReportUtil;

    @Autowired
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Autowired
    private MarcUtil marcUtil;

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;
    private Map itemStatusMap;
    private Map collectionGroupMap;
    private Map institutionEntityMap;

    /**
     * This method is used to convert scsb record into bib entity
     * @param scsbRecord
     * @param institutionName the institution name
     * @param accessionRequest    the customer code
     * @return
     */
    @Override
    public Map convert(Object scsbRecord, String institutionName, AccessionRequest accessionRequest){
        int failedItemCount = 0;
        int successItemCount = 0;
        String reasonForFailureItem = "";
        Map<String, Object> map = new HashMap<>();
        boolean processBib = false;

        List<HoldingsEntity> holdingsEntities = new ArrayList<>();
        List<ItemEntity> itemEntities = new ArrayList<>();
        List<ReportEntity> reportEntities = new ArrayList<>();

        getDbReportUtil().setInstitutionEntitiesMap(getInstitutionEntityMap());
        getDbReportUtil().setCollectionGroupMap(getCollectionGroupMap());

        BibRecord bibRecord = (BibRecord) scsbRecord;
        Integer owningInstitutionId = (Integer) getInstitutionEntityMap().get(institutionName);
        Date currentDate = new Date();
        Map<String, Object> bibMap = processAndValidateBibliographicEntity(bibRecord, owningInstitutionId, institutionName,currentDate);
        BibliographicEntity bibliographicEntity = (BibliographicEntity) bibMap.get("bibliographicEntity");
        ReportEntity bibReportEntity = (ReportEntity) bibMap.get("bibReportEntity");
        if (bibReportEntity != null) {
            reportEntities.add(bibReportEntity);
        } else {
            processBib = true;
        }
        map.put(RecapConstants.FAILED_BIB_COUNT, bibMap.get(RecapConstants.FAILED_BIB_COUNT));
        map.put(RecapConstants.SUCCESS_BIB_COUNT , bibMap.get(RecapConstants.SUCCESS_BIB_COUNT));
        map.put(RecapConstants.REASON_FOR_BIB_FAILURE , bibMap.get(RecapConstants.REASON_FOR_BIB_FAILURE));
        map.put(RecapConstants.EXIST_BIB_COUNT , bibMap.get(RecapConstants.EXIST_BIB_COUNT));

        List<Holdings> holdingsList = bibRecord.getHoldings();
        for(Holdings holdings:holdingsList){
            List<Holding> holdingList=null;
            if (holdings.getHolding()!=null) {
                holdingList = holdings.getHolding();
            } else {
                logger.error("holding is empty---"+bibRecord.getBib().getOwningInstitutionBibId());
            }
            for(Holding holding:holdingList){
                boolean processHoldings = false;
                if (holding.getContent() != null) {
                    CollectionType holdingContentCollection = holding.getContent().getCollection();
                    List<RecordType> holdingRecordTypes = holdingContentCollection.getRecord();
                    RecordType holdingsRecordType = holdingRecordTypes.get(0);

                    Map<String, Object> holdingsMap = processAndValidateHoldingsEntity(bibliographicEntity, holding, holdingContentCollection,institutionName,currentDate);
                    HoldingsEntity holdingsEntity = (HoldingsEntity) holdingsMap.get("holdingsEntity");
                    ReportEntity holdingsReportEntity = (ReportEntity) holdingsMap.get("holdingsReportEntity");
                    if (holdingsReportEntity != null) {
                        reportEntities.add(holdingsReportEntity);
                    } else {
                        processHoldings = true;
                        holdingsEntities.add(holdingsEntity);
                    }
                    String holdingsCallNumber = getMarcUtil().getDataFieldValueForRecordType(holdingsRecordType, "852", null, null, "h");
                    String holdingsCallNumberType = getMarcUtil().getInd1ForRecordType(holdingsRecordType, "852", "h");

                    List<Items> items = holding.getItems();
                    for (Items item : items) {
                        ContentType itemContent = item.getContent();
                        CollectionType itemContentCollection = itemContent.getCollection();

                        List<RecordType> itemRecordTypes = itemContentCollection.getRecord();
                        for (RecordType itemRecordType : itemRecordTypes) {
                            Map<String, Object> itemMap = processAndValidateItemEntity(bibliographicEntity, holdingsEntity, owningInstitutionId, holdingsCallNumber, holdingsCallNumberType, itemRecordType,accessionRequest,institutionName,currentDate);
                            if (itemMap != null) {
                                if(itemMap.containsKey(RecapConstants.FAILED_ITEM_COUNT)){
                                    failedItemCount = failedItemCount + (int) itemMap.get(RecapConstants.FAILED_ITEM_COUNT);
                                }
                                if(itemMap.containsKey(RecapConstants.ITEMBARCODE)){
                                    map.put(RecapConstants.ITEMBARCODE,(String)itemMap.get(RecapConstants.ITEMBARCODE));
                                }
                                if(itemMap.containsKey(RecapConstants.REASON_FOR_ITEM_FAILURE)){
                                    String reason = (String)itemMap.get(RecapConstants.REASON_FOR_ITEM_FAILURE);
                                    if(!StringUtils.isEmpty(reason)){
                                        if(StringUtils.isEmpty(reasonForFailureItem)){
                                            reasonForFailureItem = (String) itemMap.get(RecapConstants.REASON_FOR_ITEM_FAILURE);
                                        }else{
                                            StringBuilder stringBuilder = new StringBuilder();
                                            stringBuilder.append(itemMap.get(RecapConstants.REASON_FOR_ITEM_FAILURE));
                                            stringBuilder.append(",");
                                            stringBuilder.append(reasonForFailureItem);
                                            reasonForFailureItem = stringBuilder.toString();
                                        }

                                    }
                                }
                                if(itemMap.containsKey(RecapConstants.SUCCESS_ITEM_COUNT)){
                                    successItemCount = successItemCount + (int) itemMap.get(RecapConstants.SUCCESS_ITEM_COUNT);
                                }
                                ItemEntity itemEntity = (ItemEntity) itemMap.get("itemEntity");
                                ReportEntity itemReportEntity = (ReportEntity) itemMap.get("itemReportEntity");
                                if (itemReportEntity != null) {
                                    reportEntities.add(itemReportEntity);
                                } else if (processHoldings) {
                                    if (holdingsEntity.getItemEntities() == null) {
                                        holdingsEntity.setItemEntities(new ArrayList<>());
                                    }
                                    holdingsEntity.getItemEntities().add(itemEntity);
                                    itemEntities.add(itemEntity);
                                }
                            }
                        }
                    }
                }
            }
            bibliographicEntity.setHoldingsEntities(holdingsEntities);
            bibliographicEntity.setItemEntities(itemEntities);
        }
        if (CollectionUtils.isNotEmpty(reportEntities)) {
            map.put("reportEntities", reportEntities);
        }
        if (processBib) {
            map.put(RecapConstants.BIBLIOGRAPHICENTITY, bibliographicEntity);
        }
        map.put(RecapConstants.FAILED_ITEM_COUNT,failedItemCount);
        map.put(RecapConstants.SUCCESS_ITEM_COUNT,successItemCount);
        map.put(RecapConstants.REASON_FOR_ITEM_FAILURE,reasonForFailureItem);

        return map;
    }

    /**
     * This method is used to validate all necessary bib record fields
     * @param bibRecord
     * @param owningInstitutionId
     * @param institutionName
     * @param currentDate
     * @return
     */
    private Map<String, Object> processAndValidateBibliographicEntity(BibRecord bibRecord,Integer owningInstitutionId,String institutionName, Date currentDate) {
        int failedBibCount = 0;
        int successBibCount = 0;
        int exitsBibCount = 0;
        String reasonForFailureBib = "";
        Map<String, Object> map = new HashMap<>();

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        StringBuilder errorMessage = new StringBuilder();
        String owningInstitutionBibId = bibRecord.getBib().getOwningInstitutionBibId();

        if (StringUtils.isNotBlank(owningInstitutionBibId)) {
            bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);
        } else {
            errorMessage.append("Owning Institution Bib Id cannot be null");
        }
        if (owningInstitutionId != null) {
            bibliographicEntity.setOwningInstitutionId(owningInstitutionId);
        } else {
            errorMessage.append("\n");
            errorMessage.append("Owning Institution Id cannot be null");
        }
        bibliographicEntity.setCreatedDate(currentDate);
        bibliographicEntity.setCreatedBy(RecapConstants.ACCESSION);
        bibliographicEntity.setLastUpdatedDate(currentDate);
        bibliographicEntity.setLastUpdatedBy(RecapConstants.ACCESSION);

        ContentType bibContent = bibRecord.getBib().getContent();
        CollectionType bibContentCollection = bibContent.getCollection();
        String bibXmlContent = bibContentCollection.serialize(bibContentCollection);
        if (!StringUtils.isEmpty(bibXmlContent)) {
            bibliographicEntity.setContent(bibXmlContent.getBytes());
        } else {
            errorMessage.append("\n");
            errorMessage.append("Bib Content cannot be empty");
        }

        boolean subFieldExistsFor245 = getMarcUtil().isSubFieldExists(bibContentCollection.getRecord().get(0), "245");

        if (!subFieldExistsFor245) {
            errorMessage.append("\n");
            errorMessage.append("Atleast one subfield should be there for 245 tag");
        }

        LeaderFieldType leader = bibContentCollection.getRecord().get(0).getLeader();
        if (!(leader != null && StringUtils.isNotBlank(leader.getValue()) && leader.getValue().length() == 24)) {
            errorMessage.append("\n");
            errorMessage.append("Leader Field value should be 24 characters");
        }

        if(owningInstitutionId != null && StringUtils.isNotBlank(owningInstitutionBibId)){
            BibliographicEntity existBibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibIdAndIsDeletedFalse(owningInstitutionId,owningInstitutionBibId);
            if(null != existBibliographicEntity){
                exitsBibCount = 1;
            }
        }
        List<ReportDataEntity> reportDataEntities = null;

        if (errorMessage.toString().length() > 1) {
            failedBibCount = failedBibCount+1;
            reasonForFailureBib = errorMessage.toString();
            reportDataEntities = getDbReportUtil().generateBibFailureReportEntity(bibliographicEntity);
            ReportDataEntity errorReportDataEntity = new ReportDataEntity();
            errorReportDataEntity.setHeaderName(RecapConstants.ERROR_DESCRIPTION);
            errorReportDataEntity.setHeaderValue(errorMessage.toString());
            reportDataEntities.add(errorReportDataEntity);
        }else{
            successBibCount = successBibCount+1;
        }
        if (!CollectionUtils.isEmpty(reportDataEntities)) {
            ReportEntity reportEntity = new ReportEntity();
            reportEntity.setFileName(RecapConstants.ACCESSION_FAILURE_REPORT);
            reportEntity.setInstitutionName(institutionName);
            reportEntity.setType(org.recap.RecapConstants.FAILURE);
            reportEntity.setCreatedDate(new Date());
            reportEntity.addAll(reportDataEntities);
            map.put("bibReportEntity", reportEntity);
        }
        map.put(RecapConstants.FAILED_BIB_COUNT , failedBibCount);
        map.put(RecapConstants.REASON_FOR_BIB_FAILURE , reasonForFailureBib);
        map.put(RecapConstants.BIBLIOGRAPHICENTITY, bibliographicEntity);
        map.put(RecapConstants.SUCCESS_BIB_COUNT,successBibCount);
        map.put(RecapConstants.EXIST_BIB_COUNT,exitsBibCount);
        return map;
    }

    /**
     * This method is used to validate all necessary holdings fields required in the bib record.
     * @param bibliographicEntity
     * @param institutionName
     * @param holding
     * @param holdingContentCollection
     * @param institutionName
     * @param currentDate
     * @return
     */
    private Map<String, Object> processAndValidateHoldingsEntity(BibliographicEntity bibliographicEntity, Holding holding, CollectionType holdingContentCollection,String institutionName,Date currentDate) {
        StringBuilder errorMessage = new StringBuilder();
        Map<String, Object> map = new HashMap<>();
        HoldingsEntity holdingsEntity = new HoldingsEntity();

        String holdingsContent = holdingContentCollection.serialize(holdingContentCollection);
        if (StringUtils.isNotBlank(holdingsContent)) {
            holdingsEntity.setContent(holdingsContent.getBytes());
        } else {
            errorMessage.append("Holdings Content cannot be empty");
        }

        holdingsEntity.setCreatedDate(currentDate);
        holdingsEntity.setCreatedBy(RecapConstants.ACCESSION);
        holdingsEntity.setLastUpdatedDate(currentDate);
        holdingsEntity.setLastUpdatedBy(RecapConstants.ACCESSION);
        Integer owningInstitutionId = bibliographicEntity.getOwningInstitutionId();
        holdingsEntity.setOwningInstitutionId(owningInstitutionId);
        String owningInstitutionHoldingsId = holding.getOwningInstitutionHoldingsId();
        if (StringUtils.isBlank(owningInstitutionHoldingsId)) {
            owningInstitutionHoldingsId = UUID.randomUUID().toString();
        } else if (owningInstitutionHoldingsId.length() > 100) {
            owningInstitutionHoldingsId = UUID.randomUUID().toString();
        }
        holdingsEntity.setOwningInstitutionHoldingsId(owningInstitutionHoldingsId);
        List<ReportDataEntity> reportDataEntities = null;
        if (errorMessage.toString().length() > 1) {
            reportDataEntities = getDbReportUtil().generateBibHoldingsFailureReportEntity(bibliographicEntity, holdingsEntity);
            ReportDataEntity errorReportDataEntity = new ReportDataEntity();
            errorReportDataEntity.setHeaderName(RecapConstants.ERROR_DESCRIPTION);
            errorReportDataEntity.setHeaderValue(errorMessage.toString());
            reportDataEntities.add(errorReportDataEntity);
        }

        if (!org.springframework.util.CollectionUtils.isEmpty(reportDataEntities)) {
            ReportEntity reportEntity = new ReportEntity();
            reportEntity.setFileName(RecapConstants.ACCESSION_FAILURE_REPORT);
            reportEntity.setInstitutionName(institutionName);
            reportEntity.setType(org.recap.RecapConstants.FAILURE);
            reportEntity.setCreatedDate(new Date());
            reportEntity.addAll(reportDataEntities);
            map.put("holdingsReportEntity", reportEntity);
        }
        map.put("holdingsEntity", holdingsEntity);
        return map;
    }

    /**
     * This method is used to validate all necessary fields required for item.
     *
     * @param bibliographicEntity
     * @param holdingsEntity
     * @param owningInstitutionId
     * @param holdingsCallNumber
     * @param holdingsCallNumberType
     * @param itemRecordType
     * @param institutionName
     * @param accessionRequest
     * @param institutionName
     * @param currentDate
     * @return
     */
    private Map<String, Object> processAndValidateItemEntity(BibliographicEntity bibliographicEntity, HoldingsEntity holdingsEntity, Integer owningInstitutionId, String holdingsCallNumber, String holdingsCallNumberType, RecordType itemRecordType,AccessionRequest accessionRequest,String institutionName, Date currentDate) {
        StringBuilder errorMessage = new StringBuilder();
        Map<String, Object> map = new HashMap<>();
        ItemEntity itemEntity = new ItemEntity();
        int failedItemCount = 0;
        int successItemCount = 0;
        boolean isComplete = true;
        String reasonForFailureItem = "";
        map.put(RecapConstants.FAILED_ITEM_COUNT,failedItemCount);
        map.put(RecapConstants.SUCCESS_ITEM_COUNT,successItemCount);
        map.put(RecapConstants.REASON_FOR_ITEM_FAILURE,reasonForFailureItem);
        String itemBarcode = getMarcUtil().getDataFieldValueForRecordType(itemRecordType, "876", null, null, "p");
        if (accessionRequest.getItemBarcode().equals(itemBarcode)) {//This is to avoid creation of multiple items when response from partner service is having 1Bib 1Hold n items, accession should be done for one item which comes in the request and should not be done for other items which is linked with the same bib
            if (StringUtils.isNotBlank(itemBarcode)) {
                itemEntity.setBarcode(itemBarcode);
                map.put("itemBarcode",itemBarcode);
            } else {
                errorMessage.append("Item Barcode cannot be null");
            }
            itemEntity.setCustomerCode(accessionRequest.getCustomerCode());
            itemEntity.setCallNumber(holdingsCallNumber);
            itemEntity.setCallNumberType(String.valueOf(holdingsCallNumberType));
            itemEntity.setItemAvailabilityStatusId((Integer) getItemStatusMap().get("Available"));
            String copyNumber = getMarcUtil().getDataFieldValueForRecordType(itemRecordType, "876", null, null, "t");
            if (StringUtils.isNotBlank(copyNumber) && org.apache.commons.lang3.math.NumberUtils.isNumber(copyNumber)) {
                itemEntity.setCopyNumber(Integer.valueOf(copyNumber));
            }
            if (owningInstitutionId != null) {
                itemEntity.setOwningInstitutionId(owningInstitutionId);
            } else {
                errorMessage.append("\n");
                errorMessage.append("Owning Institution Id cannot be null");
            }
            String collectionGroupCode = getMarcUtil().getDataFieldValueForRecordType(itemRecordType, "900", null, null, "a");
            if (StringUtils.isNotBlank(collectionGroupCode) && collectionGroupMap.containsKey(collectionGroupCode)) {
                itemEntity.setCollectionGroupId((Integer) collectionGroupMap.get(collectionGroupCode));
            } else {
                itemEntity.setCollectionGroupId((Integer) collectionGroupMap.get("Open"));
            }
            itemEntity.setCreatedDate(currentDate);
            itemEntity.setCreatedBy(RecapConstants.ACCESSION);
            itemEntity.setLastUpdatedDate(currentDate);
            itemEntity.setLastUpdatedBy(RecapConstants.ACCESSION);

            String useRestrictions = getMarcUtil().getDataFieldValueForRecordType(itemRecordType, "876", null, null, "h");
            if (StringUtils.isNotBlank(useRestrictions) && ("In Library Use".equalsIgnoreCase(useRestrictions) || "Supervised Use".equalsIgnoreCase(useRestrictions))) {
                itemEntity.setUseRestrictions(useRestrictions);
            } else if(null == useRestrictions){
                isComplete = false;
            }

            itemEntity.setVolumePartYear(getMarcUtil().getDataFieldValueForRecordType(itemRecordType, "876", null, null, "3"));
            String owningInstitutionItemId = getMarcUtil().getDataFieldValueForRecordType(itemRecordType, "876", null, null, "a");
            if (StringUtils.isNotBlank(owningInstitutionItemId)) {
                itemEntity.setOwningInstitutionItemId(owningInstitutionItemId);
            } else {
                errorMessage.append("\n");
                errorMessage.append("Item Owning Institution Id cannot be null");
            }

            if(isComplete){
                bibliographicEntity.setCatalogingStatus(RecapConstants.COMPLETE_STATUS);
                itemEntity.setCatalogingStatus(RecapConstants.COMPLETE_STATUS);
            } else {
                bibliographicEntity.setCatalogingStatus(RecapConstants.INCOMPLETE_STATUS);
                itemEntity.setCatalogingStatus(RecapConstants.INCOMPLETE_STATUS);
            }
            List<ReportDataEntity> reportDataEntities = null;
            if (errorMessage.toString().length() > 1) {
                if(map.containsKey(RecapConstants.FAILED_ITEM_COUNT)){
                    failedItemCount = ((int) map.get(RecapConstants.FAILED_ITEM_COUNT)) + 1;
                    map.put(RecapConstants.FAILED_ITEM_COUNT,failedItemCount);
                }
                if(map.containsKey(RecapConstants.REASON_FOR_ITEM_FAILURE)){
                    reasonForFailureItem = errorMessage.toString();
                    map.put(RecapConstants.REASON_FOR_ITEM_FAILURE,reasonForFailureItem);
                }

                reportDataEntities = getDbReportUtil().generateBibHoldingsAndItemsFailureReportEntities(bibliographicEntity, holdingsEntity, itemEntity);
                ReportDataEntity errorReportDataEntity = new ReportDataEntity();
                errorReportDataEntity.setHeaderName(RecapConstants.ERROR_DESCRIPTION);
                errorReportDataEntity.setHeaderValue(errorMessage.toString());
                reportDataEntities.add(errorReportDataEntity);
            }else{
                if(map.containsKey(RecapConstants.SUCCESS_ITEM_COUNT)){
                    successItemCount = (int) map.get(RecapConstants.SUCCESS_ITEM_COUNT) + 1;
                    map.put(RecapConstants.SUCCESS_ITEM_COUNT,successItemCount);
                }
            }
            if (!org.springframework.util.CollectionUtils.isEmpty(reportDataEntities)) {
                ReportEntity reportEntity = new ReportEntity();
                reportEntity.setFileName(RecapConstants.ACCESSION_FAILURE_REPORT);
                reportEntity.setInstitutionName(institutionName);
                reportEntity.setType(RecapConstants.FAILURE);
                reportEntity.setCreatedDate(new Date());
                reportEntity.addAll(reportDataEntities);
                map.put("itemReportEntity", reportEntity);
            }
            map.put("itemEntity", itemEntity);
            return map;
        }
        return null;
    }

    /**
     * This method gets all item status and puts it in a map.
     *
     * @return the item status map
     */
    public Map getItemStatusMap() {
        if (null == itemStatusMap) {
            itemStatusMap = new HashMap();
            try {
                Iterable<ItemStatusEntity> itemStatusEntities = itemStatusDetailsRepository.findAll();
                for (Iterator iterator = itemStatusEntities.iterator(); iterator.hasNext(); ) {
                    ItemStatusEntity itemStatusEntity = (ItemStatusEntity) iterator.next();
                    itemStatusMap.put(itemStatusEntity.getStatusCode(), itemStatusEntity.getItemStatusId());
                }
            } catch (Exception e) {
                logger.error(RecapConstants.LOG_ERROR,e);
            }
        }
        return itemStatusMap;
    }

    /**
     * This method gets all collection group and puts it in a map.
     *
     * @return the collection group map
     */
    public Map getCollectionGroupMap() {
        if (null == collectionGroupMap) {
            collectionGroupMap = new HashMap();
            try {
                Iterable<CollectionGroupEntity> collectionGroupEntities = collectionGroupDetailsRepository.findAll();
                for (Iterator iterator = collectionGroupEntities.iterator(); iterator.hasNext(); ) {
                    CollectionGroupEntity collectionGroupEntity = (CollectionGroupEntity) iterator.next();
                    collectionGroupMap.put(collectionGroupEntity.getCollectionGroupCode(), collectionGroupEntity.getCollectionGroupId());
                }
            } catch (Exception e) {
                logger.error(RecapConstants.LOG_ERROR,e);
            }
        }
        return collectionGroupMap;
    }

    /**
     * This method gets all institution entity and puts it in a map.
     *
     * @return the institution entity map
     */
    public Map getInstitutionEntityMap() {
        if (null == institutionEntityMap) {
            institutionEntityMap = new HashMap();
            try {
                Iterable<InstitutionEntity> institutionEntities = institutionDetailsRepository.findAll();
                for (Iterator iterator = institutionEntities.iterator(); iterator.hasNext(); ) {
                    InstitutionEntity institutionEntity = (InstitutionEntity) iterator.next();
                    institutionEntityMap.put(institutionEntity.getInstitutionCode(), institutionEntity.getInstitutionId());
                }
            } catch (Exception e) {
                logger.error(RecapConstants.LOG_ERROR,e);
            }
        }
        return institutionEntityMap;
    }

    /**
     * This method gets db report util.
     *
     * @return the db report util
     */
    public DBReportUtil getDbReportUtil() {
        return dbReportUtil;
    }

    /**
     * This method sets db report util.
     *
     * @param dbReportUtil the db report util
     */
    public void setDbReportUtil(DBReportUtil dbReportUtil) {
        this.dbReportUtil = dbReportUtil;
    }

    /**
     * Gets marc util.
     *
     * @return the marc util
     */
    public MarcUtil getMarcUtil() {
        if (null == marcUtil) {
            marcUtil = new MarcUtil();
        }
        return marcUtil;
    }
}
