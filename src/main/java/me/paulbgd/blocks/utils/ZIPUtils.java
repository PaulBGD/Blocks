/*
 * COPYRIGHT AND PERMISSION NOTICE
 *
 * Copyright (c) 2014, PaulBGD, <paul@paulbgd.me>.
 *
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software for any purpose
 * with or without fee is hereby granted, provided that the above copyright
 * notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Except as contained in this notice, the name of a copyright holder shall not
 * be used in advertising or otherwise to promote the sale, use or other dealings
 * in this Software without prior written authorization of the copyright holder.
 */

package me.paulbgd.blocks.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import net.minecraft.util.org.apache.commons.io.IOUtils;

public class ZIPUtils {

    public static void writeZip(HashMap<InputStream, String> hashMap, OutputStream outputStream) throws IOException {
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        for (Map.Entry<InputStream, String> entry : hashMap.entrySet()) {
            if (entry.getKey().available() == 0) {
                // empty array
                continue;
            }
            zipOutputStream.putNextEntry(new ZipEntry(entry.getValue()));
            IOUtils.copy(entry.getKey(), zipOutputStream);
            IOUtils.closeQuietly(entry.getKey());
            zipOutputStream.closeEntry();
        }
        zipOutputStream.setComment("json");
        zipOutputStream.close();
    }

    public static HashMap<String, String> readZip(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[2048];
        HashMap<String, String> list = new HashMap<>();
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry entry = zipInputStream.getNextEntry();
        while (entry != null) {
            if (!entry.isDirectory()) {
                StringBuilder stringBuilder = new StringBuilder();
                while (IOUtils.read(zipInputStream, buffer) > 0) {
                    stringBuilder.append(new String(buffer, "UTF-8"));
                }
                list.put(stringBuilder.toString(), entry.getName());
            }
            zipInputStream.closeEntry();
            entry = zipInputStream.getNextEntry();
        }
        zipInputStream.closeEntry();
        zipInputStream.close();
        return list;
    }

}
