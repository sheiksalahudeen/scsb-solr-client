package org.recap.service.accession.resolver;

import org.recap.RecapConstants;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.service.accession.BulkAccessionService;
import org.recap.service.partnerservice.NYPLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public String processXml(Set<AccessionResponse> accessionResponses, String bibDataResponse, List<Map<String, String>> responseMapList, String owningInstitution, List<ReportDataEntity> reportDataEntityList, AccessionRequest accessionRequest) throws Exception {
        return bulkAccessionService.processAccessionForSCSBXml(accessionResponses, bibDataResponse,
                responseMapList, owningInstitution, reportDataEntityList, accessionRequest);
    }
}
