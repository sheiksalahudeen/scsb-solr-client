package org.recap.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.recap.RecapConstants;
import org.recap.model.IncompleteReportBibDetails;
import org.recap.model.jpa.ItemChangeLogEntity;
import org.recap.model.reports.ReportsRequest;
import org.recap.model.reports.ReportsResponse;
import org.recap.model.search.DeaccessionItemResultsRow;
import org.recap.model.search.IncompleteReportResultsRow;
import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.search.resolver.impl.item.*;
import org.recap.model.solr.Item;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.repository.solr.impl.BibSolrDocumentRepositoryImpl;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by rajeshbabuk on 13/1/17.
 */
@Service
public class ReportsServiceUtil {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private SolrQueryBuilder solrQueryBuilder;

    @Autowired
    private BibSolrCrudRepository bibSolrCrudRepository;

    @Autowired
    private ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    private List<ItemValueResolver> itemValueResolvers;

    @Autowired
    private BibSolrDocumentRepositoryImpl bibSolrDocumentRepository;

    @Autowired
    private SearchRecordsUtil searchRecordsUtil;

    @Autowired
    private DateUtil dateUtil;


    /**
     * This method populates accession and deaccession item counts from solr for report screen in UI.
     *
     * @param reportsRequest the reports request
     * @return the reports response
     * @throws Exception the exception
     */
    public ReportsResponse populateAccessionDeaccessionItemCounts(ReportsRequest reportsRequest) throws Exception {
        ReportsResponse reportsResponse = new ReportsResponse();
        String solrFormattedDate = getSolrFormattedDates(reportsRequest.getAccessionDeaccessionFromDate(), reportsRequest.getAccessionDeaccessionToDate());
        populateAccessionCounts(reportsRequest, reportsResponse, solrFormattedDate);
        populateDeaccessionCounts(reportsRequest, reportsResponse, solrFormattedDate);
        return reportsResponse;
    }

    /**
     * This method populates cgd item counts from solr for report screen in UI.
     *
     * @param reportsRequest the reports request
     * @return the reports response
     * @throws Exception the exception
     */
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

    /**
     * This method gets deaccession information results from solr and populate them in report screen (UI).
     *
     * @param reportsRequest the reports request
     * @return the reports response
     * @throws Exception the exception
     */
    public ReportsResponse populateDeaccessionResults(ReportsRequest reportsRequest) throws Exception {
        ReportsResponse reportsResponse = new ReportsResponse();
        String date = getSolrFormattedDates(reportsRequest.getAccessionDeaccessionFromDate(), reportsRequest.getAccessionDeaccessionToDate());
        SolrQuery query = solrQueryBuilder.buildSolrQueryForDeaccesionReportInformation(date, reportsRequest.getDeaccessionOwningInstitution(), true);
        query.setRows(reportsRequest.getPageSize());
        query.setStart(reportsRequest.getPageNumber() * reportsRequest.getPageSize());
        query.setSort(RecapConstants.ITEM_LAST_UPDATED_DATE, SolrQuery.ORDER.desc);
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
        List<Integer> bibIdList = new ArrayList<>();
        for (Iterator<SolrDocument> solrDocumentIterator = solrDocuments.iterator(); solrDocumentIterator.hasNext(); ) {
            SolrDocument solrDocument = solrDocumentIterator.next();
            boolean isDeletedItem = (boolean) solrDocument.getFieldValue(RecapConstants.IS_DELETED_ITEM);
            if (isDeletedItem) {
                Item item = getItem(solrDocument);
                itemList.add(item);
                itemIdList.add(item.getItemId());
                bibIdList.add(item.getItemBibIdList().get(0));
            }
        }
        String bibIdJoin = StringUtils.join(bibIdList, ",");
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(RecapConstants.BIB_DOC_TYPE);
        solrQuery.addFilterQuery(RecapConstants.SOLR_BIB_ID+StringEscapeUtils.escapeJava(bibIdJoin).replace(",","\" \""));
        solrQuery.addFilterQuery(RecapConstants.IS_DELETED_BIB_TRUE);
        solrQuery.setFields(RecapConstants.BIB_ID,RecapConstants.TITLE_DISPLAY);
        solrQuery.setRows(reportsRequest.getPageSize());
        QueryResponse response = solrTemplate.getSolrClient().query(solrQuery);
        Map<Integer,String> map = new HashMap<>();
        SolrDocumentList list = response.getResults();
        for (Iterator<SolrDocument> iterator = list.iterator(); iterator.hasNext(); ) {
            SolrDocument solrDocument = iterator.next();
            map.put((Integer) solrDocument.getFieldValue(RecapConstants.BIB_ID),(String)solrDocument.getFieldValue(RecapConstants.TITLE_DISPLAY));
        }
        SimpleDateFormat simpleDateFormat = getSimpleDateFormatForReports();
        List<DeaccessionItemResultsRow> deaccessionItemResultsRowList = new ArrayList<>();
        for (Item item : itemList) {
            DeaccessionItemResultsRow deaccessionItemResultsRow = new DeaccessionItemResultsRow();
            deaccessionItemResultsRow.setItemId(item.getItemId());
            String deaccessionDate = simpleDateFormat.format(item.getItemLastUpdatedDate());
            if(map.containsKey(item.getItemBibIdList().get(0))){
                deaccessionItemResultsRow.setTitle(map.get(item.getItemBibIdList().get(0)));
            }
            deaccessionItemResultsRow.setDeaccessionDate(deaccessionDate);
            deaccessionItemResultsRow.setDeaccessionOwnInst(item.getOwningInstitution());
            deaccessionItemResultsRow.setItemBarcode(item.getBarcode());
            List<ItemChangeLogEntity> itemChangeLogEntityList = itemChangeLogDetailsRepository.findByRecordIdAndOperationTypeAndOrderByUpdatedDateDesc(item.getItemId(), RecapConstants.REPORTS_DEACCESSION);
            if (CollectionUtils.isNotEmpty(itemChangeLogEntityList)) {
                ItemChangeLogEntity itemChangeLogEntity = itemChangeLogEntityList.get(0);
                deaccessionItemResultsRow.setDeaccessionNotes(itemChangeLogEntity.getNotes());
            }
            deaccessionItemResultsRow.setDeaccessionCreatedBy(item.getItemLastUpdatedBy());
            deaccessionItemResultsRow.setCgd(item.getCollectionGroupDesignation());
            deaccessionItemResultsRowList.add(deaccessionItemResultsRow);
        }
        reportsResponse.setDeaccessionItemResultsRows(deaccessionItemResultsRowList);
        return reportsResponse;
    }

    /**
     * This method is used to populate incomplete records report.
     *
     * @param reportsRequest the reports request
     * @return the reports response
     * @throws Exception the exception
     */
    public ReportsResponse populateIncompleteRecordsReport(ReportsRequest reportsRequest) throws Exception {
        ReportsResponse reportsResponse = new ReportsResponse();
        SolrQuery solrQuery;
        QueryResponse queryResponse;
        SolrDocumentList itemDocumentList;
        solrQuery = solrQueryBuilder.buildSolrQueryForIncompleteReports(reportsRequest.getIncompleteRequestingInstitution());
        if (!reportsRequest.isExport()){
            solrQuery.setStart(reportsRequest.getIncompletePageSize() * reportsRequest.getIncompletePageNumber());
            solrQuery.setRows(reportsRequest.getIncompletePageSize());
        }
        solrQuery.setSort(RecapConstants.ITEM_CREATED_DATE, SolrQuery.ORDER.desc);
        queryResponse = solrTemplate.getSolrClient().query(solrQuery);
        itemDocumentList = queryResponse.getResults();
        long numFound = itemDocumentList.getNumFound();
        if (reportsRequest.isExport()){
            solrQuery = solrQueryBuilder.buildSolrQueryForIncompleteReports(reportsRequest.getIncompleteRequestingInstitution());
            solrQuery.setStart(0);
            solrQuery.setRows((int) numFound);
            solrQuery.setSort(RecapConstants.ITEM_CREATED_DATE, SolrQuery.ORDER.desc);
            queryResponse = solrTemplate.getSolrClient().query(solrQuery);
            itemDocumentList= queryResponse.getResults();
        }
        List<Integer> bibIdList = new ArrayList<>();
        List<Item> itemList = new ArrayList<>();
        for (SolrDocument itemDocument : itemDocumentList) {
            Item item = getItem(itemDocument);
            itemList.add(item);
            bibIdList.add((Integer)item.getItemBibIdList().get(0));
        }
        Map<Integer, IncompleteReportBibDetails> bibDetailsMap = getBibDetailsIncompleteReport(bibIdList);
        List<IncompleteReportResultsRow> incompleteReportResultsRows = new ArrayList<>();
        for (Item item : itemList) {
            IncompleteReportResultsRow incompleteReportResultsRow = new IncompleteReportResultsRow();
            incompleteReportResultsRow.setOwningInstitution(item.getOwningInstitution());
            IncompleteReportBibDetails incompleteReportBibDetails = bibDetailsMap.get(item.getItemBibIdList().get(0));
            if(incompleteReportBibDetails!=null){
                incompleteReportResultsRow.setTitle(incompleteReportBibDetails.getTitle());
                incompleteReportResultsRow.setAuthor(incompleteReportBibDetails.getAuthorDisplay());
            }
            incompleteReportResultsRow.setCreatedDate(getFormattedDates(item.getItemCreatedDate()));
            incompleteReportResultsRow.setCustomerCode(item.getCustomerCode());
            incompleteReportResultsRow.setBarcode(item.getBarcode());
            incompleteReportResultsRows.add(incompleteReportResultsRow);
        }
        int totalPagesCount = (int) Math.ceil((double) numFound / (double) reportsRequest.getIncompletePageSize());
        reportsResponse.setIncompleteTotalPageCount(totalPagesCount);
        reportsResponse.setIncompleteTotalRecordsCount(String.valueOf(numFound));
        reportsResponse.setIncompleteReportResultsRows(incompleteReportResultsRows);
        return reportsResponse;
    }

    private Map<Integer, IncompleteReportBibDetails> getBibDetailsIncompleteReport(List<Integer> bibIdList) throws SolrServerException, IOException {
        SolrQuery bibDetailsQuery;
        QueryResponse bibDetailsResponse;
        bibDetailsQuery  = solrQueryBuilder.buildSolrQueryToGetBibDetails(bibIdList,bibIdList.size());
        bibDetailsResponse = solrTemplate.getSolrClient().query(bibDetailsQuery, SolrRequest.METHOD.POST);
        if(bibIdList.size() != bibDetailsResponse.getResults().getNumFound()){
            bibDetailsQuery = solrQueryBuilder.buildSolrQueryToGetBibDetails(bibIdList, (int) bibDetailsResponse.getResults().getNumFound());
            bibDetailsResponse = solrTemplate.getSolrClient().query(bibDetailsQuery, SolrRequest.METHOD.POST);
        }
        SolrDocumentList bibDocumentList = bibDetailsResponse.getResults();
        Map<Integer,IncompleteReportBibDetails> bibDetailsMap = new HashMap<>();
        for (SolrDocument bibDetail : bibDocumentList) {
            IncompleteReportBibDetails incompleteReportBibDetails = new IncompleteReportBibDetails();
            incompleteReportBibDetails.setTitle((String)bibDetail.getFieldValue(RecapConstants.TITLE_DISPLAY));
            incompleteReportBibDetails.setAuthorDisplay((String)bibDetail.getFieldValue(RecapConstants.AUTHOR_DISPLAY));
            bibDetailsMap.put((Integer)bibDetail.getFieldValue(RecapConstants.BIB_ID),incompleteReportBibDetails);
        }
        return bibDetailsMap;
    }

    private String getFormattedDates(Date gotDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RecapConstants.SIMPLE_DATE_FORMAT_REPORTS);
        return simpleDateFormat.format(gotDate);

    }


    /**
     * This method gets the accession count from solr
     * @param reportsRequest
     * @param reportsResponse
     * @param solrFormattedDate
     * @throws Exception
     */
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

    /**
     * This method gets the deaccession count from the solr
     * @param reportsRequest
     * @param reportsResponse
     * @param solrFormattedDate
     * @throws Exception
     */
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
        Date fromDateTime = dateUtil.getFromDate(fromDate);
        Date toDateTime = dateUtil.getToDate(toDate);
        String formattedFromDate = getFormattedDateString(fromDateTime);
        String formattedToDate = getFormattedDateString(toDateTime);
        return formattedFromDate + " TO " + formattedToDate;
    }

    private SimpleDateFormat getSimpleDateFormatForReports() {
        return new SimpleDateFormat(RecapConstants.SIMPLE_DATE_FORMAT_REPORTS);
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

    /**
     * This method gets item for the given item solr document.
     *
     * @param itemSolrDocument the item solr document
     * @return the item
     */
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

    /**
     * This method gets item value resolvers.
     *
     * @return the item value resolvers
     */
    public List<ItemValueResolver> getItemValueResolvers() {
        if (null == itemValueResolvers) {
            itemValueResolvers = new ArrayList<>();
            itemValueResolvers.add(new BarcodeValueResolver());
            itemValueResolvers.add(new CollectionGroupDesignationValueResolver());
            itemValueResolvers.add(new ItemOwningInstitutionValueResolver());
            itemValueResolvers.add(new ItemIdValueResolver());
            itemValueResolvers.add(new ItemLastUpdatedDateValueResolver());
            itemValueResolvers.add(new ItemBibIdValueResolver());
            itemValueResolvers.add(new ItemLastUpdatedByValueResolver());
            itemValueResolvers.add(new ItemCreatedDateValueResolver());
            itemValueResolvers.add(new CustomerCodeValueResolver());
        }
        return itemValueResolvers;
    }
}
