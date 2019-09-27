package com.sziti.locationkit2.util;

import android.util.Log;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PinYinUtil {
    static final int GB_SP_DIFF = 160;
    // 存放国标一级汉字不同读音的起始区位码
    static final int[] secPosValueList = { 1601, 1637, 1833, 2078, 2274, 2302,
            2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027,
            4086, 4390, 4558, 4684, 4925, 5249, 5600 };
    // 存放国标一级汉字不同读音的起始区位码对应读音
    static final char[] firstLetter = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x',
            'y', 'z' };
    public static String getSpells(String characters) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < characters.length(); i++) {
            char ch = characters.charAt(i);
            if ((ch >> 7) == 0) {
                // 判断是否为汉字，如果左移7为为0就不是汉字，否则是汉字
            } else {
                char spell = getFirstLetter(ch);
                buffer.append(String.valueOf(spell));
            }
        }
        return buffer.toString();
    }

    // 获取一个汉字的首字母
    public static Character getFirstLetter(char ch) {
		Log.i("zxb","ch:"+ ch);
        byte[] uniCode = null;
        try {
            uniCode = String.valueOf(ch).getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
		Log.i("zxb","首字母:"+ uniCode);
        if (uniCode[0] < 128 && uniCode[0] > 0) { // 非汉字
            return null;
        } else {
            return convert(uniCode);
        }
    }

    /**
     * 获取一个汉字的拼音首字母。 GB码两个字节分别减去160，转换成10进制码组合就可以得到区位码
     * 例如汉字“你”的GB码是0xC4/0xE3，分别减去0xA0（160）就是0x24/0x43
     * 0x24转成10进制就是36，0x43是67，那么它的区位码就是3667，在对照表中读音为‘n’
     */
    static char convert(byte[] bytes) {
        char result = '-';
        int secPosValue = 0;
        int i;
        for (i = 0; i < bytes.length; i++) {
            bytes[i] -= GB_SP_DIFF;
        }
        secPosValue = bytes[0] * 100 + bytes[1];
        for (i = 0; i < 23; i++) {
            if (secPosValue >= secPosValueList[i]
                    && secPosValue < secPosValueList[i + 1]) {
                result = firstLetter[i];
                break;
            }
        }
        return result;
    }
	/**
	 *  获取汉字首字母或全拼大写字母
	 *
	 * @param chinese 汉字
	 * @param isFull  是否全拼 true:表示全拼 false表示：首字母
	 *
	 * @return 全拼或者首字母大写字符窜
	 */
	public static String getUpperCase(String chinese,boolean isFull){
		return convertHanzi2Pinyin(chinese,isFull).toUpperCase();
	}

	/**
	 * 获取汉字首字母或全拼小写字母
	 *
	 * @param chinese 汉字
	 * @param isFull 是否全拼 true:表示全拼 false表示：首字母
	 *
	 * @return 全拼或者首字母小写字符窜
	 */
	public static  String getLowerCase(String chinese,boolean isFull){
		return convertHanzi2Pinyin(chinese,isFull).toLowerCase();
	}

	/**
	 * 将汉字转成拼音
	 * <P>
	 * 取首字母或全拼
	 *
	 * @param hanzi 汉字字符串
	 * @param isFull 是否全拼 true:表示全拼 false表示：首字母
	 *
	 * @return 拼音
	 */
	private static String convertHanzi2Pinyin(String hanzi,boolean isFull){
		/***
		 * ^[\u2E80-\u9FFF]+$ 匹配所有东亚区的语言
		 * ^[\u4E00-\u9FFF]+$ 匹配简体和繁体
		 * ^[\u4E00-\u9FA5]+$ 匹配简体
		 */
		String regExp="^[\u4E00-\u9FFF]+$";
		StringBuffer sb=new StringBuffer();
		if(hanzi==null||"".equals(hanzi.trim())){
			return "";
		}
		String pinyin="";
		for(int i=0;i<hanzi.length();i++){
			char unit=hanzi.charAt(i);
			//是汉字，则转拼音
			if(match(String.valueOf(unit),regExp)){
				pinyin=convertSingleHanzi2Pinyin(unit);
				if(isFull){
					sb.append(pinyin);
				}
				else{
					sb.append(pinyin.charAt(0));
				}
			}else{
				sb.append(unit);
			}
		}
		return sb.toString();
	}

	/**
	 * 将单个汉字转成拼音
	 *
	 * @param hanzi 汉字字符
	 *
	 * @return 拼音
	 */
	private static String convertSingleHanzi2Pinyin(char hanzi){
		HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
		outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		String[] res;
		StringBuffer sb=new StringBuffer();
		try {
			res = PinyinHelper.toHanyuPinyinStringArray(hanzi,outputFormat);
			sb.append(res[0]);//对于多音字，只用第一个拼音
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return sb.toString();
	}

	/***
	 * 匹配
	 * <P>
	 * 根据字符和正则表达式进行匹配
	 *
	 * @param str 源字符串
	 * @param regex 正则表达式
	 *
	 * @return true：匹配成功  false：匹配失败
	 */
	private static boolean match(String str,String regex){
		Pattern pattern=Pattern.compile(regex);
		Matcher matcher=pattern.matcher(str);
		return matcher.find();
	}
}
