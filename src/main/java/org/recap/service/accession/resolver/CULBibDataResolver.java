package org.recap.service.accession.resolver;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.Record;
import org.recap.RecapConstants;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.marc.BibMarcRecord;
import org.recap.model.marc.HoldingsMarcRecord;
import org.recap.model.marc.ItemMarcRecord;
import org.recap.service.accession.BulkAccessionService;
import org.recap.service.partnerservice.ColumbiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sheiks on 26/05/17.
 */
@Service
public class CULBibDataResolver extends BibDataResolver {

    @Autowired
    private ColumbiaService columbiaService;

    public CULBibDataResolver(BulkAccessionService bulkAccessionService) {
        super(bulkAccessionService);
    }

    @Override
    public boolean isInterested(String institution) {
        return RecapConstants.COLUMBIA.equals(institution);
    }

    @Override
    public String getBibData(String itemBarcode, String customerCode) {
        return columbiaService.getBibData(itemBarcode);
    }

    @Override
    public Object unmarshal(String bibDataResponse) {
        return marcRecordConvert(bibDataResponse);
    }


    @Override
    public ItemEntity getItemEntityFromRecord(Object object) {
        return getItemEntityFormMarcRecord((List<Record>) object);
    }

    @Override
    public String processXml(Set<AccessionResponse> accessionResponses, Object object, List<Map<String, String>> responseMapList, String owningInstitution, List<ReportDataEntity> reportDataEntityList, AccessionRequest accessionRequest) throws Exception {
        return bulkAccessionService.processAccessionForMarcXml(accessionResponses, object,
                responseMapList, owningInstitution, reportDataEntityList, accessionRequest);
    }


}
