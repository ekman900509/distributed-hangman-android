package kth.hangman_3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;


public class Start_Screen extends AppCompatActivity{


    private boolean notWorking;
    private int x;
    private TextView ip_field, port_field;
    private String ip, port;
    private LinkedBlockingQueue<Boolean> queue = new LinkedBlockingQueue<>();
    private TextView connecting;
    public Start_Screen(){

        notWorking = true;
        x = 0;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start__screen);

        ip_field = (TextView) findViewById(R.id.editText);
        port_field = (TextView) findViewById(R.id.editText2);



        connecting = (TextView)findViewById(R.id.textView10);
        final Button connect = (Button) findViewById(R.id.button);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

/*
                Thread thread = new Thread(new Communication_Thread());
                thread.start();
*/

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromInputMethod(port_field.getWindowToken(), 0);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                connecting.setText("Connecting...");
                ip = ip_field.getText().toString();
                port = port_field.getText().toString();
                (new Test_Thread(Start_Screen.this)).start();

            }
        });







    }

    @Override
    protected void onStart(){
        super.onStart();
        System.out.println("START");
    }

    @Override
    protected void onResume(){
        super.onResume();
        System.out.println("RESUME");
        /*
        try{
            Boolean answer = queue.take();
            if(!answer)
                connecting.setText("FAILED");
            else{
                Intent myIntent = new Intent(Start_Screen.this, GAME.class);
                myIntent.putExtra("IP",ip);
                myIntent.putExtra("PORT",port);
                System.out.println("START GAME");
                Start_Screen.this.startActivity(myIntent);
            }
        } catch (InterruptedException ie){
            ie.printStackTrace();
        }
        */
    }


    private class Test_Thread extends Thread{

        private Activity activity;
        public Test_Thread(Activity activity){
            this.activity = activity;
        }

        @Override
        public void run(){
            try {

                if(!(ip == null || ip.equals("")) && !(port == null || port.equals(""))) {

                    if (new TestSocket().execute(ip, port).get() == -1) {
                        queue.add(false);
                        System.out.println("FAILED");
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                connecting.setText("FAILED");
                            }
                        });

                    } else {
                        Intent myIntent = new Intent(Start_Screen.this, GAME.class);
                        myIntent.putExtra("IP",ip);
                        myIntent.putExtra("PORT",port);
                        System.out.println("START GAME");
                        Start_Screen.this.startActivity(myIntent);
                    }
                }
            }catch(Exception e){

                e.printStackTrace();
            }
            System.out.println("THREAD DONE!!");
        }
    }



}
