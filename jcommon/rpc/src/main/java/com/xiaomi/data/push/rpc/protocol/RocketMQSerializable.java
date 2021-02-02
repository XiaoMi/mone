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

package com.xiaomi.data.push.rpc.protocol;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * @author goodjava@qq.com
 */
public class RocketMQSerializable {


    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");


    public static byte[] mapSerialize(HashMap<String, String> map) {
        // keySize+key+valSize+val
        // keySize+key+valSize+val
        if (null == map || map.isEmpty()) {
            return null;
        }

        int totalLength = 0;
        int kvLength;
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (entry.getKey() != null && entry.getValue() != null) {
                kvLength =
                        // keySize + Key
                        2 + entry.getKey().getBytes(RemotingSerializable.CHARSET_UTF8).length
                                // valSize + val
                                + 4 + entry.getValue().getBytes(RemotingSerializable.CHARSET_UTF8).length;
                totalLength += kvLength;
            }
        }

        ByteBuffer content = ByteBuffer.allocate(totalLength);
        byte[] key;
        byte[] val;
        it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (entry.getKey() != null && entry.getValue() != null) {
                key = entry.getKey().getBytes(RemotingSerializable.CHARSET_UTF8);
                val = entry.getValue().getBytes(RemotingSerializable.CHARSET_UTF8);

                content.putShort((short) key.length);
                content.put(key);

                content.putInt(val.length);
                content.put(val);
            }
        }

        return content.array();
    }

    private static int calTotalLen(int remark, int ext) {
        // int code(~32767)
        int length = 2
                // LanguageCode language
                + 1
                // int version(~32767)
                + 2
                // int opaque
                + 4
                // int flag
                + 4
                // String remark
                + 4 + remark
                // HashMap<String, String> extFields
                + 4 + ext;

        return length;
    }

    public static RemotingCommand rocketMQProtocolDecode(final byte[] headerArray) {
        RemotingCommand cmd = new RemotingCommand();
        ByteBuffer headerBuffer = ByteBuffer.wrap(headerArray);
        // int code(~32767)
        cmd.setCode(headerBuffer.getShort());
        // int version(~32767)
        cmd.setVersion(headerBuffer.getShort());
        // int opaque
        cmd.setOpaque(headerBuffer.getInt());
        // int flag
        cmd.setFlag(headerBuffer.getInt());
        // String remark
        int remarkLength = headerBuffer.getInt();
        if (remarkLength > 0) {
            byte[] remarkContent = new byte[remarkLength];
            headerBuffer.get(remarkContent);
            cmd.setRemark(new String(remarkContent, RemotingSerializable.CHARSET_UTF8));
        }

        // HashMap<String, String> extFields
        int extFieldsLength = headerBuffer.getInt();
        if (extFieldsLength > 0) {
            byte[] extFieldsBytes = new byte[extFieldsLength];
            headerBuffer.get(extFieldsBytes);
            cmd.setExtFields(mapDeserialize(extFieldsBytes));
        }
        return cmd;
    }

    public static HashMap<String, String> mapDeserialize(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        short keySize = 0;
        byte[] keyContent = null;
        int valSize = 0;
        byte[] valContent = null;
        while (byteBuffer.hasRemaining()) {
            keySize = byteBuffer.getShort();
            keyContent = new byte[keySize];
            byteBuffer.get(keyContent);

            valSize = byteBuffer.getInt();
            valContent = new byte[valSize];
            byteBuffer.get(valContent);

            map.put(new String(keyContent, RemotingSerializable.CHARSET_UTF8), new String(valContent,
                    RemotingSerializable.CHARSET_UTF8));
        }
        return map;
    }


    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
