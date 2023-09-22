/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/11/26 15:18
 */
public class PinYin4jUtils {

    public static String getAllPinyin(String hanzi) {
        //Output formatting
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        /**
         * Output case settings
         *
         * LOWERCASE:Output lowercase
         * UPPERCASE:Output uppercase
         */
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);

        /**
         * Output phonetic transcription settings
         *
         * WITH_TONE_MARK:Directly with phonetic symbols (WITH U_UNICODE must be set, otherwise an exception will be thrown)
         * WITH_TONE_NUMBER：Numbers 1-4 indicate phonetic transcription
         * WITHOUT_TONE：There is no phonetic transcription
         */
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        /**
         * Special phonetic transcription U setting
         *
         * WITH_V：Use v to indicate ü
         * WITH_U_AND_COLON：Use "u:" for ü
         * WITH_U_UNICODE：Use ü directly
         */
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

        char[] hanYuArr = hanzi.trim().toCharArray();
        StringBuilder pinYin = new StringBuilder();

        try {
            for (int i = 0, len = hanYuArr.length; i < len; i++) {
                //Whether the match is a Chinese character or not
                if (Character.toString(hanYuArr[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    //If it is a polyphonetic word, return multiple pinyin, here only the first one is taken
                    String[] pys = PinyinHelper.toHanyuPinyinStringArray(hanYuArr[i], format);
                    pinYin.append(pys[0]).append("");
                } else {
                    pinYin.append(hanYuArr[i]).append("");
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }
        return pinYin.toString();
    }
}
