package com.web.image.common;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/6/29.
 */
//public class TailLogThread extends Thread {
//    private BufferedReader reader;
//    private Session session;
//
//    public TailLogThread(InputStream in, Session session) {
//        this.reader = new BufferedReader(new InputStreamReader(in));
//        this.session = session;
//    }
//
//    @Override
//    public void run() {
//        String line;
//        try {
//            while ((line = reader.readLine()) != null) {
//                session.getBasicRemote().sendText(line + "<br>");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}

public class TailLogThread {
    private long lastTimeFileSize = 0;

    public void realtimeShowLog(File file) throws IOException {
        final RandomAccessFile randomFile = new RandomAccessFile(file, "r");//只读 rw:读写
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

        exec.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    randomFile.seek(lastTimeFileSize);
                    String tmp = "";
                    while ((tmp = randomFile.readLine()) != null) {
                        System.out.println(new String(tmp.getBytes("utf-8")));
                    }
                    lastTimeFileSize = randomFile.length();

                    System.out.println(lastTimeFileSize);
                } catch (IOException e) {
                    throw new RuntimeException();
                }
            }
        }, 0, 3, TimeUnit.SECONDS);
    }
}
