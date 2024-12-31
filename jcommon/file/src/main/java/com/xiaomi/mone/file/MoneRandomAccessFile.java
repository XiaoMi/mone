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

package com.xiaomi.mone.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * This is an optimized version of the RandomAccessFile class
 *
 * @author shanwb
 * @date 2022-02-07
 */
public class MoneRandomAccessFile extends RandomAccessFile {
    /**
     * Uses a byte instead of a char buffer for efficiency reasons.
     */
    private byte[] buffer;
    private int buf_end = 0;
    private int buf_pos = 0;
    /**
     * The position inside the actual file.
     */
    private long real_pos = 0;
    /**
     * Use a 50kb buffer size as default
     */
    private final int BUF_SIZE;

    /**
     * Creates a new instance of the BufferedRandomAccessFile.
     *
     * @param filename The path of the file to open.
     * @param mode     Specifies the mode to use ("r", "rw", etc.) See the
     *                 BufferedLineReader documentation for more information.
     * @param bufSize  The buffer size (in bytes) to use.
     * @throws IOException
     */
    public MoneRandomAccessFile(String filename, String mode, int bufSize)
            throws IOException {
        super(filename, mode);
        invalidate();
        BUF_SIZE = bufSize;
        buffer = new byte[BUF_SIZE];
    }

    public MoneRandomAccessFile(File file, String mode, int bufsize)
            throws IOException {
        this(file.getAbsolutePath(), mode, bufsize);
    }

    /**
     * Reads one byte form the current position
     *
     * @return The read byte or -1 in case the end was reached.
     */
    @Override
    public final int read() throws IOException {
        if (buf_pos >= buf_end) {
            if (fillBuffer() < 0) {
                return -1;
            }
        }
        if (buf_end == 0) {
            return -1;
        } else {
            return buffer[buf_pos++] & 0xff;
        }
    }

    /**
     * Reads the next BUF_SIZE bytes into the internal buffer.
     *
     * @return
     * @throws IOException
     */
    private int fillBuffer() throws IOException {
        int n = super.read(buffer, 0, BUF_SIZE);

        if (n >= 0) {
            real_pos += n;
            buf_end = n;
            buf_pos = 0;
        }
        return n;
    }

    /**
     * Clears the local buffer.
     *
     * @throws IOException
     */
    private void invalidate() throws IOException {
        buf_end = 0;
        buf_pos = 0;
        real_pos = super.getFilePointer();
    }

    /**
     * Reads the set number of bytes into the passed buffer.
     *
     * @param b   The buffer to read the bytes into.
     * @param off Byte offset within the file to start reading from
     * @param len Number of bytes to read into the buffer.
     * @return Number of bytes read.
     */
    @Override
    public int read(byte b[], int off, int len) throws IOException {
        int leftover = buf_end - buf_pos;
        if (len <= leftover) {
            System.arraycopy(buffer, buf_pos, b, off, len);
            buf_pos += len;
            return len;
        }
        for (int i = 0; i < len; i++) {
            int c = this.read();
            if (c != -1) {
                b[off + i] = (byte) c;
            } else {
                return i;
            }
        }
        return len;
    }

    /**
     * Returns the current position of the pointer in the file.
     *
     * @return The byte position of the pointer in the file.
     */
    @Override
    public long getFilePointer() throws IOException {
        long l = real_pos;
        return (l - buf_end + buf_pos);
    }

    /**
     * Moves the internal pointer to the passed (byte) position in the file.
     *
     * @param pos The byte position to move to.
     */
    @Override
    public void seek(long pos) throws IOException {
        long n = real_pos - pos;
        if (n >= 0 && n <= buf_end) {
            buf_pos = (int) (buf_end - n);
        } else {
            super.seek(pos);
            invalidate();
        }
    }

    /**
     * Returns the next line from the file. In case no data could be loaded
     * (generally as the end of the file was reached) null is returned.
     *
     * @return The next string on the file or null in case the end of the file
     *         was reached.
     */
    public final String getNextLine() throws IOException {
        String str;
        if (buf_end - buf_pos <= 0) {
            if (fillBuffer() < 0) {
                return null;
            }
        }
        // final position of the char considering \n
        int lineEnd = -1;

        for (int i = buf_pos; i < buf_end; i++) {
            if (buffer[i] == '\n') {
                lineEnd = i;
                break;
            }
            // check for only '\r' as line end
            if ((i - buf_pos > 0) && buffer[i - 1] == '\r') {
                lineEnd = i - 1;
                break;
            }
        }

        if (lineEnd < 0) {
            StringBuffer input = new StringBuffer();
            int c = -1;
            int lastC = 0;
            int cCount = 0;
            boolean eol = false;

            while (!eol) {
                c = read();
                if (c == -1) {
                    //遇到文件末尾的场景，持续读取一段时间，缓解 异步日志输出读取到不完整行问题
                    if (cCount++ > 10) {
                        eol = true;
                    }
                } else if (c == '\n') {
                    eol = true;
                } else if (lastC == '\r') {
                    eol = true;
                } else {
                    input.append((char) c);
                    lastC = c;
                }
            }

            if ((c == -1) && (input.length() == 0)) {
                return null;
            }

            return new String(input.toString().getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
        }

        if (lineEnd > 0 && buffer[lineEnd] == '\n' && buffer[lineEnd - 1] == '\r' && lineEnd - buf_pos - 1 >= 0) {
            str = new String(buffer, buf_pos, lineEnd - buf_pos - 1, StandardCharsets.UTF_8);
        } else {
            str = new String(buffer, buf_pos, lineEnd - buf_pos, StandardCharsets.UTF_8);
        }
        buf_pos = lineEnd + 1;

        return str;
    }
}
