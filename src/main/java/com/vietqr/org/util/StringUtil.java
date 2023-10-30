package com.vietqr.org.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class StringUtil {

    private static final Logger logger = Logger.getLogger(StringUtil.class);

    public static List<String> findHashtags(String input) {
        List<String> hashtags = new ArrayList<>();
        try {
            if (input != null && !input.trim().isEmpty()) {
                Pattern pattern = Pattern.compile("#[\\p{L}\\p{N}_-]+");
                Matcher matcher = pattern.matcher(input);
                while (matcher.find()) {
                    String hashtag = matcher.group();
                    hashtags.add(hashtag);
                }
            }

        } catch (Exception e) {
            logger.error("findHashtags: ERROR: " + e.toString());
        }
        return hashtags;
    }
}
