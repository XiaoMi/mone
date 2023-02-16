package com.xiaomi.hera.trace.etl.common;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/11/7 6:17 下午
 */
public class TimeConverter {

    private static final String MICRO_SECOND = "us";
    private static final String MILLI_SECOND = "ms";
    private static final String SECOND = "s";

    private static final String LINE = "-";

    private static final int SCALE_UNIT = 1000;

    private static DateTimeFormatter TIME_BUCKET_FORMATTER = DateTimeFormat.forPattern("yyyyMMdd");
    private static final DateTime DAY_ONE = TIME_BUCKET_FORMATTER.parseDateTime("20000101");
    private static int SUPER_DATASET_DAY_STEP = 1;

    public static long getMicro(String timeWithUnit){
        if(StringUtils.isEmpty(timeWithUnit)){
            return 0;
        }
        if(isNumeric(timeWithUnit)){
            return Long.parseLong(timeWithUnit);
        }
        if(timeWithUnit.endsWith(MICRO_SECOND)){
            String substring = timeWithUnit.substring(0, timeWithUnit.indexOf(MICRO_SECOND));
            return Long.parseLong(substring);
        }
        if(timeWithUnit.endsWith(MILLI_SECOND)){
            String substring = timeWithUnit.substring(0, timeWithUnit.indexOf(MILLI_SECOND));
            return Long.parseLong(substring) * SCALE_UNIT;
        }
        if(timeWithUnit.endsWith(SECOND)){
            String substring = timeWithUnit.substring(0, timeWithUnit.indexOf(SECOND));
            return Long.parseLong(substring) * SCALE_UNIT * SCALE_UNIT;
        }
        return 0;
    }

    public static boolean isNumeric(final String cs) {
        if (cs == null || cs.length() == 0) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String[] getIndexNamesByTimes(String indexName, long startTime, long endTime) {
        if (startTime == 0 || endTime == 0) {
            return new String[] {indexName};
        }
        long endTimestampOfDay = Timestamp.valueOf(LocalDateTime.now().with(LocalTime.MAX)).getTime();
        if (endTime > endTimestampOfDay) {
            endTime = endTimestampOfDay;
        }
        DateTime startDateTime = new DateTime(startTime);
        DateTime endDateTime = new DateTime(endTime);
        List<DateTime> timeRanges = new ArrayList<>(16);
        for (int i = 0; i <= Days.daysBetween(startDateTime, endDateTime).getDays(); i++) {
            timeRanges.add(startDateTime.plusDays(i));
        }
        if (timeRanges.isEmpty()) {
            return new String[] {indexName};
        } else {
            return timeRanges.stream()
                    .map(item -> indexName + LINE + formatCompressDateTime(item, SUPER_DATASET_DAY_STEP))
                    .distinct()
                    .toArray(String[]::new);
        }
    }

    private static String formatCompressDateTime(DateTime time, int dayStep) {
        return formatDateTime(compressDateTime(time, dayStep));
    }

    private static long compressDateTime(DateTime time, int dayStep) {
        if (dayStep > 1) {
            int days = Days.daysBetween(DAY_ONE, time).getDays();
            int groupBucketOffset = days % dayStep;
            return Long.parseLong(time.minusDays(groupBucketOffset).toString(TIME_BUCKET_FORMATTER));
        } else {
            /**
             * No calculation required. dayStep is for lower traffic. For normally configuration, there is pointless to calculate.
             */
            return Long.parseLong(time.toString(TIME_BUCKET_FORMATTER));
        }
    }

    private static String formatDateTime(long dateTime) {
        String dateTimeStr = String.valueOf(dateTime);
        String year = dateTimeStr.substring(0, 4);
        String month = dateTimeStr.substring(4, 6);
        String day = dateTimeStr.substring(6, 8);
        return String.format("%s.%s.%s", year, month, day);
    }
}
