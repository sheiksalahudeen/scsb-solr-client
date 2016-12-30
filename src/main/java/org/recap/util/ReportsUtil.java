package org.recap.util;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.recap.RecapConstants;
import org.recap.model.search.DeaccessionItemResultsRow;
import org.recap.model.search.ReportsForm;
import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.search.resolver.impl.item.*;
import org.recap.model.solr.Bib;
import org.recap.model.solr.Item;
import org.recap.repository.jpa.NotesDetailsRepository;
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
    NotesDetailsRepository notesDetailsRepository;

    List<ItemValueResolver> itemValueResolvers;

    public void populateILBDCountsForRequest(ReportsForm reportsForm, Date requestFromDate, Date requestToDate) {
        reportsForm.setIlRequestPulCount(requestItemDetailsRepository.getIlRequestCounts(requestFromDate, requestToDate, 1, Arrays.asList(2, 3)));
        reportsForm.setIlRequestCulCount(requestItemDetailsRepository.getIlRequestCounts(requestFromDate, requestToDate, 2, Arrays.asList(1, 3)));
        reportsForm.setIlRequestNyplCount(requestItemDetailsRepository.getIlRequestCounts(requestFromDate, requestToDate, 3, Arrays.asList(1, 2)));
        reportsForm.setBdRequestPulCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, 1, 5));
        reportsForm.setBdRequestCulCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, 2, 5));
        reportsForm.setBdRequestNyplCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, 3, 5));
        reportsForm.setShowILBDResults(true);
        reportsForm.setShowReportResultsText(true);
        reportsForm.setShowNoteILBD(true);
    }

    public void populatePartnersCountForRequest(ReportsForm reportsForm, Date requestFromDate, Date requestToDate) {
        reportsForm.setPhysicalPrivatePulCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, 1, Arrays.asList(3), Arrays.asList(1, 2, 3, 5)));
        reportsForm.setPhysicalPrivateCulCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, 2, Arrays.asList(3), Arrays.asList(1, 2, 3, 5)));
        reportsForm.setPhysicalPrivateNyplCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, 3, Arrays.asList(3), Arrays.asList(1, 2, 3, 5)));
        reportsForm.setPhysicalSharedPulCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, 1, Arrays.asList(1, 2), Arrays.asList(1, 2, 3, 5)));
        reportsForm.setPhysicalSharedCulCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, 2, Arrays.asList(1, 2), Arrays.asList(1, 2, 3, 5)));
        reportsForm.setPhysicalSharedNyplCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, 3, Arrays.asList(1, 2), Arrays.asList(1, 2, 3, 5)));
        reportsForm.setEddPrivatePulCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, 1, Arrays.asList(3), Arrays.asList(4)));
        reportsForm.setEddPrivateCulCount( requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, 2, Arrays.asList(3), Arrays.asList(4)));
        reportsForm.setEddPrivateNyplCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, 3, Arrays.asList(3), Arrays.asList(4)));
        reportsForm.setEddSharedOpenPulCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, 1, Arrays.asList(1, 2), Arrays.asList(4)));
        reportsForm.setEddSharedOpenCulCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, 2, Arrays.asList(1, 2), Arrays.asList(4)));
        reportsForm.setEddSharedOpenNyplCount(requestItemDetailsRepository.getPhysicalAndEDDCounts(requestFromDate, requestToDate, 3, Arrays.asList(1, 2), Arrays.asList(4)));
        reportsForm.setShowPartners(true);
        reportsForm.setShowReportResultsText(true);
        reportsForm.setShowNotePartners(true);
    }


    public void populateRequestTypeInformation(ReportsForm reportsForm, Date requestFromDate, Date requestToDate) {
        for (String requestType : reportsForm.getReportRequestType()) {
            if (requestType.equalsIgnoreCase("Retrivel")) {
                reportsForm.setRetrievalRequestPulCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, 1, 2));
                reportsForm.setRetrievalRequestCulCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, 2, 2));
                reportsForm.setRetrievalRequestNyplCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, 3, 2));
                reportsForm.setShowRetrievalTable(true);
            }
            if (requestType.equalsIgnoreCase("Hold")) {
                reportsForm.setHoldRequestPulCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, 1, 1));
                reportsForm.setHoldRequestCulCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, 2, 1));
                reportsForm.setHoldRequestNyplCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, 3, 1));
                reportsForm.setShowHoldTable(true);
            }
            if (requestType.equalsIgnoreCase("Recall")) {
                reportsForm.setRecallRequestPulCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, 1, 3));
                reportsForm.setRecallRequestCulCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, 1, 3));
                reportsForm.setRecallRequestNyplCount(requestItemDetailsRepository.getBDHoldRecallRetrievalRequestCounts(requestFromDate, requestToDate, 1, 3));
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
                if (owningInstitution.equalsIgnoreCase("PUL")) {
                    if (collectionGroupDesignation.equalsIgnoreCase("Open")) {
                        reportsForm.setAccessionOpenPulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase("Shared")) {
                        reportsForm.setAccessionSharedOpenPulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase("Private")) {
                        reportsForm.setAccessionPrivatePulCount(numFound);
                    }
                } else if (owningInstitution.equalsIgnoreCase("CUL")) {
                    if (collectionGroupDesignation.equalsIgnoreCase("Open")) {
                        reportsForm.setAccessionOpenCulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase("Shared")) {
                        reportsForm.setAccessionSharedOpenCulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase("Private")) {
                        reportsForm.setAccessionPrivateCulCount(numFound);
                    }
                } else if (owningInstitution.equalsIgnoreCase("NYPL")) {
                    if (collectionGroupDesignation.equalsIgnoreCase("Open")) {
                        reportsForm.setAccessionOpenNyplCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase("Shared")) {
                        reportsForm.setAccessionSharedOpenNyplCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase("Private")) {
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
                if (ownInstitution.equalsIgnoreCase("PUL")) {
                    if (collectionGroupDesignation.equalsIgnoreCase("Open")) {
                        reportsForm.setDeaccessionOpenPulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase("Shared")) {
                        reportsForm.setDeaccessionSharedPulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase("Private")) {
                        reportsForm.setDeaccessionPrivatePulCount(numFound);
                    }
                } else if (ownInstitution.equalsIgnoreCase("CUL")) {
                    if (collectionGroupDesignation.equalsIgnoreCase("Open")) {
                        reportsForm.setDeaccessionOpenCulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase("Shared")) {
                        reportsForm.setDeaccessionSharedCulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase("Private")) {
                        reportsForm.setDeaccessionPrivateCulCount(numFound);
                    }
                } else if (ownInstitution.equalsIgnoreCase("NYPL")) {
                    if (collectionGroupDesignation.equalsIgnoreCase("Open")) {
                        reportsForm.setDeaccessionOpenNyplCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase("Shared")) {
                        reportsForm.setDeaccessionSharedNyplCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase("Private")) {
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
                if (owningInstitution.equalsIgnoreCase("PUL")) {
                    if (collectionGroupDesignation.equalsIgnoreCase("Open")) {
                        reportsForm.setOpenPulCgdCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase("Shared")) {
                        reportsForm.setSharedPulCgdCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase("Private")) {
                        reportsForm.setPrivatePulCgdCount(numFound);
                    }
                } else if (owningInstitution.equalsIgnoreCase("CUL")) {
                    if (collectionGroupDesignation.equalsIgnoreCase("Open")) {
                        reportsForm.setOpenCulCgdCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase("Shared")) {
                        reportsForm.setSharedCulCgdCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase("Private")) {
                        reportsForm.setPrivateCulCgdCount(numFound);
                    }
                } else if (owningInstitution.equalsIgnoreCase("NYPL")) {
                    if (collectionGroupDesignation.equalsIgnoreCase("Open")) {
                        reportsForm.setOpenNyplCgdCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase("Shared")) {
                        reportsForm.setSharedNyplCgdCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase("Private")) {
                        reportsForm.setPrivateNyplCgdCount(numFound);
                    }
                }
            }
        }
    }

    public List<DeaccessionItemResultsRow> deaccessionReportFieldsInformation(String requestedFromDate, String requestedToDate, String ownInst) throws Exception {
        String date = getSolrFormattedDates(requestedFromDate,requestedToDate);
        SolrQuery query = solrQueryBuilder.buildSolrQueryForDeaccesionReportInformation(date, ownInst, true);
        QueryResponse queryResponse = solrTemplate.getSolrClient().query(query);
        SolrDocumentList results = queryResponse.getResults();
        long numfound = results.getNumFound();
        query.setRows((int) numfound);
        List<Item> itemList = new ArrayList<>();
        List<Integer> itemIdList = new ArrayList<>();
        for (Iterator<SolrDocument> solrDocumentIterator = results.iterator(); solrDocumentIterator.hasNext(); ) {
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
           /* NotesEntity notesEntity = notesDetailsRepository.findByItemId(item.getItemId());
            deaccessionItemResultsRow.setDeaccessionNotes(notesEntity.getNotes());*/
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
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
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
        return new SimpleDateFormat(RecapConstants.Simple_Date_Format_REPORTS);
    }

}
