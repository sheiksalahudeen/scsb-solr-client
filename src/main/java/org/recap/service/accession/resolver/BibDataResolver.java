package org.recap.service.accession.resolver;

import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.service.accession.BulkAccessionService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sheiks on 26/05/17.
 */
public abstract class BibDataResolver {

    protected BulkAccessionService bulkAccessionService;

    public BibDataResolver(BulkAccessionService bulkAccessionService) {
        this.bulkAccessionService = bulkAccessionService;
    }

    public abstract boolean isInterested(String institution);

    public abstract String getBibData(String itemBarcode, String customerCode);

    public abstract String processXml(Set<AccessionResponse> accessionResponses, String bibDataResponse,
                                              List<Map<String, String>> responseMapList, String owningInstitution,
                                              List<ReportDataEntity> reportDataEntityList, AccessionRequest accessionRequest) throws Exception ;

}
