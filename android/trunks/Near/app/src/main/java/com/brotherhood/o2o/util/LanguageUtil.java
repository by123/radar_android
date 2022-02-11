package com.brotherhood.o2o.util;

import java.util.Locale;

/**
 * Created by Administrator on 2016/1/23 0023.
 */
public class LanguageUtil {

    public static final String ENV_EN = "en";
    public static final String ENV_CN = "cn";
    public static final String ENV_ZH = "zh";

    public static String getEnv() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        String country = l.getCountry().toLowerCase();
        if (ENV_EN.equals(language)) {
            language = ENV_EN;
        } else if (ENV_CN.equals(language)) {
            language = ENV_CN;
        } else if (ENV_ZH.equals(language)) {
            language = ENV_ZH;
        }
        return language;
    }


}
