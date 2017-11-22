package com.web.image.controller;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/6/29.
 */
@ServerEndpoint(value = "/log")
public class LogWebSocketHandle {
    private Process process;
    private InputStream inputStream;

    @OnOpen
    public void onOpen(Session session) {
        try {
            System.out.println("web");

            process = Runtime.getRuntime().exec("tail -f /home/web/temp.txt");
            inputStream = process.getInputStream();
//
//            TailLogThread thread = new TailLogThread(inputStream, session);
//            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (process != null) {
            process.destroy();
        }
    }

    @OnError
    public void onError(Throwable thr) {
        thr.printStackTrace();
    }

//    public static void main(String[] args) throws IOException {
//        TailLogThread tailLogThread = new TailLogThread();
//        final File file = new File("D:\\home\\web\\log\\spring.log.1");
//        tailLogThread.realtimeShowLog(file);
//    }
}
