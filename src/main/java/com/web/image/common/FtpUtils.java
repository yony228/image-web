package com.web.image.common;


import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/5/17.
 */
public class FtpUtils {
    private FTPClient ftp;

    public boolean connect(String path, String addr, int port, String username, String password) throws Exception {
        boolean result = false;
        ftp = new FTPClient();
        ftp.setControlEncoding("GBK");
        int reply;
        ftp.connect(addr, port);
        ftp.login(username, password);
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return result;
        }
        result = ftp.changeWorkingDirectory(path);

        return result;
    }

    public void upload(File file) throws Exception {
        if (file.isDirectory()) {
            ftp.makeDirectory(file.getName());
            ftp.changeWorkingDirectory(file.getName());
            String[] files = file.list();
            for (int i = 0; i < files.length; i++) {
                File file1 = new File(file.getPath() + "\\" + files[i]);

                if (file1.isDirectory()) {
                    upload(file1);
                    ftp.changeToParentDirectory();
                } else {
                    File file2 = new File(file.getPath() + "\\" + files[i]);
                    FileInputStream input = new FileInputStream(file2);
                    ftp.storeFile(file2.getName(), input);
                    input.close();
                }
            }
        } else {
            try {
                File file2 = new File(file.getPath());
                FileInputStream input = new FileInputStream(file2);
                ftp.storeFile(file2.getName(), input);
                input.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void closeFtp() {
        if (ftp.isConnected()) {
            try {
                ftp.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void changeWorkingDirectory(String path) {
        if (ftp.isConnected()) {
            try {
                ftp.changeWorkingDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("未连接");
        }
    }


    public static void main(String[] args) throws Exception {
        FtpUtils ftpUtils = new FtpUtils();
        ftpUtils.connect("", "192.168.2.60", 22, "root", "das115");

        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        File file = new File("D:\\tulips.jpg");
        ftpUtils.uploadPicture(file, date);
    }

    public void uploadPicture(File file2, String data) throws Exception {
        this.changeWorkingDirectory(data);
        this.upload(file2);
        this.closeFtp();
    }
}
