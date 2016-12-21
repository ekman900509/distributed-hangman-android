package kth.hangman_3;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;



public class Communication_Thread extends AsyncTask<String, Void, Long> {

    //private static final long serialVersionUID = 10045L;
    private String ip;
    private String port_string;
    private int port;
    private Socket socket;
    private BufferedInputStream input;
    private BufferedOutputStream output;
    private static final int SERVER_PORT = 4444;
    private LinkedBlockingQueue<String> outgoingMessages = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<String> incomingMessages = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<String> scoreMessage = new LinkedBlockingQueue<>();
    private int read_bytes;
    private final int RECEIVING_SIZE = 32;
    private byte[] received_message_array = new byte[RECEIVING_SIZE];
    private byte[] received_guess;
    private int flag_byte, last_byte;
    private String received_string, received_flag, received_last;
    private Activity activity;
    private TextView dashlabel;
    private TextView scoreLabel;
    private TextView attemptsLabel;
    private Connection_Thread connection_thread;

    public Communication_Thread(Activity activity){
        this.activity = activity;
    }

    @Override
    protected Long doInBackground(String... params){
        //System.err.println("KOM JAG HIT?");
        long ret = 0;
        ip = params[0];
        port_string = params[1];
        port = Integer.parseInt(port_string);
        //System.out.println(ip + "\t" + port);
        //System.out.println("HIT DÅ?");
        try {
            //System.out.println("KANSKE HÄR?");
            //"130.229.190.204"
            socket = new Socket(ip, SERVER_PORT);
            //System.err.println("I MADE IT");
        } catch (Exception e){
            //System.out.println("Socket ERROR");
            ret = -1;
            return ret;
        }



        try {
            input = new BufferedInputStream(socket.getInputStream());
        } catch (IOException ioe2) {
            System.out.println("Couldn't create inputstream");
            ret = -1;
            return ret;
        }
        try {
            output = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException ioe3) {
            System.out.println("Couldn't create outputstream");
            ret = -1;
            return ret;
        }

        System.out.println("Readers up and running!");

        while(true){
            try{
                //System.out.println("LETS GO AGAIN");
                System.out.println("AVAILABLE: "+input.available());

                read_bytes = input.read(received_message_array, 0, RECEIVING_SIZE);

                //System.out.println("READ BYTES: "+read_bytes);
                received_guess = new byte[read_bytes-2];
                flag_byte = received_message_array[read_bytes-2];
                last_byte = received_message_array[read_bytes-1];
                for(int i = 0; i < read_bytes-2; i++){
                    received_guess[i] = received_message_array[i];
                }
                received_string = new String(received_guess);

                System.out.println("RECEIVED_STRING: " + received_string);
                addToIncomingMessages(received_string);
                checkFlag(flag_byte, last_byte);
                System.out.println("LIST#1a: "+incomingMessages.toString());
                onProgressUpate();
                updateScore();
                System.out.println("LIST#1b: "+incomingMessages.toString());

                String guess = getFromOutgoingMessages();
                System.out.println("SENDING STRING: "+guess);
                output.write(guess.getBytes());
                output.flush();

                //System.out.println("Preparing outgoing message...");
                //gui_thread.updateDashesToGUI(received_string);
            } catch (IOException ioe){
                connection_thread.interrupt();
                Intent fail = new Intent(activity,FAIL.class);

                activity.startActivity(fail);
                break;
            }

        }




        return ret;
    }

    protected void onPostExecute(){

    }

    public void addToOutgoingMessages(String message){
        outgoingMessages.add(message);
    }

    private String getFromOutgoingMessages(){
        String message = null;
        try{
            message = outgoingMessages.take();
        } catch (InterruptedException ie){
            ie.printStackTrace();
        }
        return message;
    }

    private void addToIncomingMessages(String message) {
        incomingMessages.add(message);
    }

    public String getFromIncomingMessages(){
        String message = null;
        try{
            System.out.println("LIST 1c: "+incomingMessages.toString());
            message = incomingMessages.take();
            System.out.println("MESSAGE: "+message);
        } catch (Exception ie){
            ie.printStackTrace();
        }
        return message;
    }

    private void addToScoreMessage(String message){
        scoreMessage.add(message);
    }

    public String getFromScoreMessage(){
        String message = null;
        try{
            message = scoreMessage.poll();
        } catch(Exception e){
            e.printStackTrace();
        }
        return message;
    }


    private void checkFlag(int flag, int last_byte){
        if(flag == 2) continueRound(last_byte);
        else finishRound(flag, last_byte);
    }

    private void continueRound(int last_byte){
        String attempts = "Attempts: "+last_byte;
        addToIncomingMessages(attempts);

        /*
        gui_thread.updateAttemptsToGUI(last_byte);
        String wordFromQueue = queue.take();
        received_guess = wordFromQueue.getBytes();
        out.write(received_guess);
        out.flush();
        */
    }

    private void finishRound(int flag, int last_byte){
        String attempts = "Attempts: 0";
        addToIncomingMessages(attempts);
        String score = "Score: "+last_byte;
        addToScoreMessage(score);


        /*
        gui_thread.updateScoreToGUI(last_byte);
        gui_thread.updateAttemptsToGUI(0);
        int answer = (flag == 0) ?
                showMessage("Game over! Do you want to play again?") :
                showMessage("Congratulations! Do you want to play again?");
        if(answer == 1){
            out.write(new String("End Game").getBytes());
            out.flush();
            System.exit(1);
        }
        out.write(new String("New Game").getBytes());
        out.flush();
        gui_thread.newGame();
        */
    }



    @Override
    protected void onPreExecute(){
        dashlabel = (TextView)activity.findViewById(R.id.textView7);
        scoreLabel = (TextView)activity.findViewById(R.id.textView4);
        attemptsLabel = (TextView)activity.findViewById(R.id.textView5);
        connection_thread = new Connection_Thread();
        connection_thread.start();
    }

    protected void onProgressUpate(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                dashlabel.setText(getFromIncomingMessages());
                attemptsLabel.setText(getFromIncomingMessages());
            }
        });
    }

    protected void updateScore(){
        activity.runOnUiThread(new Runnable(){
            @Override
            public void run(){
                String score = getFromScoreMessage();
                if(score != null)
                    scoreLabel.setText(score);
            }
        });
    }

    private class Connection_Thread extends Thread{

        @Override
        public void run(){
            try{
                while(!isInterrupted()){

                    sleep(1000);
                    if(new TestSocket().execute(ip, port_string).get() == -1){
                        System.out.println("ERROR CONNECTING......");
                        Intent fail = new Intent(activity,FAIL.class);

                        activity.startActivity(fail);

                    }
                }

            } catch(InterruptedException ie){
                ie.printStackTrace();
            } catch (ExecutionException ee){
                ee.printStackTrace();
            }
        }
    }
}
