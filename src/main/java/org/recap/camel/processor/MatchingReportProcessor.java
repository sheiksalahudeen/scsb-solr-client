package org.recap.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by angelind on 22/8/16.
 */

@Component
public class MatchingReportProcessor implements Processor {

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Override
    public void process(Exchange exchange) throws Exception {
        List<ReportEntity> reportEntities = (List<ReportEntity>) exchange.getIn().getBody();
        reportDetailRepository.save(reportEntities);
    }
}
