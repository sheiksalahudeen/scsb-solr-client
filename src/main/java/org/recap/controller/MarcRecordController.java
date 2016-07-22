package org.recap.controller;

import org.marc4j.marc.Record;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.search.BibliographicMarcRecord;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.util.BibJSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by rajeshbabuk on 22/7/16.
 */
@Controller
public class MarcRecordController {

    Logger logger = LoggerFactory.getLogger(MarcRecordController.class);

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @RequestMapping("/openMarcRecord")
    public String searchRecords(@Valid @ModelAttribute("bibId") Integer bibId, Model model) {
        BibliographicEntity bibliographicEntity = bibliographicDetailsRepository.findByBibliographicId(bibId);
        String bibContent = new String(bibliographicEntity.getContent());
        BibJSONUtil bibJSONUtil = new BibJSONUtil();
        List<Record> records = bibJSONUtil.convertMarcXmlToRecord(bibContent);
        Record marcRecord = records.get(0);
        BibliographicMarcRecord bibliographicMarcRecord = buildBibliographicMarcRecord(marcRecord, bibJSONUtil);
        bibliographicMarcRecord.setContent(bibContent);
        model.addAttribute("bibliographicMarcRecord", bibliographicMarcRecord);
        return "marcRecordView";
    }

    private BibliographicMarcRecord buildBibliographicMarcRecord(Record marcRecord, BibJSONUtil bibJSONUtil) {
        BibliographicMarcRecord bibliographicMarcRecord = new BibliographicMarcRecord();
        bibliographicMarcRecord.setTitle(bibJSONUtil.getTitle(marcRecord));
        bibliographicMarcRecord.setAuthor(bibJSONUtil.getAuthor(marcRecord));
        bibliographicMarcRecord.setPublisher(bibJSONUtil.getPublisherValue(marcRecord));
        bibliographicMarcRecord.setPublishedDate(bibJSONUtil.getPublicationDateValue(marcRecord));
        return bibliographicMarcRecord;
    }
}
