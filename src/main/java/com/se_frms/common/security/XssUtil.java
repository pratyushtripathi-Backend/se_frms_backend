package com.se_frms.common.security;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class XssUtil {

    private XssUtil() {}

    public static String clean(String input) {

        if (input == null) {
            return null;
        }

        return Jsoup.clean(
                input,
                Safelist.none()
        );
    }
}