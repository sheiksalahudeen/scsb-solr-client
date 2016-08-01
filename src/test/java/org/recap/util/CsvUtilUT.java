package org.recap.util;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.solr.MatchingRecordReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by premkb on 29/7/16.
 */
public class CsvUtilUT extends BaseTestCase {
    @Value("${solr.report.directory}")
    private String reportDirectoryPath;

    @Autowired
    private CsvUtil csvUtil;

    @Test
    public void createFile() {
        String fileName = "testcsv";
        csvUtil.createFile(fileName);
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        File file = new File(reportDirectoryPath + File.separator + fileName + "_" + df.format(new Date()) + ".csv");
        boolean fileExists = file.exists();
        assertTrue(fileExists);
        file.delete();
    }
}
