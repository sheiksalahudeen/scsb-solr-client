package org.recap.util;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.recap.RecapConstants;
import org.recap.model.jpa.ItemChangeLogEntity;
import org.recap.model.search.DeaccessionItemResultsRow;
import org.recap.model.search.ReportsForm;
import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.search.resolver.impl.item.*;
import org.recap.model.solr.Bib;
import org.recap.model.solr.Item;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by akulak on 21/12/16.
 */
@Component
public class ReportsUtil {

    private Logger logger = LoggerFactory.getLogger(ReportsUtil.class);

    @Autowired
    SolrTemplate solrTemplate;

    @Autowired
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Autowired
    SolrQueryBuilder solrQueryBuilder;

    @Autowired
    BibSolrCrudRepository bibSolrCrudRepository;

    @Autowired
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    List<ItemValueResolver> itemValueResolvers;

    public void populateILBDCountsForRequest(ReportsForm reportsForm, Date requestFromDate, Date requestToDate) {
        reportsForm.setIlRequestPulCount(requestItemDetailsRepository.getIlRequestCounts(requestFromDate, requestToDate, RecapConstants.PUL_INST_ID, Arrays.asList(RecapConstants.CUL_INST_ID, RecapConstants.NYPL_INST_ID)));
        reportsForm.setIlRequestCulCount(requestItemDetailsRepository.getIlRequestCounts(requestFromDate, requestToDate, RecapConstants.CUL_INST_ID, Arrays.asList(RecapConstants.PUL_INST_ID, RecapConstants.NYPL_INST_ID)));
        reportsForm.setIlRequestNyplCount(requestItemDetailsRepository.getIlRequestCounts(requestFromDate, requestToDate, RecapConstants.NYPL_INST_ID, Arrays.asList(RecapConstants.PUL_INST_ID, RecapConstants.CUL_INST_ID)));
        reportsForm.setBdRequestPulCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate,RecapConstants.PUL_INST_ID, RecapConstants.BORROW_DIRECT));
        reportsForm.setBdRequestCulCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, RecapConstants.CUL_INST_ID, RecapConstants.BORROW_DIRECT));
        reportsForm.setBdRequestNyplCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, RecapConstants.NYPL_INST_ID, RecapConstants.BORROW_DIRECT));
        reportsForm.setShowILBDResults(true);
        reportsForm.setShowReportResultsText(true);
        reportsForm.setShowNoteILBD(true);
    }

    public void populatePartnersCountForRequest(ReportsForm reportsForm, Date requestFromDate, Date requestToDate) {
        reportsForm.setPhysicalPrivatePulCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, RecapConstants.PUL_INST_ID, Arrays.asList(RecapConstants.CGD_PRIVATE), Arrays.asList(RecapConstants.HOLD, RecapConstants.RETRIEVAL, RecapConstants.RECALL, RecapConstants.BORROW_DIRECT)));
        reportsForm.setPhysicalPrivateCulCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, RecapConstants.CUL_INST_ID, Arrays.asList(RecapConstants.CGD_PRIVATE), Arrays.asList(RecapConstants.HOLD, RecapConstants.RETRIEVAL, RecapConstants.RECALL, RecapConstants.BORROW_DIRECT)));
        reportsForm.setPhysicalPrivateNyplCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, RecapConstants.NYPL_INST_ID, Arrays.asList(RecapConstants.CGD_PRIVATE), Arrays.asList(RecapConstants.HOLD, RecapConstants.RETRIEVAL, RecapConstants.RECALL, RecapConstants.BORROW_DIRECT)));
        reportsForm.setPhysicalSharedPulCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, RecapConstants.PUL_INST_ID, Arrays.asList(RecapConstants.CGD_SHARED, RecapConstants.CGD_OPEN), Arrays.asList(RecapConstants.HOLD, RecapConstants.RETRIEVAL, RecapConstants.RECALL, RecapConstants.BORROW_DIRECT)));
        reportsForm.setPhysicalSharedCulCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, RecapConstants.CUL_INST_ID, Arrays.asList(RecapConstants.CGD_SHARED, RecapConstants.CGD_OPEN), Arrays.asList(RecapConstants.HOLD, RecapConstants.RETRIEVAL, RecapConstants.RECALL, RecapConstants.BORROW_DIRECT)));
        reportsForm.setPhysicalSharedNyplCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, RecapConstants.NYPL_INST_ID, Arrays.asList(RecapConstants.CGD_SHARED, RecapConstants.CGD_OPEN), Arrays.asList(RecapConstants.HOLD, RecapConstants.RETRIEVAL, RecapConstants.RECALL, RecapConstants.BORROW_DIRECT)));
        reportsForm.setEddPrivatePulCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, RecapConstants.PUL_INST_ID, Arrays.asList(RecapConstants.CGD_PRIVATE), Arrays.asList(RecapConstants.EDD)));
        reportsForm.setEddPrivateCulCount( requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, RecapConstants.CUL_INST_ID, Arrays.asList(RecapConstants.CGD_PRIVATE), Arrays.asList(RecapConstants.EDD)));
        reportsForm.setEddPrivateNyplCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, RecapConstants.NYPL_INST_ID, Arrays.asList(RecapConstants.CGD_PRIVATE), Arrays.asList(RecapConstants.EDD)));
        reportsForm.setEddSharedOpenPulCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, RecapConstants.PUL_INST_ID, Arrays.asList(RecapConstants.CGD_SHARED, RecapConstants.CGD_OPEN), Arrays.asList(RecapConstants.EDD)));
        reportsForm.setEddSharedOpenCulCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, RecapConstants.CUL_INST_ID, Arrays.asList(RecapConstants.CGD_SHARED, RecapConstants.CGD_OPEN), Arrays.asList(RecapConstants.EDD)));
        reportsForm.setEddSharedOpenNyplCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, RecapConstants.NYPL_INST_ID, Arrays.asList(RecapConstants.CGD_SHARED, RecapConstants.CGD_OPEN), Arrays.asList(RecapConstants.EDD)));
        reportsForm.setShowPartners(true);
        reportsForm.setShowReportResultsText(true);
        reportsForm.setShowNotePartners(true);
    }


    public void populateRequestTypeInformation(ReportsForm reportsForm, Date requestFromDate, Date requestToDate) {
        for (String requestType : reportsForm.getReportRequestType()) {
            if (requestType.equalsIgnoreCase(RecapConstants.REPORTS_RETRIEVAL)) {
                reportsForm.setRetrievalRequestPulCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, RecapConstants.PUL_INST_ID, RecapConstants.RETRIEVAL));
                reportsForm.setRetrievalRequestCulCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, RecapConstants.CUL_INST_ID, RecapConstants.RETRIEVAL));
                reportsForm.setRetrievalRequestNyplCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, RecapConstants.NYPL_INST_ID, RecapConstants.RETRIEVAL));
                reportsForm.setShowRetrievalTable(true);
            }

            if (requestType.equalsIgnoreCase(RecapConstants.REPORTS_RECALL)) {
                reportsForm.setRecallRequestPulCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, RecapConstants.PUL_INST_ID, RecapConstants.RECALL));
                reportsForm.setRecallRequestCulCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, RecapConstants.CUL_INST_ID, RecapConstants.RECALL));
                reportsForm.setRecallRequestNyplCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, RecapConstants.NYPL_INST_ID, RecapConstants.RECALL));
                reportsForm.setShowRecallTable(true);
            }
        }
        reportsForm.setShowReportResultsText(true);
        reportsForm.setShowRequestTypeTable(true);
        reportsForm.setShowNoteRequestType(true);
    }
    public void populateAccessionDeaccessionItemCounts(ReportsForm reportsForm, String requestedFromDate, String requestedToDate) throws Exception {
        String date = getSolrFormattedDates(requestedFromDate, requestedToDate);
        for (String owningInstitution : reportsForm.getOwningInstitutions()) {
            for (String collectionGroupDesignation : reportsForm.getCollectionGroupDesignations()) {
                SolrQuery query = solrQueryBuilder.buildSolrQueryForAccessionReports(date, owningInstitution, false, collectionGroupDesignation);
                query.setRows(0);
                QueryResponse queryResponse = solrTemplate.getSolrClient().query(query);
                SolrDocumentList results = queryResponse.getResults();
                long numFound = results.getNumFound();
                if (owningInstitution.equalsIgnoreCase(RecapConstants.PRINCETON)) {
                    if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_OPEN)) {
                        reportsForm.setAccessionOpenPulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_SHARED)) {
                        reportsForm.setAccessionSharedPulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_PRIVATE)) {
                        reportsForm.setAccessionPrivatePulCount(numFound);
                    }
                } else if (owningInstitution.equalsIgnoreCase(RecapConstants.COLUMBIA)) {
                    if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_OPEN)) {
                        reportsForm.setAccessionOpenCulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_SHARED)) {
                        reportsForm.setAccessionSharedCulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_PRIVATE)) {
                        reportsForm.setAccessionPrivateCulCount(numFound);
                    }
                } else if (owningInstitution.equalsIgnoreCase(RecapConstants.NYPL)) {
                    if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_OPEN)) {
                        reportsForm.setAccessionOpenNyplCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_SHARED)) {
                        reportsForm.setAccessionSharedNyplCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_PRIVATE)) {
                        reportsForm.setAccessionPrivateNyplCount(numFound);
                    }
                }
            }
        }
        for (String ownInstitution : reportsForm.getOwningInstitutions()) {
            for (String collectionGroupDesignation : reportsForm.getCollectionGroupDesignations()) {
                SolrQuery query = solrQueryBuilder.buildSolrQueryForDeaccessionReports(date,ownInstitution,true,collectionGroupDesignation);
                query.setRows(0);
                QueryResponse queryResponse = solrTemplate.getSolrClient().query(query);
                SolrDocumentList results = queryResponse.getResults();
                long numFound = results.getNumFound();
                if (ownInstitution.equalsIgnoreCase(RecapConstants.PRINCETON)) {
                    if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_OPEN)) {
                        reportsForm.setDeaccessionOpenPulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_SHARED)) {
                        reportsForm.setDeaccessionSharedPulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_PRIVATE)) {
                        reportsForm.setDeaccessionPrivatePulCount(numFound);
                    }
                } else if (ownInstitution.equalsIgnoreCase(RecapConstants.COLUMBIA)) {
                    if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_OPEN)) {
                        reportsForm.setDeaccessionOpenCulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_SHARED)) {
                        reportsForm.setDeaccessionSharedCulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_PRIVATE)) {
                        reportsForm.setDeaccessionPrivateCulCount(numFound);
                    }
                } else if (ownInstitution.equalsIgnoreCase(RecapConstants.NYPL)) {
                    if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_OPEN)) {
                        reportsForm.setDeaccessionOpenNyplCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_SHARED)) {
                        reportsForm.setDeaccessionSharedNyplCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_PRIVATE)) {
                        reportsForm.setDeaccessionPrivateNyplCount(numFound);
                    }
                }
            }

        }
        reportsForm.setShowAccessionDeaccessionTable(true);
    }


    public void populateCGDItemCounts(ReportsForm reportsForm) throws Exception {
        for (String owningInstitution : reportsForm.getOwningInstitutions()) {
            for (String collectionGroupDesignation : reportsForm.getCollectionGroupDesignations()) {
                SolrQuery query = solrQueryBuilder.buildSolrQueryForCGDReports(owningInstitution, collectionGroupDesignation);
                query.setStart(0);
                QueryResponse queryResponse = solrTemplate.getSolrClient().query(query);
                SolrDocumentList results = queryResponse.getResults();
                long numFound = results.getNumFound();
                if (owningInstitution.equalsIgnoreCase(RecapConstants.PRINCETON)) {
                    if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_OPEN)) {
                        reportsForm.setOpenPulCgdCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_SHARED)) {
                        reportsForm.setSharedPulCgdCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_PRIVATE)) {
                        reportsForm.setPrivatePulCgdCount(numFound);
                    }
                } else if (owningInstitution.equalsIgnoreCase(RecapConstants.COLUMBIA)) {
                    if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_OPEN)) {
                        reportsForm.setOpenCulCgdCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_SHARED)) {
                        reportsForm.setSharedCulCgdCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_PRIVATE)) {
                        reportsForm.setPrivateCulCgdCount(numFound);
                    }
                } else if (owningInstitution.equalsIgnoreCase(RecapConstants.NYPL)) {
                    if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_OPEN)) {
                        reportsForm.setOpenNyplCgdCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_SHARED)) {
                        reportsForm.setSharedNyplCgdCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_PRIVATE)) {
                        reportsForm.setPrivateNyplCgdCount(numFound);
                    }
                }
            }
        }
    }

    public List<DeaccessionItemResultsRow> deaccessionReportFieldsInformation(ReportsForm reportsForm) throws Exception {
        String date = getSolrFormattedDates(reportsForm.getAccessionDeaccessionFromDate(),reportsForm.getAccessionDeaccessionToDate());
        SolrQuery query = solrQueryBuilder.buildSolrQueryForDeaccesionReportInformation(date, reportsForm.getDeaccessionOwnInst(), true);
        query.setRows(reportsForm.getPageSize());
        query.setStart(reportsForm.getPageNumber() * reportsForm.getPageSize());
        QueryResponse queryResponse = solrTemplate.getSolrClient().query(query);
        SolrDocumentList solrDocuments = queryResponse.getResults();
        long numFound = solrDocuments.getNumFound();
        reportsForm.setTotalRecordsCount(String.valueOf(numFound));
        int totalPagesCount = (int) Math.ceil((double) numFound / (double) reportsForm.getPageSize());
        reportsForm.setTotalPageCount(totalPagesCount);
        List<Item> itemList = new ArrayList<>();
        List<Integer> itemIdList = new ArrayList<>();
        for (Iterator<SolrDocument> solrDocumentIterator = solrDocuments.iterator(); solrDocumentIterator.hasNext(); ) {
            SolrDocument solrDocument = solrDocumentIterator.next();
            boolean isDeletedItem = (boolean) solrDocument.getFieldValue(RecapConstants.IS_DELETED_ITEM);
            if(isDeletedItem) {
                Item item = getItem(solrDocument);
                itemList.add(item);
                itemIdList.add(item.getItemId());
            }
        }
        SimpleDateFormat simpleDateFormat = getSimpleDateFormatForReports();
        List<DeaccessionItemResultsRow> deaccessionItemResultsRowList = new ArrayList<>();
        for(Item item : itemList){
            DeaccessionItemResultsRow deaccessionItemResultsRow = new DeaccessionItemResultsRow();
            deaccessionItemResultsRow.setItemId(item.getItemId());
            String deaccessionDate = simpleDateFormat.format(item.getItemLastUpdatedDate());
            Bib bib = bibSolrCrudRepository.findByBibId(item.getItemBibIdList().get(0));
            deaccessionItemResultsRow.setTitle(bib.getTitleDisplay());
            deaccessionItemResultsRow.setDeaccessionDate(deaccessionDate);
            deaccessionItemResultsRow.setDeaccessionOwnInst(item.getOwningInstitution());
            deaccessionItemResultsRow.setItemBarcode(item.getBarcode());
            ItemChangeLogEntity itemChangeLogEntity = itemChangeLogDetailsRepository.findByRecordIdAndOperationType(item.getItemId(),RecapConstants.REPORTS_DEACCESSION);
            if(null != itemChangeLogEntity) {
                deaccessionItemResultsRow.setDeaccessionNotes(itemChangeLogEntity.getNotes());
            }
            deaccessionItemResultsRow.setCgd(item.getCollectionGroupDesignation());
            deaccessionItemResultsRowList.add(deaccessionItemResultsRow);
        }
        return deaccessionItemResultsRowList;
    }

    public Item getItem(SolrDocument itemSolrDocument) {
        Item item = new Item();
        Collection<String> fieldNames = itemSolrDocument.getFieldNames();
        List<ItemValueResolver> itemValueResolvers = getItemValueResolvers();
        for (Iterator<String> iterator = fieldNames.iterator(); iterator.hasNext(); ) {
            String fieldName = iterator.next();
            Object fieldValue = itemSolrDocument.getFieldValue(fieldName);
            for (Iterator<ItemValueResolver> itemValueResolverIterator = itemValueResolvers.iterator(); itemValueResolverIterator.hasNext(); ) {
                ItemValueResolver itemValueResolver = itemValueResolverIterator.next();
                if (itemValueResolver.isInterested(fieldName)) {
                    itemValueResolver.setValue(item, fieldValue);
                }
            }
        }
        return item;
    }

    public List<ItemValueResolver> getItemValueResolvers() {
        if (null == itemValueResolvers) {
            itemValueResolvers = new ArrayList<>();
            itemValueResolvers.add(new BarcodeValueResolver());
            itemValueResolvers.add(new CollectionGroupDesignationValueResolver());
            itemValueResolvers.add(new ItemOwningInstitutionValueResolver());
            itemValueResolvers.add(new ItemIdValueResolver());
            itemValueResolvers.add(new ItemLastUpdatedDateValueResolver());
            itemValueResolvers.add(new ItemBibIdValueResolver());
        }
        return itemValueResolvers;
    }



    public String getFormattedDateString(Date inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RecapConstants.DATE_FORMAT_YYYYMMDDHHMM);
        String utcStr = null;
        try {
            String dateString = simpleDateFormat.format(inputDate);
            Date date = simpleDateFormat.parse(dateString);
            DateFormat format = new SimpleDateFormat(RecapConstants.UTC_DATE_FORMAT);
            format.setTimeZone(TimeZone.getTimeZone(RecapConstants.UTC));
            utcStr = format.format(date);
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return utcStr;
    }


    public Date getFromDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    public Date getToDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY,23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,59);
        return cal.getTime();
    }

    private String getSolrFormattedDates(String requestedFromDate, String requestedToDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = getSimpleDateFormatForReports();
        Date fromDate = simpleDateFormat.parse(requestedFromDate);
        Date toDate = simpleDateFormat.parse(requestedToDate);
        Date fromDateTime = getFromDate(fromDate);
        Date toDateTime = getToDate(toDate);
        String formattedFromDate = getFormattedDateString(fromDateTime);
        String formattedToDate = getFormattedDateString(toDateTime);
        return formattedFromDate + " TO " + formattedToDate;
    }

    private SimpleDateFormat getSimpleDateFormatForReports() {
        return new SimpleDateFormat(RecapConstants.SIMPLE_DATE_FORMAT_REPORTS);
    }

}
