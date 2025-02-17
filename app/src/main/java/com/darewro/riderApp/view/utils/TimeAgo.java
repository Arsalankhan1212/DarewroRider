package com.darewro.riderApp.view.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeAgo {
    public static String getTimeAgo(String timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
        sdf.setLenient(false);

        try {
            Date givenDate = sdf.parse(timestamp);
            Date now = new Date();
            long durationMillis = now.getTime() - givenDate.getTime();

            long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis);
            if (seconds < 60) return seconds + " seconds ago";
            long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
            if (minutes < 60) return minutes + " minutes ago";
            long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
            if (hours < 24) return hours + " hours ago";
            long days = TimeUnit.MILLISECONDS.toDays(durationMillis);
            if (days < 30) return days + " days ago";
            long months = days / 30;
            if (months < 12) return months + " months ago";
            long years = months / 12;
            return years + " years ago";
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid date";
        }
    }

}