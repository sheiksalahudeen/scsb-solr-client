package org.recap.service.accession.resolver;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.Record;
import org.recap.RecapConstants;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.jaxb.*;
import org.recap.model.jaxb.marc.BibRecords;
import org.recap.model.jaxb.marc.CollectionType;
import org.recap.model.jaxb.marc.ContentType;
import org.recap.model.jaxb.marc.RecordType;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.marc.BibMarcRecord;
import org.recap.service.accession.BulkAccessionService;
import org.recap.service.partnerservice.NYPLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sheiks on 26/05/17.
 */
@Service
public class NYPLBibDataResolver extends BibDataResolver {

    @Autowired
    private NYPLService nyplService;

    public NYPLBibDataResolver(BulkAccessionService bulkAccessionService) {
        super(bulkAccessionService);
    }

    @Override
    public boolean isInterested(String institution) {
        return RecapConstants.NYPL.equals(institution);
    }

    @Override
    public String getBibData(String itemBarcode, String customerCode) {
        return nyplService.getBibData(itemBarcode, customerCode);
    }

    @Override
    public Object unmarshal(String bibDataResponse) {
        BibRecords bibRecords = null;
        try {
            bibRecords = (BibRecords) JAXBHandler.getInstance().unmarshal(bibDataResponse, BibRecords.class);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return bibRecords;
    }

    @Override
    public ItemEntity getItemEntityFromRecord(Object object) {
        BibRecords bibRecords = (BibRecords) object;
        List<BibRecord> bibRecordList = bibRecords.getBibRecordList();
        if(CollectionUtils.isNotEmpty(bibRecordList)) {
            String owningInstitutionItemId = getOwningInstitutionItemIdFromBibRecord(bibRecordList.get(0));
            if(StringUtils.isNotBlank(owningInstitutionItemId)) {
                return itemDetailsRepository.findByOwningInstitutionItemId(owningInstitutionItemId);
            }
        }
        return null;
    }

    private String getOwningInstitutionItemIdFromBibRecord(BibRecord bibRecord) {
        List<Holdings> holdings = bibRecord.getHoldings();
        if(CollectionUtils.isNotEmpty(holdings)) {
            for (Iterator<Holdings> iterator = holdings.iterator(); iterator.hasNext(); ) {
                Holdings holdingsRecord =  iterator.next();
                List<Holding> holdingList = holdingsRecord.getHolding();
                if(CollectionUtils.isNotEmpty(holdingList)) {
                    for (Iterator<Holding> holdingIterator = holdingList.iterator(); holdingIterator.hasNext(); ) {
                        Holding holding = holdingIterator.next();
                        List<Items> items = holding.getItems();
                        if(CollectionUtils.isNotEmpty(items)) {
                            for (Iterator<Items> itemsIterator = items.iterator(); itemsIterator.hasNext(); ) {
                                Items item = itemsIterator.next();

                                ContentType itemContent = item.getContent();
                                CollectionType itemContentCollection = itemContent.getCollection();

                                List<RecordType> itemRecordTypes = itemContentCollection.getRecord();

                                if(CollectionUtils.isNotEmpty(itemRecordTypes)) {
                                    for (Iterator<RecordType> recordTypeIterator = itemRecordTypes.iterator(); recordTypeIterator.hasNext(); ) {
                                        RecordType recordType = recordTypeIterator.next();
                                        return marcUtil.getDataFieldValueForRecordType(recordType,
                                                "876", null, null, "a");

                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String processXml(Set<AccessionResponse> accessionResponses, Object object, List<Map<String, String>> responseMapList, String owningInstitution, List<ReportDataEntity> reportDataEntityList, AccessionRequest accessionRequest) throws Exception {
        return bulkAccessionService.processAccessionForSCSBXml(accessionResponses, object,
                responseMapList, owningInstitution, reportDataEntityList, accessionRequest);
    }
}
