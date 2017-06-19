package org.recap.service.accession;

import org.marc4j.marc.Record;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jaxb.Holding;
import org.recap.model.jaxb.Holdings;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.HoldingsDetailsRepository;
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
    private HoldingsDetailsRepository holdingsDetailsRepository;

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

    public boolean validateItemAndHolding(BibliographicEntity bibliographicEntity, boolean isValidBoundWithRecord, boolean isFirstRecord, StringBuilder errorMessage){
        boolean isValid = true;
        isValid &= validateItem(bibliographicEntity,isValidBoundWithRecord,isFirstRecord,errorMessage);
        isValid &= validateHolding(bibliographicEntity,isValidBoundWithRecord,isFirstRecord,errorMessage);
        return isValid;

    }

    public boolean validateItem(BibliographicEntity bibliographicEntity,boolean isValidBoundWithRecord,boolean isFirstRecord, StringBuilder errorMessage) {
        boolean isValid = true;
        List<ItemEntity> incomingItemEntityList = bibliographicEntity.getItemEntities();
        for (ItemEntity incomingItemEntity : incomingItemEntityList) {
            ItemEntity existingItemEntity = itemDetailsRepository.findByOwningInstitutionItemIdAndOwningInstitutionId(incomingItemEntity.getOwningInstitutionItemId(), incomingItemEntity.getOwningInstitutionId());
            if (existingItemEntity != null && (!isValidBoundWithRecord || (isValidBoundWithRecord && isFirstRecord))) {
                errorMessage.append("Failed - The incoming owning institution itemid " + incomingItemEntity.getOwningInstitutionItemId() + " of incoming barcode "
                        + incomingItemEntity.getBarcode() + " is already available in scsb"
                        + " and linked with barcode " + existingItemEntity.getBarcode() + " and its owning institution bib id(s) are "
                        + getOwningInstitutionBibIds(existingItemEntity.getBibliographicEntities())+". ");//Getting bib ids if it is a bound with items
                return false;
            }
        }
        return isValid;
    }

    public boolean validateHolding(BibliographicEntity bibliographicEntity,boolean isValidBoundWithRecord,boolean isFirstRecord, StringBuilder errorMessage){
        boolean isValid = true;
        List<HoldingsEntity> holdingsEntityList = bibliographicEntity.getHoldingsEntities();
        String itemBarcode = bibliographicEntity.getItemEntities().get(0).getBarcode();
        for(HoldingsEntity holdingsEntity:holdingsEntityList){
            HoldingsEntity existingHoldingEntity = holdingsDetailsRepository.findByOwningInstitutionHoldingsIdAndOwningInstitutionId(holdingsEntity.getOwningInstitutionHoldingsId(),holdingsEntity.getOwningInstitutionId());
            if(existingHoldingEntity != null && (!isValidBoundWithRecord || (isValidBoundWithRecord && isFirstRecord))){
                List<BibliographicEntity> existingBibliographicEntityList = existingHoldingEntity.getBibliographicEntities();
                if(existingBibliographicEntityList.size()==1 && !existingBibliographicEntityList.get(0).getOwningInstitutionBibId().equals(bibliographicEntity.getOwningInstitutionBibId())){
                    errorMessage.append("Failed - The incoming holding id "+ holdingsEntity.getOwningInstitutionHoldingsId()+" of the incoming barcode "+itemBarcode+" is already linked with another bib, " +
                            "owning institution bib id "+existingBibliographicEntityList.get(0).getOwningInstitutionBibId());
                    return false;
                } else if(existingBibliographicEntityList.size()>1){
                    errorMessage.append("Failed - The incoming holding id "+ holdingsEntity.getOwningInstitutionHoldingsId()+" of the incoming barcode "+itemBarcode+" is already linked with another bibs, " +
                    "owning institution bib ids "+getOwningInstitutionBibIds(existingBibliographicEntityList));
                }
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
