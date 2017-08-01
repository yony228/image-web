package com.test.demo.common;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.*;
import java.util.*;

/**
 * Created by Administrator on 2017/6/23.
 */
public class ZipUtil {
    private static final int buffer = 2048;

    public static void unZip(String path) {
        int count = -1;
        String savePath = "";

        File file = null;
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        savePath = path.substring(0, path.lastIndexOf(".")) + File.separator;//保存解压文件目录
        new File(savePath).mkdir();
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(path, "gbk");
            Enumeration<?> entries = zipFile.getEntries();

            while (entries.hasMoreElements()) {
                byte buf[] = new byte[buffer];
                ZipArchiveEntry entry = (ZipArchiveEntry) entries.nextElement();

                String filename = entry.getName();
                boolean ismkdir = false;
                if (filename.lastIndexOf("/") != -1) {//检查此文件是否带有文件夹
                    ismkdir = true;
                }
                filename = savePath + filename;

                if (entry.isDirectory()) {//如果有文件夹先创建
                    file = new File(filename);
                    file.mkdirs();
                    continue;
                }
                file = new File(filename);
                if (!file.exists()) {
                    if (ismkdir) {
                        new File(filename.substring(0, filename.lastIndexOf("/"))).mkdirs();
                    }
                }
                file.createNewFile();

                is = zipFile.getInputStream(entry);
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos, buffer);

                while ((count = is.read(buf)) > -1) {
                    bos.write(buf, 0, count);
                }

                bos.flush();
                bos.close();
                fos.close();
                is.close();
            }

            zipFile.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bos != null) bos.close();
                if (fos != null) fos.close();
                if (is != null) is.close();
                if (zipFile != null) zipFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
