package org.recap.service.accession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.marc4j.marc.Record;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.CustomerCodeEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.CustomerCodeDetailsRepository;
import org.recap.converter.MarcToBibEntityConverter;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.util.MarcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chenchulakshmig on 20/10/16.
 */
@Service
public class AccessionService {

    Logger log = Logger.getLogger(AccessionService.class);

    @Value("${ils.princeton.bibdata}")
    String ilsprincetonBibData;

    @Autowired
    MarcToBibEntityConverter marcToBibEntityConverter;

    @Autowired
    CustomerCodeDetailsRepository customerCodeDetailsRepository;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    MarcUtil marcUtil;

    @Autowired
    SolrIndexService solrIndexService;

    @PersistenceContext
    private EntityManager entityManager;

    public String getOwningInstitution(String customerCode) {
        String owningInstitution = null;
        try {
            CustomerCodeEntity customerCodeEntity = customerCodeDetailsRepository.findByCustomerCode(customerCode);
            if (null != customerCodeEntity) {
                owningInstitution = customerCodeEntity.getInstitutionEntity().getInstitutionCode();
            }
        } catch (Exception e) {
            log.error("Exception " + e);
        }
        return owningInstitution;
    }

    @Transactional
    public String processRequest(String itemBarcode, String owningInstitution) {
        String response = null;
        RestTemplate restTemplate = new RestTemplate();

        String ilsBibDataURL = getILSBibDataURL(owningInstitution);
        String bibDataResponse = null;
        if (StringUtils.isNotBlank(ilsBibDataURL)) {
            try {
                bibDataResponse = restTemplate.getForObject(ilsBibDataURL + itemBarcode, String.class);
            } catch (HttpClientErrorException ex) {
                response = "Item Barcode not found";
                return response;
            } catch (Exception e) {
                response = ilsBibDataURL + "Service is Unavailable.";
                return response;
            }
        }
        List<Record> records = new ArrayList<>();
        if (StringUtils.isNotBlank(bibDataResponse)) {
            records = marcUtil.readMarcXml(bibDataResponse);
        }
        if (CollectionUtils.isNotEmpty(records)) {
            try {
                for (Record record : records) {
                    Map responseMap = marcToBibEntityConverter.convert(record, owningInstitution);
                    BibliographicEntity bibliographicEntity = (BibliographicEntity) responseMap.get("bibliographicEntity");
                    List<ReportEntity> reportEntityList = (List<ReportEntity>) responseMap.get("reportEntities");
                    if (CollectionUtils.isNotEmpty(reportEntityList)) {
                        reportDetailRepository.save(reportEntityList);
                    }
                    if (bibliographicEntity != null) {
                        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
                        entityManager.refresh(savedBibliographicEntity);
                        if (null != savedBibliographicEntity) {
                            solrIndexService.indexByBibliographicId(savedBibliographicEntity.getBibliographicId());
                            response = RecapConstants.SUCCESS;
                        }
                    }
                }
            } catch (Exception e) {
                response = e.getMessage();
                log.error(e.getMessage());
            }
        }
        return response;
    }

    private String getILSBibDataURL(String owningInstitution) {
        if (owningInstitution.equalsIgnoreCase(RecapConstants.PRINCETON)) {
            return ilsprincetonBibData;
        }
        return null;
    }
}
