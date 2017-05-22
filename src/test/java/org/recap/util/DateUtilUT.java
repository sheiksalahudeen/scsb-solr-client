package org.recap.util;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by angelind on 18/5/17.
 */
public class DateUtilUT extends BaseTestCase{

    @Autowired
    DateUtil dateUtil;

    @Test
    public void getFromDate() throws Exception {
        Date fromDate = dateUtil.getFromDate(new Date());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        String dateString = simpleDateFormat.format(fromDate);
        assertTrue(dateString.contains("12:00 AM"));
    }

    @Test
    public void getToDate() throws Exception {
        Date toDate = dateUtil.getToDate(new Date());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        String dateString = simpleDateFormat.format(toDate);
        assertTrue(dateString.contains("11:59 PM"));
    }

}