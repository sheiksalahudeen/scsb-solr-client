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
    public static final String HOLDINGS_ID = "HoldingsId";
    public static final String ITEM_ID = "ItemId";
    public static final String OWNING_INSTITUTION = "OwningInstitution";
    public static final String COLLECTION_GROUP_DESIGNATION = "CollectionGroupDesignation";
    public static final String AVAILABILITY = "Availability";
    public static final String TITLE = "Title_search";
    public static final String TITLE_STARTS_WITH= "TitleStartsWith";
    public static final String BARCODE = "Barcode";
    public static final String CALL_NUMBER = "CallNumber";
    public static final String NOTES = "Notes";
    public static final String LEADER_MATERIAL_TYPE = "LeaderMaterialType";
    public static final String MONOGRAPH = "Monograph";
    public static final String SERIAL = "Serial";
    public static final String OTHER = "Other";

    public static final String INCREMENTAL_DATE_FORMAT = "dd-MM-yyyy hh:mm";

    //Matching Algorithm Constants
    public static final String CSV_MATCHING_ALGO_REPORT_Q = "seda:csvMatchingAlgoReportQ";
    public static final String CSV_SUMMARY_ALGO_REPORT_Q = "seda:csvSummaryAlgoReportQ";
    public static final String FTP_MATCHING_ALGO_REPORT_Q = "seda:ftpMatchingAlgoReportQ";
    public static final String FTP_SUMMARY_ALGO_REPORT_Q = "seda:ftpSummaryAlgoReportQ";

    public static final String CSV_MATCHING_ALGO_REPORT_ROUTE_ID = "csvMatchingAlgoReportRoute";
    public static final String CSV_SUMMARY_ALGO_REPORT_ROUTE_ID = "csvSummaryAlgoReportRoute";
    public static final String FTP_MATCHING_ALGO_REPORT_ROUTE_ID = "ftpMatchingAlgoReportRoute";
    public static final String FTP_SUMMARY_ALGO_REPORT_ROUTE_ID = "ftpSummaryAlgoReportRoute";

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
    public static final String MATCHING_INSTITUTION_ID = "InstitutionId";
    public static final String MATCHING_OCLC = "Oclc";
    public static final String MATCHING_ISBN = "Isbn";
    public static final String MATCHING_ISSN = "Issn";
    public static final String MATCHING_LCCN = "Lccn";
    public static final String MATCHING_USE_RESTRICTIONS = "UseRestrictions";
    public static final String MATCHING_SUMMARY_HOLDINGS = "SummaryHoldings";

    public static final String SUMMARY_NUM_BIBS_IN_TABLE = "NumberOfBibsInTable";
    public static final String SUMMARY_NUM_ITEMS_IN_TABLE = "NumberOfItemsInTable";
    public static final String SUMMARY_MATCHING_KEY_FIELD = "MatchingKeyField";
    public static final String SUMMARY_MATCHING_BIB_COUNT = "CountOfBibMatches";
    public static final String SUMMARY_NUM_ITEMS_AFFECTED = "NumberOfItemsAffected";

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

    public static final String BIB_COUNT = "bibCount";
    public static final String ITEM_COUNT = "itemCount";

    public static final String MATCHING_EXCEPTION_OCCURED = "MatchingExceptionOccurred";
    public static final String EXCEPTION_MSG = "ExceptionMessage";

    public static final String MATCHING_REPORT_ENTITY_MAP = "matchingReportEntityMap";
    public static final String EXCEPTION_REPORT_ENTITY_MAP = "exceptionReportEntityMap";

    //Error Message
    public static final String RECORD_NOT_AVAILABLE = "Database may be empty or Bib table does not contains this record";
}
