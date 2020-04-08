package com.example.reservationproject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class TimeCalculator {
    private static final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static long diff = 0;

    public static long timeDiff(String startTime, String endTime) {

        try {
            Date beginDate = f.parse(startTime);
            Date endDate = f.parse(endTime);

            // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
            diff = endDate.getTime() - beginDate.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            return diff;
        }

    }

    public static String timeAddMin(String firstTIme, int time) throws ParseException {
            Calendar cal = new GregorianCalendar(Locale.KOREA);
            cal.setTime(f.parse(firstTIme));
            cal.add(Calendar.MINUTE, time);

            String strDate = f.format(cal.getTime());

            return strDate;
    }

    public static String timeAddHour(String firstTIme, int time) throws ParseException {
        Calendar cal = new GregorianCalendar(Locale.KOREA);
        cal.setTime(f.parse(firstTIme));
        cal.add(Calendar.HOUR, time);

        String strDate = f.format(cal.getTime());

        return strDate;
    }
}
