package org.recap.matchingAlgorithm;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.ItemChangeLogEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.CollectionGroupDetailsRepository;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by angelind on 6/1/17.
 */
public class MatchingAlgorithmCGDProcessor {

    Logger logger = LoggerFactory.getLogger(MatchingAlgorithmCGDProcessor.class);

    private BibliographicDetailsRepository bibliographicDetailsRepository;
    private ProducerTemplate producerTemplate;
    private Map collectionGroupMap;
    private Map institutionMap;
    private ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;
    private String matchingType;
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;
    private ItemDetailsRepository itemDetailsRepository;

    public MatchingAlgorithmCGDProcessor(BibliographicDetailsRepository bibliographicDetailsRepository, ProducerTemplate producerTemplate, Map collectionGroupMap, Map institutionMap,
                                         ItemChangeLogDetailsRepository itemChangeLogDetailsRepository, String matchingType, CollectionGroupDetailsRepository collectionGroupDetailsRepository,
                                         ItemDetailsRepository itemDetailsRepository) {
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
        this.producerTemplate = producerTemplate;
        this.collectionGroupMap = collectionGroupMap;
        this.institutionMap = institutionMap;
        this.itemChangeLogDetailsRepository = itemChangeLogDetailsRepository;
        this.matchingType = matchingType;
        this.collectionGroupDetailsRepository = collectionGroupDetailsRepository;
        this.itemDetailsRepository = itemDetailsRepository;
    }

    public void updateCGDProcess(Map<Integer, Map<Integer, List<ItemEntity>>> useRestrictionMap, Map<Integer, ItemEntity> itemEntityMap) {
        if(useRestrictionMap.size() > 1) {
            // Multiple Use Restriction
            if(useRestrictionMap.containsKey(0)) {
                // Institutions which has no restriction
                Map<Integer, List<ItemEntity>> institutionMap = useRestrictionMap.get(0);
                findItemsToBeUpdatedAsOpen(itemEntityMap, institutionMap);
            } else if(useRestrictionMap.containsKey(1)) {
                // Institutions which has least restriction
                Map<Integer, List<ItemEntity>> institutionMap = useRestrictionMap.get(1);
                findItemsToBeUpdatedAsOpen(itemEntityMap, institutionMap);
            }
        } else {
            //Same UseRestriction
            for (Iterator<Map<Integer, List<ItemEntity>>> iterator = useRestrictionMap.values().iterator(); iterator.hasNext(); ) {
                Map<Integer, List<ItemEntity>> institutionMap = iterator.next();
                findItemToBeSharedBasedOnCounter(itemEntityMap, institutionMap);
            }
        }
        List<ItemEntity> itemEntitiesToUpdate = new ArrayList<>();
        List<ItemChangeLogEntity> itemChangeLogEntities = new ArrayList<>();
        CollectionGroupEntity collectionGroupEntity = null;
        Integer collectionGroupId = (Integer) collectionGroupMap.get(RecapConstants.REPORTS_OPEN);
        if(matchingType.equalsIgnoreCase(RecapConstants.ONGOING_MATCHING_ALGORITHM)) {
            collectionGroupEntity = collectionGroupDetailsRepository.findOne(collectionGroupId);
        }
        for (Iterator<ItemEntity> iterator = itemEntityMap.values().iterator(); iterator.hasNext(); ) {
            // Items which needs to be changed to open status
            ItemEntity itemEntity = iterator.next();
            MatchingCounter.updateCounter(itemEntity.getOwningInstitutionId(), true);
            Integer oldCgd = itemEntity.getCollectionGroupId();
            itemEntity.setLastUpdatedDate(new Date());
            itemEntity.setLastUpdatedBy(RecapConstants.GUEST);
            itemEntity.setCollectionGroupId(collectionGroupId);
            if(matchingType.equalsIgnoreCase(RecapConstants.ONGOING_MATCHING_ALGORITHM)) {
                itemEntity.setCollectionGroupEntity(collectionGroupEntity);
            }
            itemEntitiesToUpdate.add(itemEntity);
            itemChangeLogEntities.add(getItemChangeLogEntity(oldCgd, itemEntity));
        }
        if(CollectionUtils.isNotEmpty(itemEntitiesToUpdate) && CollectionUtils.isNotEmpty(itemChangeLogEntities)) {
            if(matchingType.equalsIgnoreCase(RecapConstants.ONGOING_MATCHING_ALGORITHM)) {
                itemDetailsRepository.save(itemEntitiesToUpdate);
            } else {
                producerTemplate.sendBody("scsbactivemq:queue:updateItemsQ", itemEntitiesToUpdate);
            }
            itemChangeLogDetailsRepository.save(itemChangeLogEntities);
        }
    }

    private ItemChangeLogEntity getItemChangeLogEntity(Integer oldCgd, ItemEntity itemEntity) {
        ItemChangeLogEntity itemChangeLogEntity = new ItemChangeLogEntity();
        itemChangeLogEntity.setOperationType(matchingType);
        itemChangeLogEntity.setUpdatedBy(RecapConstants.GUEST);
        itemChangeLogEntity.setUpdatedDate(new Date());
        itemChangeLogEntity.setRecordId(itemEntity.getItemId());
        itemChangeLogEntity.setNotes(oldCgd + " - " + itemEntity.getCollectionGroupId());
        return itemChangeLogEntity;
    }

    public boolean checkForMonographAndPopulateValues(Set<String> materialTypeSet, Map<Integer, Map<Integer, List<ItemEntity>>> useRestrictionMap, Map<Integer, ItemEntity> itemEntityMap, List<Integer> bibIdList) {
        boolean isMonograph = true;
        List<BibliographicEntity> bibliographicEntities = bibliographicDetailsRepository.findByBibliographicIdIn(bibIdList);
        for(BibliographicEntity bibliographicEntity : bibliographicEntities) {
            List<ItemEntity> itemEntities = bibliographicEntity.getItemEntities();
            //Check for Monograph - (Single Bib & Single Item)
            if(itemEntities != null && itemEntities.size() == 1) {
                ItemEntity itemEntity = itemEntities.get(0);
                if(itemEntity.getCollectionGroupId().equals(collectionGroupMap.get(RecapConstants.SHARED_CGD))) {
                    populateValues(materialTypeSet, useRestrictionMap, itemEntityMap, itemEntity);
                } else {
                    isMonograph = false;
                    materialTypeSet.add(RecapConstants.MONOGRAPH);
                }
            } else {
                if(bibliographicEntity.getOwningInstitutionId().equals(institutionMap.get("NYPL"))) {
                    //NYPL
                    boolean isMultipleCopy = false;
                    for(ItemEntity itemEntity : itemEntities) {
                        if(itemEntity.getCollectionGroupId().equals(collectionGroupMap.get(RecapConstants.SHARED_CGD))) {
                            if(itemEntity.getCopyNumber() > 1) {
                                isMultipleCopy = true;
                                populateValues(materialTypeSet, useRestrictionMap, itemEntityMap, itemEntity);
                            }
                        }
                    }
                    if(!isMultipleCopy) {
                        isMonograph = false;
                        materialTypeSet.add(RecapConstants.MONOGRAPHIC_SET);
                    }
                } else {
                    //CUL & PUL
                    if(bibliographicEntity.getHoldingsEntities().size() > 1) {
                        for(ItemEntity itemEntity : itemEntities) {
                            if(itemEntity.getCollectionGroupId().equals(collectionGroupMap.get(RecapConstants.SHARED_CGD))) {
                                populateValues(materialTypeSet, useRestrictionMap, itemEntityMap, itemEntity);
                            }
                        }
                    } else {
                        isMonograph = false;
                        materialTypeSet.add(RecapConstants.MONOGRAPHIC_SET);
                    }
                }
            }
        }
        return isMonograph;
    }

    private void populateValues(Set<String> materialTypeSet, Map<Integer, Map<Integer, List<ItemEntity>>> useRestrictionMap, Map<Integer, ItemEntity> itemEntityMap, ItemEntity itemEntity) {
        itemEntityMap.put(itemEntity.getItemId(), itemEntity);
        Integer owningInstitutionId = itemEntity.getOwningInstitutionId();
        Integer useRestriction = getUseRestrictionInNumbers(itemEntity.getUseRestrictions());
        populateUseRestrictionMap(useRestrictionMap, itemEntity, owningInstitutionId, useRestriction);
        materialTypeSet.add(RecapConstants.MONOGRAPH);
    }

    public void populateUseRestrictionMap(Map<Integer, Map<Integer, List<ItemEntity>>> useRestrictionMap, ItemEntity itemEntity, Integer owningInstitutionId, Integer useRestriction) {
        if(useRestrictionMap.containsKey(useRestriction)) {
            Map<Integer, List<ItemEntity>> institutionMap = new HashMap<>();
            institutionMap.putAll(useRestrictionMap.get(useRestriction));
            if(institutionMap.containsKey(owningInstitutionId)) {
                List<ItemEntity> itemEntityList = new ArrayList<>();
                itemEntityList.addAll(institutionMap.get(owningInstitutionId));
                itemEntityList.add(itemEntity);
                institutionMap.put(owningInstitutionId, itemEntityList);
            } else {
                institutionMap.put(owningInstitutionId, Arrays.asList(itemEntity));
            }
            useRestrictionMap.put(useRestriction, institutionMap);
        } else {
            Map<Integer, List<ItemEntity>> institutionMap = new HashMap<>();
            institutionMap.put(owningInstitutionId, Arrays.asList(itemEntity));
            useRestrictionMap.put(useRestriction, institutionMap);
        }
    }

    private void findItemsToBeUpdatedAsOpen(Map<Integer, ItemEntity> itemEntityMap, Map<Integer, List<ItemEntity>> institutionMap) {
        if(institutionMap.size() > 1) {
            // Multiple Institution sharing the same use restriction
            findItemToBeSharedBasedOnCounter(itemEntityMap, institutionMap);

        } else {
            // Has only One Institution with this use Restriction
            for (Iterator<List<ItemEntity>> iterator = institutionMap.values().iterator(); iterator.hasNext(); ) {
                List<ItemEntity> itemEntities = iterator.next();
                findAndremoveSharedItem(itemEntityMap, itemEntities);
            }
        }
    }

    private void findItemToBeSharedBasedOnCounter(Map<Integer, ItemEntity> itemEntityMap, Map<Integer, List<ItemEntity>> institutionMap) {
        Set<Integer> owningInstitutions = institutionMap.keySet();
        Map<Integer, List<Integer>> counterMap = new HashMap<>();
        for (Iterator<Integer> iterator = owningInstitutions.iterator(); iterator.hasNext();) {
            Integer institution = iterator.next();
            populateCounterMap(counterMap, institution);
        }
        if(counterMap.size() > 1) {
            // Different Counter Values
            Integer count = Collections.min(counterMap.keySet());
            List<Integer> institutionList = counterMap.get(count);
            if(CollectionUtils.isNotEmpty(institutionList)) {
                // Institution to which item to be remained Shared
                Integer institution = institutionList.get(0);
                List<ItemEntity> itemEntities = institutionMap.get(institution);
                findAndremoveSharedItem(itemEntityMap, itemEntities);
            }
        } else {
            // The counter values are same across one or more institutions
            for (Iterator<List<Integer>> iterator = counterMap.values().iterator(); iterator.hasNext(); ) {
                List<Integer> institutions =  iterator.next();
                // Institution to which item to be remained Shared
                Integer institution = institutions.get(0);
                List<ItemEntity> itemEntities = institutionMap.get(institution);
                findAndremoveSharedItem(itemEntityMap, itemEntities);
            }
        }
    }

    private void findAndremoveSharedItem(Map<Integer, ItemEntity> itemEntityMap, List<ItemEntity> itemEntities) {
        // Item which needs to remain in Shared status and increment the institution's counter
        ItemEntity itemEntity = itemEntities.get(0);
        itemEntityMap.remove(itemEntity.getItemId());
        MatchingCounter.updateCounter(itemEntity.getOwningInstitutionId(), false);
    }

    private void populateCounterMap(Map<Integer, List<Integer>> counterMap, Integer institution) {
        Integer counter = getCounterForGivenInst(institution);
        if(counterMap.containsKey(counter)) {
            List<Integer> institutions = new ArrayList<>();
            institutions.addAll(counterMap.get(counter));
            institutions.add(institution);
            counterMap.put(counter, institutions);
        } else {
            counterMap.put(counter, Arrays.asList(institution));
        }
    }

    private Integer getCounterForGivenInst(Integer institution) {
        if(institution == 1) {
            return MatchingCounter.getPulSharedCount();
        } else if(institution == 2) {
            return MatchingCounter.getCulSharedCount();
        } else if(institution == 3) {
            return MatchingCounter.getNyplSharedCount();
        }
        return null;
    }

    private Integer getUseRestrictionInNumbers(String useRestrictions) {
        if(StringUtils.isBlank(useRestrictions)) {
            return 0;
        } else if(useRestrictions.equalsIgnoreCase(RecapConstants.IN_LIBRARY_USE)) {
            return 1;
        } else if(useRestrictions.equalsIgnoreCase(RecapConstants.SUPERVISED_USE)) {
            return 2;
        }
        return 0;
    }
}
