package org.recap.service.accession;

import org.marc4j.marc.Record;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jaxb.Holding;
import org.recap.model.jaxb.Holdings;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.util.MarcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by premkb on 2/6/17.
 */
@Service
public class AccessionValidationService {

    @Autowired
    private MarcUtil marcUtil;

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    public boolean validateBoundWithMarcRecordFromIls(List<Record> records){
        List<String> holdingIdList = new ArrayList<>();
        for(Record record : records){
            String holdingId = marcUtil.getDataFieldValue(record,"876","","","0");
            if(holdingIdList.isEmpty()){
                holdingIdList.add(holdingId);
            } else {
                if(!holdingIdList.contains(holdingId)){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean validateBoundWithScsbRecordFromIls(List<BibRecord> bibRecordList){
        List<String> holdingIdList = new ArrayList<>();
        for(BibRecord bibRecord : bibRecordList){
            List<Holdings> holdings = bibRecord.getHoldings();
            for(Holdings holdings1 : holdings) {
                for (Holding holding : holdings1.getHolding()) {
                    String owningInstitutionHoldingId = holding.getOwningInstitutionHoldingsId();
                    if(holdingIdList.isEmpty()){
                        holdingIdList.add(owningInstitutionHoldingId);
                    } else {
                        if(!holdingIdList.contains(owningInstitutionHoldingId)){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean validateItemRecord(BibliographicEntity bibliographicEntity,StringBuilder errorMessage) {
        boolean isValid = true;
        List<ItemEntity> incomingItemEntityList = bibliographicEntity.getItemEntities();
        for (ItemEntity incomingItemEntity : incomingItemEntityList) {
            ItemEntity existingItemEntity = itemDetailsRepository.findByOwningInstitutionItemIdAndOwningInstitutionId(incomingItemEntity.getOwningInstitutionItemId(), incomingItemEntity.getOwningInstitutionId());
            if (existingItemEntity != null) {
                errorMessage.append("Failed - The incoming owning institution itemid " + incomingItemEntity.getOwningInstitutionItemId() + " of incoming barcode "
                        + incomingItemEntity.getBarcode() + " is already available in scsb"
                        + " and linked with barcode " + existingItemEntity.getBarcode() + " and its owning institution bib id(s) are "
                        + getOwningInstitutionBibIds(existingItemEntity.getBibliographicEntities()));
                return false;
            }
        }
        return isValid;
    }

    private StringBuilder getOwningInstitutionBibIds(List<BibliographicEntity> bibliographicEntityList){
        StringBuilder bibIdsStringBuilder = new StringBuilder();
        for(BibliographicEntity bibliographicEntity:bibliographicEntityList){
            if (bibIdsStringBuilder.length()>0) {
                bibIdsStringBuilder.append(", ").append(bibliographicEntity.getOwningInstitutionBibId());
            } else {
                bibIdsStringBuilder.append(bibliographicEntity.getOwningInstitutionBibId());
            }
        }
        return bibIdsStringBuilder;
    }

}
