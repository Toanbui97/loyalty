package vn.com.vpbanks.loyalty.core.utils;

import lombok.experimental.UtilityClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public class DateTimeUtils {

    AtomicInteger atomicInteger = new AtomicInteger(0);

    public static final SimpleDateFormat formatter = new SimpleDateFormat();
    public static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final String ISO_8601_FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_YYYY_MM_DD = "yyyy/MM/dd";
    public static final String FORMAT_MM_DD_YYYY = "MM/dd/yyyy";
    public static final String FORMAT_DD_MM_YYYY = "dd/MM/yyyy";
    public static final String FORMAT_DD_MM = "dd/MM";
    public static final String FORMAT_DATE_TIME3 = "dd/MM/yyyy HH:mm:ss";
    public static final String FORMAT_DATE_TIME4 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE_TIME5 = "dd-MM-yyyy HH:mm:ss";

    public static final String TIME_ZONE_VIETNAM = "Asia/Ho_Chi_Minh";

    public static String dateToString(Date date, String format) {
        formatter.applyPattern(format);
        formatter.setLenient(false);
        return formatter.format(date);
    }

    public static Date stringToDate(String dateStr, String format) throws ParseException {
        formatter.applyPattern(format);
        formatter.setLenient(false);
        return formatter.parse(dateStr.trim());
    }

    public static Date longToDate(Long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return cal.getTime();
    }

    public static LocalDate convertStringToLocalDate(String dateStr, String format) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(format));
    }

    public static LocalDate convertDateToLocalDate(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static Date convertLocalDateToDate(LocalDate dateToConvert) {
        return Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
}
