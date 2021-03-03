/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.test.codefilter;

import java.util.ArrayList;

/**
 * Time    : 2019/8/29 下午2:15
 * Author  : zhangjunyi
 * E-mail  : 
 * Version : 1.0
 */

public class Zhangjunyi {

    public static Integer code(byte[] bodyStr) {
        ArrayList<Character> charStack = new ArrayList<Character>();

        int bodyStrLen = bodyStr.length;
        for (int i=0; i < bodyStrLen; ++i){
            if (bodyStr[i] == ' '){
                continue;
            }
            else{
                if (bodyStr[i] != '{'){
                    return Integer.valueOf(200);
                }
                else{
                    break;
                }
            }
        }
        for (int i=bodyStrLen - 1; i >= 0; --i){
            if (bodyStr[i] == ' '){
                continue;
            }
            else{
                if (bodyStr[i] != '}'){
                    return 200;
                }
                else{
                    break;
                }
            }
        }

        boolean isCode = false;
        boolean noKey = true;
        int keyIdx = 0;
        for (int i=0; i < bodyStrLen; ++i) {
            char currChar = (char) bodyStr[i];
            int charStackSize = charStack.size();

            if (currChar == '\"') {
                currChar = '\'';
            }

            if (charStackSize == 2 && charStack.get(1) == '\''){
                if (noKey == true) {
                    noKey = false;
                    isCode = true;
                    keyIdx = 0;
                }
                if (isCode == true){
                    if (currChar != '\'') {
                        switch (keyIdx) {
                            case 0:
                                if (currChar != 'c') {
                                    isCode = false;
                                }
                                break;
                            case 1:
                                if (currChar != 'o') {
                                    isCode = false;
                                }
                                break;
                            case 2:
                                if (currChar != 'd') {
                                    isCode = false;
                                }
                                break;
                            case 3:
                                if (currChar != 'e') {
                                    isCode = false;
                                }
                                break;
                            default:
                                isCode = false;
                                break;
                        }
                        ++keyIdx;
                    }
                }
            }

            if (charStackSize > 0 && charStack.get(charStackSize - 1) == '\''){
                if (bodyStr[i - 1] != '\\' && currChar == '\''){
                    charStack.remove(charStackSize - 1);
                    noKey = true;
                    if (isCode == true && keyIdx == 4){
                        int startIdx = -1;
                        int endIdx = -1;
                        for (int j=i; j < bodyStrLen; j++) {
                            char tempChar = (char) bodyStr[j];
                            if (tempChar >= '0' && tempChar <= '9') {
                                if (startIdx == -1) {
                                    startIdx = j;
                                }
                            }
                            else {
                                if (startIdx != -1) {
                                    endIdx = j;
                                    break;
                                }
                            }
                        }
                        byte[] codeByte = new byte[endIdx - startIdx];
                        System.arraycopy(bodyStr, startIdx, codeByte, 0, endIdx - startIdx);
                        return Integer.parseInt(new String(codeByte));
                    }
                }
                continue;
            }

            if (noKey == false){
                continue;
            }

            switch (currChar) {
                case '\'':
                    charStack.add(currChar);
                    break;
                case '{':
                    charStack.add(currChar);
                    break;
                case '}':
                    charStack.remove(charStackSize - 1);
                    break;
                case '[':
                    charStack.add(currChar);
                    break;
                case ']':
                    charStack.remove(charStackSize - 1);
                    break;
                default:
                    break;
            }

        }

        //没有code
        return Integer.valueOf(200);
    }

}
