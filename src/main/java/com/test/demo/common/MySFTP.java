package com.test.demo.common;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by Administrator on 2017/5/17.
 */
public class MySFTP {
    public ChannelSftp connect(String host, int port, String username, String password) {
        ChannelSftp sftp = null;
        try {
            JSch jSch = new JSch();
            jSch.getSession(username, host, port);
            Session sshSession = jSch.getSession(username, host, port);
            System.out.println("Session created");
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            //sshConfig.put("StricHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            System.out.println("Session connected");
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            System.out.println("Connected to ");
        } catch (Exception e) {
            System.out.println("error" + e.getMessage());
        }
        return sftp;
    }

    public void upload(String directory, String uploadFile, ChannelSftp sftp) {
        try {
            sftp.cd(directory);
            File file = new File(uploadFile);
            sftp.put(new FileInputStream(file), file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        MySFTP sf = new MySFTP();
        String host = "192.168.2.60";
        int port = 22;
        String username = "root";
        String password = "das115";

        ChannelSftp sftp = sf.connect(host, port, username, password);
        sf.upload("/root", "D:\\tulips.jpg", sftp);


//        String key = "D:\\pic";
//        JSch jsch = new JSch();
//        jsch.addIdentity(key,"pass");
//        jsch.setKnownHosts("D:\\pic");
//        Session session = jsch.getSession("root","192.168.2.60",22);
//        session.setPassword("das115");
//        session.connect();
    }
}
