package org.recap.service.accession;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.accession.BatchAccessionResponse;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.service.accession.callable.BibDataCallable;
import org.recap.service.accession.resolver.BibDataResolver;
import org.recap.service.accession.resolver.CULBibDataResolver;
import org.recap.service.accession.resolver.NYPLBibDataResolver;
import org.recap.service.accession.resolver.PULBibDataResolver;
import org.recap.spring.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by sheiks on 26/05/17.
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BulkAccessionService extends AccessionService{

    private static final Logger logger = LoggerFactory.getLogger(BulkAccessionService.class);

    @Autowired
    private PULBibDataResolver pulBibDataResolver;

    @Autowired
    private CULBibDataResolver culBibDataResolver;

    @Autowired
    private NYPLBibDataResolver nyplBibDataResolver;

    private List<BibDataResolver> bibDataResolvers;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * The batch accession thread size.
     */
    @Value("${batch.accession.thread.size}")
    int batchAccessionThreadSize;

    public BatchAccessionResponse processAccessionRequest(List<AccessionRequest> accessionRequestList) {
        BatchAccessionResponse batchAccessionResponse = new BatchAccessionResponse();
        int requestedCount = accessionRequestList.size();
        List<AccessionRequest> trimmedAccessionRequests = getTrimmedAccessionRequests(accessionRequestList);
        trimmedAccessionRequests = removeDuplicateRecord(trimmedAccessionRequests);

        int duplicateCount = requestedCount - trimmedAccessionRequests.size();
        batchAccessionResponse.setRequestedRecords(requestedCount);
        batchAccessionResponse.setDuplicateRecords(duplicateCount);

        List<ReportDataEntity> reportDataEntityList = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(batchAccessionThreadSize);

        List<List<AccessionRequest>> partitions = Lists.partition(trimmedAccessionRequests, batchAccessionThreadSize);

        for (Iterator<List<AccessionRequest>> iterator = partitions.iterator(); iterator.hasNext(); ) {
            List<AccessionRequest> accessionRequests = iterator.next();
            List<Future> futures = new ArrayList<>();
            List<AccessionRequest> failedRequests = new ArrayList<>();
            for (Iterator<AccessionRequest> accessionRequestIterator = accessionRequests.iterator(); accessionRequestIterator.hasNext(); ) {
                AccessionRequest accessionRequest = accessionRequestIterator.next();

                String itemBarcode = accessionRequest.getItemBarcode();
                if(StringUtils.isBlank(itemBarcode)) {
                    batchAccessionResponse.addEmptyBarcodes(1);
                    reportDataEntityList.addAll(getAccessionHelperUtil().createReportDataEntityList(accessionRequest, RecapConstants.ITEM_BARCODE_EMPTY));
                    String owningInstitution = getAccessionHelperUtil().getOwningInstitution(accessionRequest.getCustomerCode());
                    saveReportEntity(owningInstitution, reportDataEntityList);
                    continue;
                }
                BibDataCallable bibDataCallable = (BibDataCallable) applicationContext.getBean(BibDataCallable.class);
                bibDataCallable.setAccessionRequest(accessionRequest);
                futures.add(executorService.submit(bibDataCallable));

            }
            for (Iterator<Future> futureIterator = futures.iterator(); futureIterator.hasNext(); ) {
                Future bibDataFuture = futureIterator.next();
                try {
                    Object object = bibDataFuture.get();
                    if(object instanceof Set) {
                        processSuccessResponse(batchAccessionResponse, object);
                    } else if(object instanceof AccessionRequest) {
                        failedRequests.add((AccessionRequest)object);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Processed failed barcodes one by one
            for (Iterator<AccessionRequest> accessionRequestIterator = failedRequests.iterator(); accessionRequestIterator.hasNext(); ) {
                AccessionRequest accessionRequest = accessionRequestIterator.next();
                BibDataCallable bibDataCallable = applicationContext.getBean(BibDataCallable.class);
                bibDataCallable.setAccessionRequest(accessionRequest);
                bibDataCallable.setWriteToReport(true);
                Future submit = executorService.submit(bibDataCallable);
                try {
                    Object o = submit.get();
                    processSuccessResponse(batchAccessionResponse, o);
                } catch (Exception e) {
                    e.printStackTrace();
                    batchAccessionResponse.addException(1);
                }
            }
        }

        return batchAccessionResponse;

    }

    public void createSummaryReport(String summary) {
        List<ReportDataEntity> reportDataEntityList = new ArrayList<>();
        ReportDataEntity reportDataEntityMessage = new ReportDataEntity();
        reportDataEntityMessage.setHeaderName(RecapConstants.BATCH_ACCESSION_SUMMARY);
        reportDataEntityMessage.setHeaderValue(summary);
        reportDataEntityList.add(reportDataEntityMessage);
        saveReportEntity(null, reportDataEntityList);
    }

    private List<AccessionRequest> removeDuplicateRecord(List<AccessionRequest> trimmedAccessionRequests) {
        Set<AccessionRequest> accessionRequests = new HashSet<>(trimmedAccessionRequests);
        return new ArrayList<>(accessionRequests);
    }

    private void processSuccessResponse(BatchAccessionResponse batchAccessionResponse, Object object) {
        Set<AccessionResponse> accessionResponses = (Set<AccessionResponse>) object;
        if(CollectionUtils.isNotEmpty(accessionResponses)) {
            for (Iterator<AccessionResponse> iterator = accessionResponses.iterator(); iterator.hasNext(); ) {
                AccessionResponse accessionResponse = iterator.next();
                String message = accessionResponse.getMessage();
                if(message.contains(RecapConstants.SUCCESS)) {
                    batchAccessionResponse.addSuccessRecord(1);
                }else if(message.contains(RecapConstants.ITEM_ALREADY_ACCESSIONED)) {
                    batchAccessionResponse.addAlreadyAccessioned(1);
                } else if(message.contains(RecapConstants.ACCESSION_DUMMY_RECORD)) {
                    batchAccessionResponse.addDummyRecords(1);
                } else if(message.contains(RecapConstants.EXCEPTION)) {
                    batchAccessionResponse.addException(1);
                } else if(StringUtils.equalsIgnoreCase(RecapConstants.INVALID_BARCODE_LENGTH, message)) {
                    batchAccessionResponse.addInvalidLenghBarcode(1);
                } else if(StringUtils.equalsIgnoreCase(RecapConstants.OWNING_INST_EMPTY, message)) {
                    batchAccessionResponse.addEmptyOwningInst(1);
                } else if(StringUtils.equalsIgnoreCase(RecapConstants.ITEM_BARCODE_EMPTY, message)) {
                    batchAccessionResponse.addEmptyBarcodes(1);
                } else {
                    batchAccessionResponse.setFailure(1);
                }
            }
        }
    }

    @Override
    public BibliographicEntity saveBibRecord(BibliographicEntity fetchBibliographicEntity) {
        return accessionDAO.saveBibRecord(fetchBibliographicEntity);
    }

    public List<BibDataResolver> getBibDataResolvers() {
        if(CollectionUtils.isEmpty(bibDataResolvers)) {
            bibDataResolvers = new ArrayList<>();
            bibDataResolvers.add(pulBibDataResolver);
            bibDataResolvers.add(culBibDataResolver);
            bibDataResolvers.add(nyplBibDataResolver);
        }
        return bibDataResolvers;
    }

}
