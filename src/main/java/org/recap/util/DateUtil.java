package org.recap.util;

import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by angelind on 17/5/17.
 */
@Service
public class DateUtil {

    /**
     * Gets from date from the parameter and formats by setting start time of the day.
     *
     * @param createdDate the created date
     * @return the from date
     */
    public Date getFromDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return  cal.getTime();
    }

    /**
     * Gets to date from the parameter and formats by setting end time of the day.
     *
     * @param createdDate the created date
     * @return the to date
     */
    public Date getToDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }
}
