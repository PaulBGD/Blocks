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
