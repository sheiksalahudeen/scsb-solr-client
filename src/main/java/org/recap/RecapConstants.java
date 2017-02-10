package org.recap;

/**
 * Created by SheikS on 6/20/2016.
 */
public class RecapConstants {
    public static final String PATH_SEPARATOR = "/";
    public static final String PROCESSSED_RECORDS = "processedRecords";

    public static final String ALL = "*";
    public static final String DOCTYPE = "DocType";
    public static final String BIB = "Bib";
    public static final String ITEM = "Item";
    public static final String HOLDINGS = "Holdings";
    public static final String HOLDINGS_ID = "HoldingsId";
    public static final String HOLDING_ID = "HoldingId";
    public static final String ITEM_ID = "ItemId";
    public static final String SEARCH = "search";
    public static final String PRIVATE = "Private";
    public static final String BIB_OWNING_INSTITUTION = "BibOwningInstitution";
    public static final String HOLDINGS_OWNING_INSTITUTION = "HoldingsOwningInstitution";
    public static final String ITEM_OWNING_INSTITUTION = "ItemOwningInstitution";
    public static final String OWNING_INSTITUTION = "OwningInstitution";
    public static final String COLLECTION_GROUP_DESIGNATION = "CollectionGroupDesignation";
    public static final String AVAILABILITY = "Availability_search";
    public static final String TITLE_SEARCH = "Title_search";
    public static final String AUTHOR_SEARCH = "Author_search";
    public static final String PUBLISHER = "Publisher";
    public static final String TITLE_STARTS_WITH= "TitleStartsWith";
    public static final String TITLE_SORT= "Title_sort";
    public static final String BARCODE = "Barcode";
    public static final String CALL_NUMBER = "CallNumber_search";
    public static final String NOTES = "Notes";
    public static final String LEADER_MATERIAL_TYPE = "LeaderMaterialType";
    public static final String MONOGRAPH = "Monograph";
    public static final String MONOGRAPHIC_SET = "MonographicSet";
    public static final String SERIAL = "Serial";
    public static final String OTHER = "Other";
    public static final String AND = "AND";
    public static final String OR = "OR";
    public static final String ALL_DIACRITICS = "all_diacritics";
    public static final String ALL_FIELDS = "_text_";
    public static final String IS_DELETED_BIB = "IsDeletedBib";
    public static final String IS_DELETED_HOLDINGS = "IsDeletedHoldings";
    public static final String IS_DELETED_ITEM = "IsDeletedItem";
    public static final String PUBLICATION_DATE = "PublicationDate";
    public static final String CGD_CHANAGE_LOG = "CGDChangeLog";
    public static final String CGD_CHANAGE_LOG_SHARED_TO_PRIVATE = "SharedToPrivate";
    public static final String CGD_CHANAGE_LOG_OPEN_TO_PRIVATE = "OpenToPrivate";

    public static final String USE_RESTRICTION = "UseRestriction_search";
    public static final String NO_RESTRICTIONS = "No Restrictions";
    public static final String IN_LIBRARY_USE = "In Library Use";
    public static final String SUPERVISED_USE = "Supervised Use";

    public static final String COLLECTION_GROUP_CODE = "CollectionGroupCode";
    public static final String STATUS = "Status";
    public static final String REASON_FOR_FAILURE = "ReasonForFailure";
    public static final String REASON_FOR_FAILURE_BIB = "ReasonForFailureBib";
    public static final String REASON_FOR_FAILURE_HOLDING = "ReasonForFailureHolding";
    public static final String REASON_FOR_FAILURE_ITEM = "ReasonForFailureItem";

    public static final String INCREMENTAL_DATE_FORMAT = "dd-MM-yyyy hh:mm";

    //Matching Algorithm Constants
    public static final String CSV_MATCHING_ALGO_REPORT_Q = "scsbactivemq:queue:csvMatchingAlgoReportQ";
    public static final String CSV_SUMMARY_ALGO_REPORT_Q = "scsbactivemq:queue:csvSummaryAlgoReportQ";
    public static final String CSV_SOLR_EXCEPTION_REPORT_Q = "scsbactivemq:queue:csvSolrExceptionReportQ";
    public static final String FTP_MATCHING_ALGO_REPORT_Q = "scsbactivemq:queue:ftpMatchingAlgoReportQ";
    public static final String FTP_SUMMARY_ALGO_REPORT_Q = "scsbactivemq:queue:ftpSummaryAlgoReportQ";
    public static final String FTP_SOLR_EXCEPTION_REPORT_Q = "scsbactivemq:queue:ftpSolrExceptionReportQ";
    public static final String REPORT_Q= "scsbactivemq:queue:reportQ";
    public static final String FS_SUBMIT_COLLECTION_REJECTION_REPORT_Q = "scsbactivemq:queue:fsSubmitCollectionRejectionReportQ";
    public static final String FTP_SUBMIT_COLLECTION_REJECTION_REPORT_Q = "scsbactivemq:queue:ftpSubmitCollectionRejectionReportQ";
    public static final String FS_SUBMIT_COLLECTION_EXCEPTION_REPORT_Q = "scsbactivemq:queue:fsSubmitCollectionExceptionReportQ";
    public static final String FTP_SUBMIT_COLLECTION_EXCEPTION_REPORT_Q = "scsbactivemq:queue:ftpSubmitCollectionExceptionReportQ";
    public static final String FTP_ONGOING_ACCESSON_REPORT_Q = "scsbactivemq:queue:ftpOngoingAccessionReportQ";
    public static final String FS_ONGOING_ACCESSION_REPORT_Q = "scsbactivemq:queue:fsOngoingAccessionReportQ";


    public static final String CSV_MATCHING_ALGO_REPORT_ROUTE_ID = "csvMatchingAlgoReportRoute";
    public static final String CSV_SUMMARY_ALGO_REPORT_ROUTE_ID = "csvSummaryAlgoReportRoute";
    public static final String CSV_SOLR_EXCEPTION_REPORT_ROUTE_ID = "csvSolrExceptionReportRoute";
    public static final String FTP_MATCHING_ALGO_REPORT_ROUTE_ID = "ftpMatchingAlgoReportRoute";
    public static final String FTP_SUMMARY_ALGO_REPORT_ROUTE_ID = "ftpSummaryAlgoReportRoute";
    public static final String FTP_SOLR_EXCEPTION_REPORT_ROUTE_ID = "ftpSolrExceptionReportRoute";
    public static final String REPORT_ROUTE_ID = "reportQRoute";
    public static final String FS_SUBMIT_COLLECTION_REJECTION_REPORT_ID = "fsSubmitCollectionRejectionReport";
    public static final String FTP_SUBMIT_COLLECTION_REJECTION_REPORT_ID = "ftpSubmitCollectionRejectionReport";
    public static final String FS_SUBMIT_COLLECTION_EXCEPTION_REPORT_ID = "fsSubmitCollectionExceptionReport";
    public static final String FTP_SUBMIT_COLLECTION_EXCEPTION_REPORT_ID = "ftpSubmitCollectionExceptionReport";

    public static final String MATCHING_ALGO_FULL_FILE_NAME = "Matching_Algo_Phase1";
    public static final String MATCHING_ALGO_OCLC_FILE_NAME = "Matching_Algo_OCLC";
    public static final String MATCHING_ALGO_ISBN_FILE_NAME = "Matching_Algo_ISBN";
    public static final String MATCHING_ALGO_ISSN_FILE_NAME = "Matching_Algo_ISSN";
    public static final String MATCHING_ALGO_LCCN_FILE_NAME = "Matching_Algo_LCCN";

    public static final String EXCEPTION_REPORT_FILE_NAME = "Exception_Report";
    public static final String EXCEPTION_REPORT_OCLC_FILE_NAME = "Exception_Report_OCLC";
    public static final String EXCEPTION_REPORT_ISBN_FILE_NAME = "Exception_Report_ISBN";
    public static final String EXCEPTION_REPORT_ISSN_FILE_NAME = "Exception_Report_ISSN";
    public static final String EXCEPTION_REPORT_LCCN_FILE_NAME = "Exception_Report_LCCN";

    public static final String SUMMARY_REPORT_FILE_NAME = "Summary_Report_Phase1";
    public static final String SUMMARY_REPORT_OCLC_FILE_NAME = "Summary_Report_OCLC";
    public static final String SUMMARY_REPORT_ISBN_FILE_NAME = "Summary_Report_ISBN";
    public static final String SUMMARY_REPORT_ISSN_FILE_NAME = "Summary_Report_ISSN";
    public static final String SUMMARY_REPORT_LCCN_FILE_NAME = "Summary_Report_LCCN";

    public static final String REPORT_FILE_NAME = "fileName";
    public static final String DATE_FORMAT_FOR_FILE_NAME = "ddMMMyyyy";

    public static final String MATCHING_BIB_ID = "BibId";
    public static final String MATCHING_TITLE = "Title";
    public static final String MATCHING_BARCODE = "Barcode";
    public static final String MATCHING_VOLUME_PART_YEAR = "VolumePartYear";
    public static final String MATCHING_INSTITUTION_ID = "InstitutionId";
    public static final String MATCHING_OCLC = "Oclc";
    public static final String MATCHING_ISBN = "Isbn";
    public static final String MATCHING_ISSN = "Issn";
    public static final String MATCHING_LCCN = "Lccn";
    public static final String MATCHING_USE_RESTRICTIONS = "UseRestrictions";
    public static final String MATCHING_SUMMARY_HOLDINGS = "SummaryHoldings";
    public static final String MATCHING_MATERIAL_TYPE = "MaterialType";
    public static final String INITIAL_MATCHING_OPERATION_TYPE = "InitialMatchingAlgorithm";
    public static final String ONGOING_MATCHING_OPERATION_TYPE = "OngoingMatchingAlgorithm";

    public static final String SUMMARY_NUM_BIBS_IN_TABLE = "CountOfBibsInTable";
    public static final String SUMMARY_NUM_ITEMS_IN_TABLE = "CountOfItemsInTable";
    public static final String SUMMARY_MATCHING_KEY_FIELD = "MatchingKeyField";
    public static final String SUMMARY_MATCHING_BIB_COUNT = "CountOfBibMatches";
    public static final String SUMMARY_NUM_ITEMS_AFFECTED = "CountOfItemAffected";

    public static final String MATCHING_LOCAL_BIB_ID = "LocalBibId";

    public static final String MATCH_POINT_FIELD_OCLC = "OCLCNumber";
    public static final String MATCH_POINT_FIELD_ISBN = "ISBN";
    public static final String MATCH_POINT_FIELD_ISSN = "ISSN";

    public static final String MATCH_POINT_FIELD_LCCN = "LCCN";
    public static final String ALL_INST = "ALL";

    public static final String OCLC_TAG = "035";
    public static final String ISBN_TAG = "020";
    public static final String ISSN_TAG = "022";
    public static final String LCCN_TAG = "010";

    //Report Types
    public static final String MATCHING_TYPE = "Matching";
    public static final String EXCEPTION_TYPE = "Exception";
    public static final String SUMMARY_TYPE = "Summary";

    //Transmission Types
    public static final String FILE_SYSTEM = "FileSystem";
    public static final String FTP = "FTP";


    public static final String SHARED_CGD = "Shared";
    public static final String OCLC_CRITERIA = "OCLC";

    public static final String ISBN_CRITERIA = "ISBN";
    public static final String ISSN_CRITERIA = "ISSN";
    public static final String LCCN_CRITERIA = "LCCN";

    public static final String OCLC_NUMBER = "OCLCNumber";

    public static final String BIB_COUNT = "bibCount";
    public static final String ITEM_COUNT = "itemCount";
    public static final String BIB_ITEM_COUNT = "bibItemCount";

    public static final String MATCHING_EXCEPTION_OCCURED = "MatchingExceptionOccurred";
    public static final String EXCEPTION_MSG = "ExceptionMessage";

    public static final String MATCHING_REPORT_ENTITY_MAP = "matchingReportEntityMap";
    public static final String EXCEPTION_REPORT_ENTITY_MAP = "exceptionReportEntityMap";

    public static final String ONGOING_MATCHING_ALGORITHM = "OngoingMatchingAlgorithm";

    //Error Message
    public static final String RECORD_NOT_AVAILABLE = "Database may be empty or Bib table does not contains this record";
    public static final String SERVER_ERROR_MSG = "Server is down for Maintenance Please Try again Later.";
    public static final String EMPTY_FACET_ERROR_MSG = "At least one Bib Facet Box and one Item Facet Box needs to be checked to get results.";
    public static final String ACCESS_RESTRICTED="User is not permitted to access this record";

    //Search Response Types
    public static final String SEARCH_SUCCESS_RESPONSE = "SuccessResponse";
    public static final String SEARCH_ERROR_RESPONSE = "ErrorResponse";


    public static final String SOLR_CORE = "solrCore";
    public static final String SOLR_QUEUE = "scsbactivemq:queue:solrQ";

    public static final String SOLR_INDEX_FAILURE_REPORT = "Solr_Index_Failure_Report";
    public static final String SOLR_INDEX_EXCEPTION = "SolrIndexException";
    public static final String OWNING_INST_BIB_ID = "OwningInstitutionBibId";
    public static final String BIB_ID = "BibId";
    public static final String NA = "NA";

    //Collection
    public static final String UPDATE_CGD = "Update CGD";
    public static final String DEACCESSION = "Deaccession";
    public static final String TEMPLATE = "template";
    public static final String COLLECTION = "collection";
    public static final String REQUEST = "request";
    public static final String GUEST = "guest";
    public static final String ITEM_BARCODES = "itemBarcodes";
    public static final String API_KEY = "api_key";
    public static final String RECAP = "recap";
    public static final String AVAILABLE = "Available";
    public static final String NOT_AVAILABLE = "Not Available";
    public static final String PERMANENT_WITHDRAWAL_DIRECT = "Permanent Withdrawal Direct (PWD)";
    public static final String PERMANENT_WITHDRAWAL_INDIRECT = "Permanent Withdrawal Indirect (PWI)";

    public static final String DEACCESSION_URL = "sharedCollection/deAccession";

    public static final String SUCCESS = "Success";
    public static final String FAILURE = "Failure";
    public static final String NO_RESULTS_FOUND = "No results found.";
    public static final String BARCODES_NOT_FOUND = "Barcode(s) not found";
    public static final String BARCODE_LIMIT_ERROR = "A maximum of only 10 items can be retrieved. Ignored barcode(s)";
    public static final String CGD_UPDATE_SUCCESSFUL = "The CGD has been successfully updated.";
    public static final String CGD_UPDATE_FAILED = "Updating CGD failed";
    public static final String DEACCESSION_SUCCESSFUL = "The item has been successfully deaccessioned.";
    public static final String DEACCESSION_FAILED = "Deaccessioning the item failed";
    public static final int BARCODE_LIMIT = 10;
    public static final String ACCESSION_SUCCESS = "One or more record got accessioned";
    public static final String ACCESSION_DUMMY_RECORD = "Dummy record created";

    //Request
    public static final String REQUEST_ID = "requestId";

    public static final String SEARCH_RESULT_ERROR_NO_RECORDS_FOUND="No search results found. Please refine search conditions.";
    public static final String SEARCH_RESULT_ERROR_INVALID_CHARACTERS="No search results found. Search conditions, has invalid characters (double quotes [\"],open parenthesis [(], backslash [\\], curly braces[{}] and caret [^). Please refine search conditions.";

    public static final String CUSTOMER_CODE_DOESNOT_EXIST = "Customer Code doesn't exist in SCSB database.";
    public static final String COLUMBIA = "CUL";
    public static final String PRINCETON = "PUL";
    public static final String NYPL = "NYPL";
    public static final String OWNING_INSTITUTION_BIB_ID = "OwningInstitutionBibId";
    public static final String TITLE = "Title";
    public static final String OWNING_INSTITUTION_HOLDINGS_ID = "OwningInstitutionHoldingsId";
    public static final String LOCAL_ITEM_ID = "LocalItemId";
    public static final String ITEM_BARCODE = "ItemBarcode";
    public static final String CUSTOMER_CODE = "CustomerCode";
    public static final String CREATE_DATE_ITEM = "CreateDateItem";
    public static final String LAST_UPDATED_DATE_ITEM = "LastUpdatedDateItem";
    public static final String ERROR_DESCRIPTION = "ErrorDescription";
    public static final String RESPONSE_DATE = "Date";
    public static final String ITEM_BARCDE_DOESNOT_EXIST = "Item Barcode doesn't exist in SCSB database.";

    public static final String REQUESTED_ITEM_DEACCESSIONED = "The requested item has already been deaccessioned.";
    public static final String DATE_OF_DEACCESSION = "DateOfDeAccession";
    public static final String DATE_OF_ACCESSION = "DateOfAccession";

    public static final String FS_DE_ACCESSION_SUMMARY_REPORT_Q = "scsbactivemq:queue:fsDeAccessionSummaryReportQ";
    public static final String FTP_DE_ACCESSION_SUMMARY_REPORT_Q = "scsbactivemq:queue:ftpDeAccessionSummaryReportQ";

    public static final String FS_DE_ACCESSION_SUMMARY_REPORT_ID = "fsDeAccessionSummaryReportQ";
    public static final String FTP_DE_ACCESSION_SUMMARY_REPORT_ID = "ftpDeAccessionSummaryReportQ";

    public static final String FS_ACCESSION_SUMMARY_REPORT_Q = "scsbactivemq:queue:fsAccessionSummaryReportQ";
    public static final String FTP_ACCESSION_SUMMARY_REPORT_Q = "scsbactivemq:queue:ftpAccessionSummaryReportQ";

    public static final String FS_ACCESSION_SUMMARY_REPORT_ID = "fsAccessionSummaryReportQ";
    public static final String FTP_ACCESSION_SUMMARY_REPORT_ID = "ftpAccessionSummaryReportQ";
    public static final String FS_ONGOING_ACCESSION_REPORT_ID = "fsOngoingAccessionReportQ";
    public static final String FTP_ONGOING_ACCESSION_REPORT_ID = "ftpOngingAccessionReportQ";
    public static final String DEACCESION_DATE_FORMAT_FOR_FILE_NAME = "ddMMMyyyyHHmmss";
    public static final String ACCESSION_SUMMARY_REPORT = "Accession_Summary_Report";
    public static final String DEACCESSION_SUMMARY_REPORT = "DeAccession_Summary_Report";
    public static final String DEACCESSION_REPORT = "DeAccession_Report";
    public static final String ACCESSION_REPORT = "Accession_Report";
    public static final String SUCCESS_BIB_COUNT = "successBibCount";
    public static final String FAILED_BIB_COUNT = "failedBibCount";
    public static final String SUCCESS_ITEM_COUNT = "successItemCount";
    public static final String FAILED_ITEM_COUNT = "failedItemCount";
    public static final String EXIST_BIB_COUNT = "exitsBibCount";
    public static final String REASON_FOR_BIB_FAILURE = "reasonForFailureBib";
    public static final String REASON_FOR_ITEM_FAILURE = "reasonForFailureItem";
    public static final String BIB_SUCCESS_COUNT = "SuccessBibCount";
    public static final String ITEM_SUCCESS_COUNT = "SuccessItemCount";
    public static final String BIB_FAILURE_COUNT = "FailedBibCount";
    public static final String ITEM_FAILURE_COUNT = "FailedItemCount";
    public static final String NUMBER_OF_BIB_MATCHES = "NoOfBibMatches";
    public static final String FAILURE_BIB_REASON = "ReasonForFailureBib";
    public static final String FAILURE_ITEM_REASON = "ReasonForFailureItem";
    public static final String ITEMBARCODE = "itemBarcode";

    public static final String SUBMIT_COLLECTION_REPORT = "Submit_Collection_Report";
    public static final String SUBMIT_COLLECTION_REJECTION_REPORT = "Submit_Collection_Rejection_Report";
    public static final String SUBMIT_COLLECTION_EXCEPTION_REPORT = "Submit_Collection_Exception_Report";
    public static final String SUBMIT_COLLECTION_ITEM_BARCODE= "ItemBarcode";
    public static final String SUBMIT_COLLECTION_CUSTOMER_CODE= "CustomerCode";
    public static final String ITEM_BARCODE_NOT_FOUND_MSG = "Item Barcode not found";
    public static final String ITEM_BARCODE_ALREADY_ACCESSIONED_MSG = "Unavailable barcode from partner is already accessioned";
    public static final String ACCESSION = "accession";
    public static final String DUMMYCALLNUMBER = "dummycallnumber";
    public static final String COMPLETE_STATUS = "Complete";
    public static final String INCOMPLETE_STATUS = "Incomplete";
    public static final String BIBLIOGRAPHICENTITY = "bibliographicEntity";
    public static final String REPORTENTITIES = "reportEntities";
    public static final String DUMMY_CALL_NUMBER = "dummycallnumbertype";

    //solr
    public static final String DATE_FORMAT_YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";
    public static final String UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String UTC = "UTC";
    public static final String SOLR_DATE_RANGE_TO_NOW = " TO NOW";

    //Reports
    public static final String SIMPLE_DATE_FORMAT_REPORTS = "MM/dd/yyyy";
    public static final String REPORTS = "reports";
    public static final String REPORTS_REQUEST = "request";
    public static final String REPORTS_IL_BD = "IL_BD";
    public static final String REPORTS_PARTNERS = "Partners";
    public static final String REPORTS_REQUEST_TYPE = "RequestType";
    public static final String REPORTS_ACCESSION_DEACCESSION = "Accession/Deaccesion";
    public static final String REPORTS_DEACCESSION = "Deaccession";
    public static final String REPORTS_RETRIEVAL = "Retrieval";
    public static final String REPORTS_RECALL = "Recall";
    public static final String REPORTS_OPEN = "Open";
    public static final String REPORTS_SHARED = "Shared";
    public static final String REPORTS_PRIVATE = "Private";
    public static final String RETRIEVAL = "RETRIEVAL";
    public static final String RECALL = "RECALL";
    public static final String EDD = "EDD";
    public static final String BORROW_DIRECT = "BORROW DIRECT";


    public static final String  BIBITEM_LASTUPDATED_DATE = "BibItemLastUpdatedDate";
    public static final String  ITEM_LASTUPDATED_DATE = "ItemLastUpdatedDate";

    public static final String EMAIL_Q = "scsbactivemq:queue:solrClientEmailQ";
    public static final String EMAIL_ROUTE_ID = "solrClientEmailQ";

    public static final String CGD_UPDATE_ITEM_BARCODE = "itemBarcode";
    public static final String OWNING_INST = "owningInstitution";
    public static final String OLD_CGD = "oldCollectionGroupDesignation";
    public static final String NEW_CGD = "newCollectionGroupDesignation";
    public static final String CGD_CHANGE_NOTES = "cgdChangeNotes";
    public static final String TO = "To";
    public static final String ITEM_CATALOGING_STATUS = "ItemCatalogingStatus";
    public static final String BIB_CATALOGING_STATUS = "BibCatalogingStatus";
    public static final String MESSAGE = "Message";
    public static final String ONGOING_ACCESSION_REPORT = "Ongoing_Accession_Report";
    public static final String ERROR = "error->";
    public static final String EXCEPTION = "exception->";
    public static final String ITEM_BARCODE_NOT_FOUND = "Item Barcode not found";
    public static final String SERVICE_UNAVAILABLE =" Service is Unavailable.";
    public static final String VERIFY_ONGOING_ACCESSION_REPORT_MSG = "Please verify ongoing accession report";
    public static final String DUMMY_BIB_CONTENT_XML = "dummybibcontent.xml";
    public static final String DUMMY_HOLDING_CONTENT_XML = "dummyholdingcontent.xml";
    public static final String ONGOING_ACCESSION_LIMIT_EXCEED_MESSAGE = "Input limit exceeded, maximum allowed input limit is ";

}