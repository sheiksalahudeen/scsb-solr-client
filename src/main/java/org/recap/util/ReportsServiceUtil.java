package org.recap.util;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.recap.RecapConstants;
import org.recap.model.jpa.ItemChangeLogEntity;
import org.recap.model.reports.ReportsRequest;
import org.recap.model.reports.ReportsResponse;
import org.recap.model.search.DeaccessionItemResultsRow;
import org.recap.model.search.IncompleteReportResultsRow;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.search.SearchResultRow;
import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.search.resolver.impl.item.*;
import org.recap.model.solr.Bib;
import org.recap.model.solr.Item;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.repository.solr.impl.BibSolrDocumentRepositoryImpl;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by rajeshbabuk on 13/1/17.
 */

@Service
public class ReportsServiceUtil {

    private static final Logger logger = LoggerFactory.getLogger(ReportsServiceUtil.class);

    @Autowired
    SolrTemplate solrTemplate;

    @Autowired
    SolrQueryBuilder solrQueryBuilder;

    @Autowired
    BibSolrCrudRepository bibSolrCrudRepository;

    @Autowired
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    List<ItemValueResolver> itemValueResolvers;

    @Autowired
    BibSolrDocumentRepositoryImpl bibSolrDocumentRepository;

    @Autowired
    SearchRecordsUtil searchRecordsUtil;


    public ReportsResponse populateAccessionDeaccessionItemCounts(ReportsRequest reportsRequest) throws Exception {
        ReportsResponse reportsResponse = new ReportsResponse();
        String solrFormattedDate = getSolrFormattedDates(reportsRequest.getAccessionDeaccessionFromDate(), reportsRequest.getAccessionDeaccessionToDate());
        populateAccessionCounts(reportsRequest, reportsResponse, solrFormattedDate);
        populateDeaccessionCounts(reportsRequest, reportsResponse, solrFormattedDate);
        return reportsResponse;
    }

    public ReportsResponse populateCgdItemCounts(ReportsRequest reportsRequest) throws Exception {
        ReportsResponse reportsResponse = new ReportsResponse();
        for (String owningInstitution : reportsRequest.getOwningInstitutions()) {
            for (String collectionGroupDesignation : reportsRequest.getCollectionGroupDesignations()) {
                SolrQuery query = solrQueryBuilder.buildSolrQueryForCGDReports(owningInstitution, collectionGroupDesignation);
                query.setStart(0);
                QueryResponse queryResponse = solrTemplate.getSolrClient().query(query);
                SolrDocumentList results = queryResponse.getResults();
                long numFound = results.getNumFound();
                if (owningInstitution.equalsIgnoreCase(RecapConstants.PRINCETON)) {
                    if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_OPEN)) {
                        reportsResponse.setOpenPulCgdCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_SHARED)) {
                        reportsResponse.setSharedPulCgdCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_PRIVATE)) {
                        reportsResponse.setPrivatePulCgdCount(numFound);
                    }
                } else if (owningInstitution.equalsIgnoreCase(RecapConstants.COLUMBIA)) {
                    if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_OPEN)) {
                        reportsResponse.setOpenCulCgdCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_SHARED)) {
                        reportsResponse.setSharedCulCgdCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_PRIVATE)) {
                        reportsResponse.setPrivateCulCgdCount(numFound);
                    }
                } else if (owningInstitution.equalsIgnoreCase(RecapConstants.NYPL)) {
                    if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_OPEN)) {
                        reportsResponse.setOpenNyplCgdCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_SHARED)) {
                        reportsResponse.setSharedNyplCgdCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_PRIVATE)) {
                        reportsResponse.setPrivateNyplCgdCount(numFound);
                    }
                }
            }
        }
        return reportsResponse;
    }

    public ReportsResponse populateDeaccessionResults(ReportsRequest reportsRequest) throws Exception {
        ReportsResponse reportsResponse = new ReportsResponse();
        String date = getSolrFormattedDates(reportsRequest.getAccessionDeaccessionFromDate(), reportsRequest.getAccessionDeaccessionToDate());
        SolrQuery query = solrQueryBuilder.buildSolrQueryForDeaccesionReportInformation(date, reportsRequest.getDeaccessionOwningInstitution(), true);
        query.setRows(reportsRequest.getPageSize());
        query.setStart(reportsRequest.getPageNumber() * reportsRequest.getPageSize());
        QueryResponse queryResponse = solrTemplate.getSolrClient().query(query);
        SolrDocumentList solrDocuments = queryResponse.getResults();
        long numFound = solrDocuments.getNumFound();
        reportsResponse.setTotalRecordsCount(String.valueOf(numFound));
        int totalPagesCount = (int) Math.ceil((double) numFound / (double) reportsRequest.getPageSize());
        if(totalPagesCount == 0){
            reportsResponse.setTotalPageCount(1);
        }else{
            reportsResponse.setTotalPageCount(totalPagesCount);
        }
        List<Item> itemList = new ArrayList<>();
        List<Integer> itemIdList = new ArrayList<>();
        for (Iterator<SolrDocument> solrDocumentIterator = solrDocuments.iterator(); solrDocumentIterator.hasNext(); ) {
            SolrDocument solrDocument = solrDocumentIterator.next();
            boolean isDeletedItem = (boolean) solrDocument.getFieldValue(RecapConstants.IS_DELETED_ITEM);
            if (isDeletedItem) {
                Item item = getItem(solrDocument);
                itemList.add(item);
                itemIdList.add(item.getItemId());
            }
        }
        SimpleDateFormat simpleDateFormat = getSimpleDateFormatForReports();
        List<DeaccessionItemResultsRow> deaccessionItemResultsRowList = new ArrayList<>();
        for (Item item : itemList) {
            DeaccessionItemResultsRow deaccessionItemResultsRow = new DeaccessionItemResultsRow();
            deaccessionItemResultsRow.setItemId(item.getItemId());
            String deaccessionDate = simpleDateFormat.format(item.getItemLastUpdatedDate());
            Bib bib = bibSolrCrudRepository.findByBibId(item.getItemBibIdList().get(0));
            deaccessionItemResultsRow.setTitle(bib.getTitleDisplay());
            deaccessionItemResultsRow.setDeaccessionDate(deaccessionDate);
            deaccessionItemResultsRow.setDeaccessionOwnInst(item.getOwningInstitution());
            deaccessionItemResultsRow.setItemBarcode(item.getBarcode());
            ItemChangeLogEntity itemChangeLogEntity = itemChangeLogDetailsRepository.findByRecordIdAndOperationType(item.getItemId(), RecapConstants.REPORTS_DEACCESSION);
            if (null != itemChangeLogEntity) {
                deaccessionItemResultsRow.setDeaccessionNotes(itemChangeLogEntity.getNotes());
            }
            deaccessionItemResultsRow.setCgd(item.getCollectionGroupDesignation());
            deaccessionItemResultsRowList.add(deaccessionItemResultsRow);
        }
        reportsResponse.setDeaccessionItemResultsRows(deaccessionItemResultsRowList);
        return reportsResponse;
    }

    public ReportsResponse populateIncompleteRecordsReport(ReportsRequest reportsRequest) throws Exception {
        ReportsResponse reportsResponse = new ReportsResponse();
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldName(RecapConstants.ITEM_CATALOGING_STATUS);
        searchRecordsRequest.setCatalogingStatus(RecapConstants.INCOMPLETE_STATUS);
        searchRecordsRequest.setPageSize(reportsRequest.getIncompletePageSize());
        if(!reportsRequest.isExport()){
            searchRecordsRequest.setPageNumber(reportsRequest.getIncompletePageNumber());
        }
        searchRecordsRequest.setOwningInstitutions(Arrays.asList(reportsRequest.getIncompleteRequestingInstitution()));
        List<SearchResultRow> searchResultRows = searchRecordsUtil.searchRecords(searchRecordsRequest);
        if(reportsRequest.isExport()){
            Integer totalRecordsCount = Integer.valueOf(searchRecordsRequest.getTotalRecordsCount());
            if (totalRecordsCount > reportsRequest.getIncompletePageSize()) {
                searchRecordsRequest.setPageSize(totalRecordsCount);
                searchResultRows = searchRecordsUtil.searchRecords(searchRecordsRequest);
            }
        }
        List<IncompleteReportResultsRow> incompleteReportResultsRows = new ArrayList<>();
        for (SearchResultRow searchResultRow : searchResultRows) {
            IncompleteReportResultsRow incompleteReportResultsRow = new IncompleteReportResultsRow();
            incompleteReportResultsRow.setTitle(searchResultRow.getTitle());
            incompleteReportResultsRow.setOwningInstitution(searchResultRow.getOwningInstitution());
            incompleteReportResultsRow.setAuthor(searchResultRow.getAuthorSearch());
            incompleteReportResultsRow.setCreatedDate(getFormattedDates(searchResultRow.getBibCreatedDate()));
            incompleteReportResultsRow.setCustomerCode(searchResultRow.getCustomerCode());
            incompleteReportResultsRow.setBarcode(searchResultRow.getBarcode());
            incompleteReportResultsRows.add(incompleteReportResultsRow);
        }
        reportsResponse.setIncompletePageNumber(searchRecordsRequest.getPageNumber());
        reportsResponse.setIncompletePageSize(searchRecordsRequest.getPageSize());
        reportsResponse.setIncompleteTotalPageCount(searchRecordsRequest.getTotalPageCount());
        reportsResponse.setIncompleteTotalRecordsCount(searchRecordsRequest.getTotalItemRecordsCount());
        reportsResponse.setIncompleteReportResultsRows(incompleteReportResultsRows);
        return reportsResponse;
    }

    private String getFormattedDates(Date gotDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/YYYY");
        return simpleDateFormat.format(gotDate);

    }



    private void populateAccessionCounts(ReportsRequest reportsRequest, ReportsResponse reportsResponse, String solrFormattedDate) throws Exception {
        for (String owningInstitution : reportsRequest.getOwningInstitutions()) {
            for (String collectionGroupDesignation : reportsRequest.getCollectionGroupDesignations()) {
                SolrQuery query = solrQueryBuilder.buildSolrQueryForAccessionReports(solrFormattedDate, owningInstitution, false, collectionGroupDesignation);
                query.setRows(0);
                QueryResponse queryResponse = solrTemplate.getSolrClient().query(query);
                SolrDocumentList results = queryResponse.getResults();
                long numFound = results.getNumFound();
                if (owningInstitution.equalsIgnoreCase(RecapConstants.PRINCETON)) {
                    if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_OPEN)) {
                        reportsResponse.setAccessionOpenPulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_SHARED)) {
                        reportsResponse.setAccessionSharedPulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_PRIVATE)) {
                        reportsResponse.setAccessionPrivatePulCount(numFound);
                    }
                } else if (owningInstitution.equalsIgnoreCase(RecapConstants.COLUMBIA)) {
                    if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_OPEN)) {
                        reportsResponse.setAccessionOpenCulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_SHARED)) {
                        reportsResponse.setAccessionSharedCulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_PRIVATE)) {
                        reportsResponse.setAccessionPrivateCulCount(numFound);
                    }
                } else if (owningInstitution.equalsIgnoreCase(RecapConstants.NYPL)) {
                    if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_OPEN)) {
                        reportsResponse.setAccessionOpenNyplCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_SHARED)) {
                        reportsResponse.setAccessionSharedNyplCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_PRIVATE)) {
                        reportsResponse.setAccessionPrivateNyplCount(numFound);
                    }
                }
            }
        }
    }

    private void populateDeaccessionCounts(ReportsRequest reportsRequest, ReportsResponse reportsResponse, String solrFormattedDate) throws Exception {
        for (String ownInstitution : reportsRequest.getOwningInstitutions()) {
            for (String collectionGroupDesignation : reportsRequest.getCollectionGroupDesignations()) {
                SolrQuery query = solrQueryBuilder.buildSolrQueryForDeaccessionReports(solrFormattedDate, ownInstitution, true, collectionGroupDesignation);
                query.setRows(0);
                QueryResponse queryResponse = solrTemplate.getSolrClient().query(query);
                SolrDocumentList results = queryResponse.getResults();
                long numFound = results.getNumFound();
                if (ownInstitution.equalsIgnoreCase(RecapConstants.PRINCETON)) {
                    if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_OPEN)) {
                        reportsResponse.setDeaccessionOpenPulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_SHARED)) {
                        reportsResponse.setDeaccessionSharedPulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_PRIVATE)) {
                        reportsResponse.setDeaccessionPrivatePulCount(numFound);
                    }
                } else if (ownInstitution.equalsIgnoreCase(RecapConstants.COLUMBIA)) {
                    if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_OPEN)) {
                        reportsResponse.setDeaccessionOpenCulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_SHARED)) {
                        reportsResponse.setDeaccessionSharedCulCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_PRIVATE)) {
                        reportsResponse.setDeaccessionPrivateCulCount(numFound);
                    }
                } else if (ownInstitution.equalsIgnoreCase(RecapConstants.NYPL)) {
                    if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_OPEN)) {
                        reportsResponse.setDeaccessionOpenNyplCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_SHARED)) {
                        reportsResponse.setDeaccessionSharedNyplCount(numFound);
                    } else if (collectionGroupDesignation.equalsIgnoreCase(RecapConstants.REPORTS_PRIVATE)) {
                        reportsResponse.setDeaccessionPrivateNyplCount(numFound);
                    }
                }
            }
        }
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

    private String getFormattedDateString(Date inputDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RecapConstants.DATE_FORMAT_YYYYMMDDHHMM);
        String utcStr;
        String dateString = simpleDateFormat.format(inputDate);
        Date date = simpleDateFormat.parse(dateString);
        DateFormat format = new SimpleDateFormat(RecapConstants.UTC_DATE_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone(RecapConstants.UTC));
        utcStr = format.format(date);
        return utcStr;
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
}
