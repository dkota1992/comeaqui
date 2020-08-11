package com.comeaqui.eduardorodriguez.comeaqui.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.comeaqui.eduardorodriguez.comeaqui.App.USER;

public class DateFormatting {
    static String[] WEEK_DAYS = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};

    public static Date convertToDate(String dateString) throws ParseException {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format.parse(dateString);
        } catch (Exception e){
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format.parse(dateString);
        }
    }

    public static String h(String dateString){
        try {
            Date date = convertToDate(dateString);
            DateFormat df = new SimpleDateFormat("h:mm aa");
            df.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
            String dateTextString = df.format(date);
            return dateTextString;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String hPost(String startDate){
        try {
            // https://stackoverflow.com/questions/32113211/saving-model-instance-with-datetimefield-in-django-admin-loses-microsecond-resol
            Date date = convertToDate(startDate);
            DateFormat df = new SimpleDateFormat("h:mm a");
            df.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
            String dateTextString = df.format(date);
            return dateTextString;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date startOfToday(String timeZone) throws ParseException {
        long now = System.currentTimeMillis();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone(timeZone));
        String startTodayString = format.format(new Date(now));
        return format.parse(startTodayString);
    }

    public static String todayYesterdayWeekDay(String dateString){
        try {
            Date date = convertToDate(dateString);

            long now = System.currentTimeMillis();
            long startOfDay = DateFormatting.startOfToday(USER.timeZone).getTime();

            long differenceToDate = now - date.getTime();
            long differenceToStartOrDay = now - startOfDay;

            if (differenceToStartOrDay >= differenceToDate) {
                return "TODAY";
            } else if (differenceToStartOrDay + TimeUnit.DAYS.toMillis(1) >= differenceToDate && differenceToDate > differenceToStartOrDay) {
                return "YESTERDAY";
            } else if (differenceToStartOrDay + TimeUnit.DAYS.toMillis(7) >= differenceToDate && differenceToDate > differenceToStartOrDay + TimeUnit.DAYS.toMillis(1)) {
                DateFormat dayNumberFormat = new SimpleDateFormat("u");
                dayNumberFormat.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
                return WEEK_DAYS[Integer.parseInt(dayNumberFormat.format(date)) - 1];
            } else{
                String pattern = "MM/dd/yyyy";
                DateFormat df = new SimpleDateFormat(pattern);
                df.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
                return df.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String hhmmHappenedNowTodayYesterdayWeekDay(String dateString, String endDateString){
        try {
            Date date;
            Date endDate;
            try {
                date = convertToDate(dateString);
                endDate = convertToDate(endDateString);
            } catch (ParseException e) {
                try {
                    // https://stackoverflow.com/questions/32113211/saving-model-instance-with-datetimefield-in-django-admin-loses-microsecond-resol
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    format.setTimeZone(TimeZone.getTimeZone("UTC"));
                    date = format.parse(dateString);
                    endDate = format.parse(endDateString);
                } catch (ParseException e2) {
                    return null;
                }
            }

            long now = System.currentTimeMillis();
            long startOfDay = DateFormatting.startOfToday(USER.timeZone).getTime();

            long differenceToDate = now - date.getTime();
            long differenceToStartOrDay = now - startOfDay;

            String result;
            if (now < date.getTime() - TimeUnit.DAYS.toMillis(2)){
                DateFormat df = new SimpleDateFormat("EEE, MMM d");
                df.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
                result = df.format(date);
            } else if (now < date.getTime() - TimeUnit.DAYS.toMillis(1)){
                DateFormat df = new SimpleDateFormat("h:mm aa");
                df.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
                result =  "TOMORROW";
            } else if (now < date.getTime()) {
                DateFormat df = new SimpleDateFormat("h:mm aa");
                df.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
                result =  "TODAY";
            } else if (date.getTime() <= now && now < endDate.getTime()){
                result =  "HAPPENING NOW";
            } else if (differenceToDate <= differenceToStartOrDay) {
                result =  "HAPPENED TODAY";
            } else if (differenceToDate <= differenceToStartOrDay + TimeUnit.DAYS.toMillis(1)) {
                result =  "YESTERDAY";
            } else if (differenceToDate <= differenceToStartOrDay + TimeUnit.DAYS.toMillis(7)) {
                int n = (int) (differenceToDate / TimeUnit.DAYS.toMillis(1));
                result =  n + " DAY" + (n > 1 ? "S " : " ") + "AGO";
            } else if (differenceToDate <= differenceToStartOrDay + TimeUnit.DAYS.toMillis(30)) {
                int n = (int) (differenceToDate / TimeUnit.DAYS.toMillis(7));
                result =  n + " WEEK" + (n > 1 ? "S " : " ") + " AGO";
            } else if (differenceToDate <= differenceToStartOrDay + TimeUnit.DAYS.toMillis(30 * 12)) {
                int n = (int) (differenceToDate / TimeUnit.DAYS.toMillis(30));
                result =  n + " MONTH" + (n > 1 ? "S " : " ") + " AGO";
            } else {
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                df.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
                result =  df.format(date);
            }
            return result.toUpperCase();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String timeRange(String dateString, String endDateString){
        Date date;
        Date endDate;
        try {
            date = convertToDate(dateString);
            endDate = convertToDate(endDateString);
        } catch (ParseException e) {
            try {
                // https://stackoverflow.com/questions/32113211/saving-model-instance-with-datetimefield-in-django-admin-loses-microsecond-resol
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                date = format.parse(dateString);
                endDate = format.parse(endDateString);
            } catch (ParseException e2) {
                return null;
            }
        }

        DateFormat df = new SimpleDateFormat("h:mm aa");
        df.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
        return df.format(date) + " - " + df.format(endDate);
    }


    public static String hYesterdayWeekDay(String dateString){
        try {
            Date date = convertToDate(dateString);
            long now = System.currentTimeMillis();
            long startOfDay = startOfToday(USER.timeZone).getTime();

            long differenceToDate = now - date.getTime();
            long differenceToStartOrDay = now - startOfDay;

            if (differenceToStartOrDay >= differenceToDate) {
                String pattern = "h:mm aa";
                DateFormat df = new SimpleDateFormat(pattern);
                df.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
                return df.format(date);
            } else if (differenceToStartOrDay + TimeUnit.DAYS.toMillis(1) >= differenceToDate && differenceToDate > differenceToStartOrDay) {
                return "YESTERDAY";
            } else if (differenceToStartOrDay + TimeUnit.DAYS.toMillis(7) >= differenceToDate && differenceToDate > differenceToStartOrDay + TimeUnit.DAYS.toMillis(1)) {
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                return WEEK_DAYS[c.get(Calendar.DAY_OF_WEEK) - 1];
            } else{
                String pattern = "MM/dd/yyyy";
                DateFormat df = new SimpleDateFormat(pattern);
                df.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
                return df.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
