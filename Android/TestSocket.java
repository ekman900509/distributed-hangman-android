package kth.hangman_3;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.LinkedBlockingQueue;


public class TestSocket extends AsyncTask<String, Void, Long> /*implements Serializable*/{

    //private static final long serialVersionUID = 10045L;
    private String ip;
    private int port;
    private Socket socket;
    private static final int SERVER_PORT = 4444;


    protected Long doInBackground(String... params) {
        long ret = 0;
        String ip = params[0];
        int port = Integer.parseInt(params[1]);
        System.out.println("TESTING SOCKET...");
        try {
            //socket = new Socket(ip, port);
            socket = new Socket();
            SocketAddress addr = new InetSocketAddress(ip, port);
            socket.connect(addr, 3000);
            socket.close();
        } catch (Exception e) {
            System.out.println("SOCKET ERROR");
            ret = -1;
            return ret;
        }
        System.out.println("DONE...");
        return ret;
    }
}
