package com.sziti.locationkit2.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringKit {
    private static final Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private static final Pattern numericPattern = Pattern.compile("^[0-9]+$");
    private static final Pattern blankPattern = Pattern.compile("\\s*|\t|\r|\n");
    private static final Pattern blankPatternLR = Pattern.compile("^\\s*|\\s*$");
    private static final Pattern letterPattern = Pattern.compile("[a-zA-Z]");
    private static final Pattern htmlPattern = Pattern.compile("<[^>]+>", 2);
    private static Pattern abstractPattern = Pattern.compile("^.{1,10}[报网]讯\\s?([（(][^）)]*[）)])?");

    public StringKit() {
    }

    public static boolean isEmpty(String src) {
        return src == null || src.trim().length() == 0;
    }

    public static boolean isNotEmpty(String src) {
        return !isEmpty(src);
    }

    public static boolean isEmail(String src) {
        return isEmpty(src) ? false : emailer.matcher(src).matches();
    }

    public static boolean isNumeric(String src) {
        return isEmpty(src) ? false : numericPattern.matcher(src).matches();
    }

    public static boolean isLetter(String src) {
        return isEmpty(src) ? false : letterPattern.matcher(src).matches();
    }

    public static String trim(String str) {
        if (isEmpty(str)) {
            return str;
        } else {
            Matcher m = blankPattern.matcher(str);
            String after = m.replaceAll("");
            return after;
        }
    }

    public static String trimAbstract(String str) {
        if (str == null) {
            return str;
        } else {
            Matcher m = abstractPattern.matcher(str);
            String after = m.replaceAll("");
            return after;
        }
    }

    public static String trimLR(String str) {
        if (isEmpty(str)) {
            return str;
        } else {
            Matcher m = blankPatternLR.matcher(str);
            String after = m.replaceAll("");
            return after;
        }
    }

    public static String trimHtml(String str) {
        if (isEmpty(str)) {
            return str;
        } else {
            Matcher m = htmlPattern.matcher(str);
            String after = m.replaceAll("");
            return after;
        }
    }

    public static InputStream string2Stream(String str) {
        if (isEmpty(str)) {
            return null;
        } else {
            ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
            return stream;
        }
    }

    public static String stream2String(InputStream inStream) throws IOException {
        return new String(stream2Bytes(inStream));
    }

    public static byte[] stream2Bytes(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = null;

        try {
            outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            boolean var3 = false;

            int len;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }

            byte[] var5 = outStream.toByteArray();
            return var5;
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (Exception var10) {
            }

        }
    }
}
