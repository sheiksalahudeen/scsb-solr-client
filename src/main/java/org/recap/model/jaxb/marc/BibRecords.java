package org.recap.model.jaxb.marc;

import org.recap.model.jaxb.BibRecord;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * Created by chenchulakshmig on 5/8/16.
 */
@XmlRootElement
public class BibRecords implements Serializable {

    private List<BibRecord> bibRecordList;

    /**
     * Gets bib record list.
     *
     * @return the bib record list
     */
    @XmlElement(name = "bibRecord")
    public List<BibRecord> getBibRecordList() {
        return bibRecordList;
    }

    /**
     * Sets bib record list.
     *
     * @param bibRecordList the bib record list
     */
    public void setBibRecordList(List<BibRecord> bibRecordList) {
        this.bibRecordList = bibRecordList;
    }
}
