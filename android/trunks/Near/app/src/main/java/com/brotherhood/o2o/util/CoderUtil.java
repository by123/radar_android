package com.brotherhood.o2o.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/1/14 0014.
 */
public class CoderUtil {

    /**
     * Get XML String of utf-8
     *
     * @return XML-Formed string
     */
    public static String getUTF8String(String xml) {
        // A StringBuffer Object
        StringBuffer sb = new StringBuffer();
        sb.append(xml);
        String xmString = "";
        String xmlUTF8 = "";
        try {
            xmString = new String(sb.toString().getBytes("UTF-8"));
            xmlUTF8 = URLEncoder.encode(xmString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // return to String Formed
        return xmlUTF8;
    }

    public static final String GB2312 = "GB2312";
    public static final String ISO88591 = "ISO-8859-1";
    public static final String UTF8 = "UTF-8";
    public static final String GBK = "GBK";

    public static String getEncoding(String str) {
        String encode = GB2312;
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s = encode;
                return s;
            }
        } catch (Exception exception) {
        }
        encode = ISO88591;
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s1 = encode;
                return s1;
            }
        } catch (Exception exception1) {
        }
        encode = UTF8;
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s2 = encode;
                return s2;
            }
        } catch (Exception exception2) {
        }
        encode = GBK;
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s3 = encode;
                return s3;
            }
        } catch (Exception exception3) {
        }
        return "";
    }

    /**
     * 判断字符是否是中文
     *
     * @param c 字符
     * @return 是否是中文
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

//    /**
//     * 判断字符串是否是乱码
//     *
//     * @param strName 字符串
//     * @return 是否是乱码
//     */
//    public static boolean isMessyCode(String strName) {
//        Pattern p = Pattern.compile("\\s*|t*|r*|n*");
//        Matcher m = p.matcher(strName);
//        String after = m.replaceAll("");
//        String temp = after.replaceAll("\\p{P}", "");
//        char[] ch = temp.trim().toCharArray();
//        float chLength = ch.length;
//        float count = 0;
//        for (int i = 0; i < ch.length; i++) {
//            char c = ch[i];
//            if (!Character.isLetterOrDigit(c)) {
//                if (!isChinese(c)) {
//                    count = count + 1;
//                }
//            }
//        }
//        float result = count / chLength;
//        if (result > 0.4) {
//            return true;
//        } else {
//            return false;
//        }
//
//    }

    /**
     * 判断是否为乱码
     *
     * @param str
     * @return
     */
    public static boolean isMessyCode(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            // 当从Unicode编码向某个字符集转换时，如果在该字符集中没有对应的编码，则得到0x3f（即问号字符?）
            //从其他字符集向Unicode编码转换时，如果这个二进制数在该字符集中没有标识任何的字符，则得到的结果是0xfffd
            //System.out.println("--- " + (int) c);
            if ((int) c == 0xfffd) {
                // 存在乱码
                //System.out.println("存在乱码 " + (int) c);
                return true;
            }
        }
        return false;
    }


    //chenese为gbk中文
    public static byte[] gbk2utf8(String chenese) {
        char c[] = chenese.toCharArray();
        byte[] fullByte = new byte[3 * c.length];
        int index = 0;
        for (int i = 0; i < c.length; i++) {
            int m = (int) c[i];
            if (m > 0 && m < 255) {  //判断是否是中文
                fullByte[index] = (byte) c[i];
                index++;
                continue;
            }
            //如果是中文字符则补齐一个字节
            //gbk一个字对应两个字节而UTF-8一个字对应三个字节
            String word = Integer.toBinaryString(m);
// System.out.println(word);

            StringBuffer sb = new StringBuffer();
            int len = 16 - word.length();

            for (int j = 0; j < len; j++) {
                sb.append("0");
            }
            sb.append(word);
            sb.insert(0, "1110");
            sb.insert(8, "10");
            sb.insert(16, "10");

            System.out.println(sb.toString());

            String s1 = sb.substring(0, 8);
            String s2 = sb.substring(8, 16);
            String s3 = sb.substring(16);

            byte b0 = Integer.valueOf(s1, 2).byteValue();
            byte b1 = Integer.valueOf(s2, 2).byteValue();
            byte b2 = Integer.valueOf(s3, 2).byteValue();
            byte[] bf = new byte[3];
            bf[0] = b0;
            fullByte[index] = bf[0];
            bf[1] = b1;
            fullByte[index + 1] = bf[1];
            bf[2] = b2;
            fullByte[index + 2] = bf[2];
            index = index + 3;
        }
        byte[] result = new byte[index];
        System.arraycopy(fullByte, 0, result, 0, index);
        return result;
    }
}

