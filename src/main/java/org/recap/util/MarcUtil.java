package org.recap.util;

import info.freelibrary.marc4j.impl.ControlFieldImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pvsubrah on 6/15/16.
 */
public class MarcUtil {

    public List<Record> convertMarcXmlToRecord(String marcXml) {
        List<Record> records = new ArrayList<>();
        MarcReader reader = new MarcXmlReader(IOUtils.toInputStream(marcXml));
        while (reader.hasNext()) {
            Record record = reader.next();
            records.add(record);
        }
        return records;
    }

    public String getDataFieldValueStartsWith(Record record, String dataFieldStartTag) {
        StringBuffer fieldValue = new StringBuffer();
        if (record != null) {
            List<VariableField> variableFields = record.getVariableFields();
            if (!CollectionUtils.isEmpty(variableFields)) {
                for (VariableField variableField : variableFields) {
                    if (variableField != null && StringUtils.isNotBlank(variableField.getTag()) && variableField.getTag().startsWith(dataFieldStartTag)) {
                        DataField dataField = (DataField) variableField;
                        List<Subfield> subfields = dataField.getSubfields();
                        for (Subfield subfield : subfields) {
                            if (subfield != null && StringUtils.isNotBlank(subfield.getData())) {
                                fieldValue.append(subfield.getData());
                                fieldValue.append(" ");
                            }
                        }
                    }
                }
            }
        }
        return fieldValue.toString().trim();
    }

    public String getDataFieldValueStartsWith(Record record, String dataFieldStartTag, List<Character> subFieldTags) {
        StringBuffer fieldValue = new StringBuffer();
        if (record != null) {
            List<VariableField> variableFields = record.getVariableFields();
            if (!CollectionUtils.isEmpty(variableFields)) {
                Subfield subfield;
                for (VariableField variableField : variableFields) {
                    if (variableField != null && StringUtils.isNotBlank(variableField.getTag()) && variableField.getTag().startsWith(dataFieldStartTag)) {
                        DataField dataField = (DataField) variableField;
                        for (Character subFieldTag : subFieldTags){
                            subfield = dataField.getSubfield(subFieldTag);
                            if (subfield != null) {
                                fieldValue.append(subfield.getData());
                                fieldValue.append(" ");
                            }
                        }
                    }
                }
            }
        }
        return fieldValue.toString().trim();
    }

    public List<String> getListOfDataFieldValuesStartsWith(Record record, String dataFieldStartTag, List<Character> subFieldTags) {
        List<String> fieldValues = new ArrayList<>();
        if (record != null) {
            List<VariableField> variableFields = record.getVariableFields();
            if (!CollectionUtils.isEmpty(variableFields)) {
                Subfield subfield;
                for (VariableField variableField : variableFields) {
                    if (variableField != null && StringUtils.isNotBlank(variableField.getTag()) && variableField.getTag().startsWith(dataFieldStartTag)) {
                        DataField dataField = (DataField) variableField;
                        for (Character subFieldTag : subFieldTags){
                            subfield = dataField.getSubfield(subFieldTag);
                            if (subfield != null) {
                                String data = subfield.getData();
                                if (StringUtils.isNotBlank(data)){
                                    fieldValues.add(data);
                                }
                            }
                        }
                    }
                }
            }
        }
        return fieldValues;
    }

    public String getDataFieldValue(Record marcRecord, String field, String ind1, String ind2, String subField) {
        List<String> strings = resolveValue(marcRecord, field, ind1, ind2, subField);
        return CollectionUtils.isEmpty(strings)? "" : strings.get(0);
    }

    public List<String> getMultiDataFieldValues(Record marcRecord, String field, String ind1, String ind2, String subField) {
        return resolveValue(marcRecord, field, ind1, ind2, subField);
    }

    private List<String> resolveValue(Record marcRecord, String field, String ind1, String ind2, String subField) {
        List<String> values = new ArrayList<>();
        String indicator1 = (StringUtils.isNotBlank(ind1) ? String.valueOf(ind1.charAt(0)) : " ");
        String indicator2 = (StringUtils.isNotBlank(ind2) ? String.valueOf(ind2.charAt(0)) : " ");
        List<VariableField> dataFields = marcRecord.getVariableFields(field);

        for (Iterator<VariableField> variableFieldIterator = dataFields.iterator(); variableFieldIterator.hasNext(); ) {
            DataField dataField = (DataField) variableFieldIterator.next();
            if(dataField!=null){
                if (doIndicatorsMatch(indicator1, indicator2, dataField)) {
                    List<Subfield> subFields = dataField.getSubfields(subField);
                    for (Iterator<Subfield> subfieldIterator = subFields.iterator(); subfieldIterator.hasNext(); ) {
                        Subfield subfield = subfieldIterator.next();
                        if (subField!=null){
                            String data = subfield.getData();
                            if (StringUtils.isNotBlank(data)) {
                                values.add(data);
                            }
                        }
                    }
                }
            }
        }
        return values;
    }

    private boolean doIndicatorsMatch(String indicator1, String indicator2, DataField dataField) {
        boolean result = true;
        if (StringUtils.isNotBlank(indicator1)) {
            result = dataField.getIndicator1() == indicator1.charAt(0);
        }
        if (StringUtils.isNotBlank(indicator2)) {
            result &= dataField.getIndicator2() == indicator2.charAt(0);
        }
        return result;
    }

    public String getControlFieldValue(Record marcRecord, String field) {
        List<VariableField> variableFields = marcRecord.getVariableFields(field);
        for (Iterator<VariableField> variableFieldIterator = variableFields.iterator(); variableFieldIterator.hasNext(); ) {
            ControlFieldImpl controlField = (ControlFieldImpl) variableFieldIterator.next();
            if (controlField!=null) {
                return controlField.getData();
            }
        }
        return null;
    }

}
