package com.hadenwatne.splatbot.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataService {
    public static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

    public static String BuildTimeWindowString(Calendar start, Calendar end) {
        Date now = new Date();

        if (now.after(start.getTime()) && now.before(end.getTime())) {
            return "— Now —";
        }

        String startTime = (start.get(Calendar.HOUR) == 0 ? 12 : start.get(Calendar.HOUR)) + ":" + start.get(Calendar.MINUTE) + "0" + (start.get(Calendar.AM_PM) == Calendar.AM ? "a" : "p");
        String endTime = (end.get(Calendar.HOUR) == 0 ? 12 : end.get(Calendar.HOUR)) + ":" + end.get(Calendar.MINUTE) + "0" + (end.get(Calendar.AM_PM) == Calendar.AM ? "a" : "p");
        String timeHeader = HourToClockEmoji(start.get(Calendar.HOUR)) + " " + (start.get(Calendar.MONTH) + 1) + "/" + start.get(Calendar.DAY_OF_MONTH) + " " + startTime + " — " + (end.get(Calendar.MONTH) + 1) + "/" + end.get(Calendar.DAY_OF_MONTH) + " " + endTime;

        return timeHeader;
    }

    public static String BuildUpdatedTimestamp(Calendar time, boolean refreshing) {
        String startTime = (time.get(Calendar.HOUR) == 0 ? 12 : time.get(Calendar.HOUR)) + ":" + (time.get(Calendar.MINUTE) < 10 ? "0" : "") + time.get(Calendar.MINUTE) + (time.get(Calendar.AM_PM) == Calendar.AM ? "a" : "p");
        return (refreshing ? "Refreshed " : "As of ") + (time.get(Calendar.MONTH) + 1) + "/" + time.get(Calendar.DAY_OF_MONTH) + " @ " + startTime + " • " + time.getTimeZone().getDisplayName(time.getTimeZone().inDaylightTime(time.getTime()), TimeZone.SHORT);
    }

    public static LinkedHashMap<String, Integer> SortHashMap(HashMap<String, Integer> passedMap) {
        List<String> mapKeys = new ArrayList<String>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<Integer>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);
        Collections.reverse(mapValues);
        Collections.reverse(mapKeys);

        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();

        for (int val : mapValues) {
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                int comp1 = passedMap.get(key);

                if (comp1 == val) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }

        return sortedMap;
    }

    public static Date ParseDate(String date) {
        try {
            return Date.from(Instant.parse(date));
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean IsLong(String test) {
        try {
            Long.parseLong(test);
            return true;
        } catch (Exception ignored) {}

        return false;
    }

    public static boolean IsBoolean(String test) {
        return test.equalsIgnoreCase("true") || test.equalsIgnoreCase("false");
    }

    public static boolean IsInteger(String test) {
        try {
            Integer.parseInt(test);
            return true;
        }catch(Exception e) {
            return false;
        }
    }

    public static String HourToClockEmoji(int hour) {
        if (hour == 0) {
            return ":clock12:";
        }

        return ":clock" + hour + ":";
    }
}
