package org.recap.util;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.jpa.*;
import org.recap.model.solr.Item;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by angelind on 16/6/16.
 */
public class ItemJSONUtil extends MarcUtil{

    private ProducerTemplate producerTemplate;

    /**
     * This method instantiates a new item json util.
     */
    public ItemJSONUtil() {
        //Do Nothing
    }

    /**
     * This method generates item document to index in solr.
     *
     * @param itemEntity the item entity
     * @return the item
     */
    public Item generateItemForIndex(ItemEntity itemEntity) {
        try {
            Item item = new Item();
            Integer itemId = itemEntity.getItemId();
            item.setId(String.valueOf(itemEntity.getOwningInstitutionId()+itemEntity.getOwningInstitutionItemId()));
            item.setItemId(itemId);
            item.setOwningInstitutionItemId(itemEntity.getOwningInstitutionItemId());
            item.setBarcode(itemEntity.getBarcode());
            item.setDocType(RecapConstants.ITEM);
            item.setCustomerCode(itemEntity.getCustomerCode());
            String useRestriction = StringUtils.isNotBlank(itemEntity.getUseRestrictions()) ? itemEntity.getUseRestrictions() : RecapConstants.NO_RESTRICTIONS;
            item.setUseRestriction(useRestriction.replaceAll(" ", ""));
            item.setUseRestrictionDisplay(useRestriction);
            item.setVolumePartYear(itemEntity.getVolumePartYear());
            item.setCallNumberSearch(itemEntity.getCallNumber().replaceAll(" ", ""));
            item.setCallNumberDisplay(itemEntity.getCallNumber());
            item.setItemCreatedBy(itemEntity.getCreatedBy());
            item.setItemCreatedDate(itemEntity.getCreatedDate());
            item.setItemLastUpdatedBy(itemEntity.getLastUpdatedBy());
            item.setItemLastUpdatedDate(itemEntity.getLastUpdatedDate());
            item.setDeletedItem(itemEntity.isDeleted());
            item.setItemCatalogingStatus(itemEntity.getCatalogingStatus() != null ? itemEntity.getCatalogingStatus():"");
            item.setCgdChangeLog(itemEntity.getCgdChangeLog());

            List<Integer> bibIdList = new ArrayList<>();
            List<BibliographicEntity> bibliographicEntities = itemEntity.getBibliographicEntities();
            for (BibliographicEntity bibliographicEntity : bibliographicEntities){
                bibIdList.add(bibliographicEntity.getBibliographicId());
            }
            item.setItemBibIdList(bibIdList);

            InstitutionEntity institutionEntity = itemEntity.getInstitutionEntity();
            String institutionCode = null != institutionEntity ? institutionEntity.getInstitutionCode() : "";
            item.setOwningInstitution(institutionCode);

            ItemStatusEntity itemStatusEntity = itemEntity.getItemStatusEntity();
            if (itemStatusEntity != null) {
                String statusCode = itemStatusEntity.getStatusCode();
                item.setAvailability(statusCode.replaceAll(" ", ""));
                item.setAvailabilityDisplay(statusCode);
            }
            CollectionGroupEntity collectionGroupEntity = itemEntity.getCollectionGroupEntity();
            if (collectionGroupEntity != null) {
                item.setCollectionGroupDesignation(collectionGroupEntity.getCollectionGroupCode());
            }

            List<Integer> holdingsIds = new ArrayList<>();
            List<HoldingsEntity> holdingsEntities = itemEntity.getHoldingsEntities();
            if (!CollectionUtils.isEmpty(holdingsEntities)) {
                for (HoldingsEntity holdingsEntity : holdingsEntities) {
                    holdingsIds.add(holdingsEntity.getHoldingsId());
                    item.setHoldingsIdList(holdingsIds);
                }
            }
            return item;
        } catch (Exception e) {
            saveExceptionReportForItem(itemEntity, e);
        }
        return null;
    }

    private void saveExceptionReportForItem(ItemEntity itemEntity, Exception e) {
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType(RecapConstants.SOLR_INDEX_EXCEPTION);
        reportEntity.setFileName(RecapConstants.SOLR_INDEX_FAILURE_REPORT);
        InstitutionEntity institutionEntity = null != itemEntity ? itemEntity.getInstitutionEntity() : null;
        String institutionCode = null != institutionEntity ? institutionEntity.getInstitutionCode() : RecapConstants.NA;
        reportEntity.setInstitutionName(institutionCode);

        ReportDataEntity docTypeDataEntity = new ReportDataEntity();
        docTypeDataEntity.setHeaderName(RecapConstants.DOCTYPE);
        docTypeDataEntity.setHeaderValue(RecapConstants.ITEM);
        reportDataEntities.add(docTypeDataEntity);

        ReportDataEntity owningInstDataEntity = new ReportDataEntity();
        owningInstDataEntity.setHeaderName(RecapConstants.OWNING_INSTITUTION);
        owningInstDataEntity.setHeaderValue(institutionCode);
        reportDataEntities.add(owningInstDataEntity);

        ReportDataEntity exceptionMsgDataEntity = new ReportDataEntity();
        exceptionMsgDataEntity.setHeaderName(RecapConstants.EXCEPTION_MSG);
        exceptionMsgDataEntity.setHeaderValue(StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : e.toString());
        reportDataEntities.add(exceptionMsgDataEntity);

        if(itemEntity != null && itemEntity.getItemId() != null) {
            ReportDataEntity itemIdDataEntity = new ReportDataEntity();
            itemIdDataEntity.setHeaderName(RecapConstants.ITEM_ID);
            itemIdDataEntity.setHeaderValue(String.valueOf(itemEntity.getItemId()));
            reportDataEntities.add(itemIdDataEntity);
        }

        reportEntity.addAll(reportDataEntities);
        getProducerTemplate().sendBody(RecapConstants.REPORT_Q, reportEntity);
    }

    /**
     * This method gets producer template.
     *
     * @return the producer template
     */
    public ProducerTemplate getProducerTemplate() {
        return producerTemplate;
    }

    /**
     * This method sets producer template.
     *
     * @param producerTemplate the producer template
     */
    public void setProducerTemplate(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }
}
