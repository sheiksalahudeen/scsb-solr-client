package org.recap.controller;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.search.BibDataField;
import org.recap.model.search.BibliographicMarcRecord;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.util.BibJSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 22/7/16.
 */
@Controller
public class MarcRecordController {

    Logger logger = LoggerFactory.getLogger(MarcRecordController.class);

    private String errorMessage;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    InstitutionDetailsRepository institutionDetailRepository;


    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @RequestMapping("/openMarcRecord")
    public String openMarcRecord(@Valid @ModelAttribute("bibId") Integer bibId, Model model) {
        BibliographicEntity bibliographicEntity = bibliographicDetailsRepository.findByBibliographicId(bibId);
        if(null == bibliographicEntity){
            BibliographicMarcRecord bibliographicMarcRecord = new BibliographicMarcRecord();
            bibliographicMarcRecord.setErrorMessage(RecapConstants.RECORD_NOT_AVAILABLE);
            model.addAttribute("bibliographicMarcRecord", bibliographicMarcRecord);
            return "marcRecordErrorMessage";
        }else {
            String bibContent = new String(bibliographicEntity.getContent());
            BibJSONUtil bibJSONUtil = new BibJSONUtil();
            List<Record> records = bibJSONUtil.convertMarcXmlToRecord(bibContent);
            Record marcRecord = records.get(0);
            BibliographicMarcRecord bibliographicMarcRecord = buildBibliographicMarcRecord(marcRecord, bibJSONUtil);
            bibliographicMarcRecord.setContent(bibContent);
            InstitutionEntity institutionEntity = institutionDetailRepository.findByInstitutionId(bibliographicEntity.getOwningInstitutionId());
            if (null != institutionEntity) {
                bibliographicMarcRecord.setOwningInstitution(institutionEntity.getInstitutionCode());
            }
            if (!CollectionUtils.isEmpty(bibliographicEntity.getItemEntities())) {
                bibliographicMarcRecord.setCallNumber(bibliographicEntity.getItemEntities().get(0).getCallNumber());
            }
            model.addAttribute("bibliographicMarcRecord", bibliographicMarcRecord);
            return "marcRecordView";
        }
    }

    private BibliographicMarcRecord buildBibliographicMarcRecord(Record marcRecord, BibJSONUtil bibJSONUtil) {
        BibliographicMarcRecord bibliographicMarcRecord = new BibliographicMarcRecord();
        bibliographicMarcRecord.setTitle(bibJSONUtil.getTitleDisplay(marcRecord));
        bibliographicMarcRecord.setAuthor(bibJSONUtil.getAuthorDisplayValue(marcRecord));
        bibliographicMarcRecord.setPublisher(bibJSONUtil.getPublisherValue(marcRecord));
        bibliographicMarcRecord.setPublishedDate(bibJSONUtil.getPublicationDateValue(marcRecord));
        bibliographicMarcRecord.setTag000(bibJSONUtil.getLeader(marcRecord));
        bibliographicMarcRecord.setControlNumber001(bibJSONUtil.getControlFieldValue(marcRecord, "001"));
        bibliographicMarcRecord.setControlNumber005(bibJSONUtil.getControlFieldValue(marcRecord, "005"));
        bibliographicMarcRecord.setControlNumber008(bibJSONUtil.getControlFieldValue(marcRecord, "008"));
        bibliographicMarcRecord.setBibDataFields(buildBibDataFields(marcRecord));
        return bibliographicMarcRecord;
    }

    private List<BibDataField> buildBibDataFields(Record marcRecord) {
        List<BibDataField> bibDataFields = new ArrayList<>();
        List<DataField> marcDataFields = marcRecord.getDataFields();
        if (!CollectionUtils.isEmpty(marcDataFields)) {
            for (DataField marcDataField : marcDataFields) {
                BibDataField bibDataField = new BibDataField();
                bibDataField.setDataFieldTag(marcDataField.getTag());
                if (Character.isWhitespace(marcDataField.getIndicator1())) {
                    bibDataField.setIndicator1('_');
                } else {
                    bibDataField.setIndicator1(marcDataField.getIndicator1());
                }
                if (Character.isWhitespace(marcDataField.getIndicator2())) {
                    bibDataField.setIndicator2('_');
                } else {
                    bibDataField.setIndicator2(marcDataField.getIndicator2());
                }
                List<Subfield> subfields = marcDataField.getSubfields();
                if (!CollectionUtils.isEmpty(subfields)) {
                    StringBuffer buffer = new StringBuffer();
                    for (Subfield subfield : subfields) {
                        buffer.append("|").append(subfield.getCode());
                        buffer.append(" ").append(subfield.getData()).append(" ");
                    }
                    bibDataField.setDataFieldValue(buffer.toString());
                }
                bibDataFields.add(bibDataField);
            }
        }
        return bibDataFields;
    }
}
