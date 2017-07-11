package org.recap.service.accession.resolver;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.Record;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.marc.BibMarcRecord;
import org.recap.model.marc.HoldingsMarcRecord;
import org.recap.model.marc.ItemMarcRecord;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.service.accession.BulkAccessionService;
import org.recap.util.MarcUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by sheiks on 26/05/17.
 */
public abstract class BibDataResolver {

    protected BulkAccessionService bulkAccessionService;

    @Autowired
    public MarcUtil marcUtil;

    @Autowired
    public ItemDetailsRepository itemDetailsRepository;

    public BibDataResolver(BulkAccessionService bulkAccessionService) {
        this.bulkAccessionService = bulkAccessionService;
    }

    public abstract boolean isInterested(String institution);

    public abstract String getBibData(String itemBarcode, String customerCode);

    public abstract Object unmarshal(String unmarshal);

    public abstract ItemEntity getItemEntityFromRecord(Object object);

    public abstract String processXml(Set<AccessionResponse> accessionResponses, Object object,
                                              List<Map<String, String>> responseMapList, String owningInstitution,
                                              List<ReportDataEntity> reportDataEntityList, AccessionRequest accessionRequest) throws Exception ;

    public Object marcRecordConvert(String bibDataResponse) {
        List<Record> records = new ArrayList<>();
        if (StringUtils.isNotBlank(bibDataResponse)) {
            records = marcUtil.readMarcXml(bibDataResponse);
        }
        return records;
    }

    public ItemEntity getItemEntityFormMarcRecord(List<Record> object) {
        List<Record> records  = object;
        String owningInstitutionItemIdFromMarcRecord = getOwningInstitutionItemIdFromMarcRecord(records);
        if(StringUtils.isNotBlank(owningInstitutionItemIdFromMarcRecord)) {
            return itemDetailsRepository.findByOwningInstitutionItemId(owningInstitutionItemIdFromMarcRecord);
        }

        return null;
    }

    private String getOwningInstitutionItemIdFromMarcRecord(List<Record> records) {
        if(CollectionUtils.isNotEmpty(records)) {
            return marcUtil.getDataFieldValue(records.get(0), "876", 'a');
        }
        return null;
    }

    public boolean isAccessionProcess(ItemEntity itemEntity, String owningInstitution) {
        if(null != itemEntity) {
            InstitutionEntity institutionEntity = itemEntity.getInstitutionEntity();
            if(StringUtils.equals(owningInstitution, institutionEntity.getInstitutionCode())) {
                return false;
            }
        }
        return true;
    }

}
