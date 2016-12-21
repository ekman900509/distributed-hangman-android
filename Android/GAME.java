package kth.hangman_3;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

public class GAME extends AppCompatActivity{

    private Communication_Thread ct;
    private final LinkedList<String> guessed_strings = new LinkedList<String>();
    private String ip_addr, port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ct = new Communication_Thread(this);
        Button guess = (Button) findViewById(R.id.button2);
        final EditText guess_field = (EditText) findViewById(R.id.editText3);
        guess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String guessed_string = guess_field.getText().toString();
                if (guessed_string.length() > 0) {
                    if(!guessed_strings.contains(guessed_string)) {
                        ct.addToOutgoingMessages(guess_field.getText().toString());
                        guessed_strings.addLast(guessed_string);
                        addToGuessedStringsLabel(guessed_string);
                    }
                    guess_field.setText("");
                }
            }
        });

        Button newGame = (Button) findViewById(R.id.button3);
        newGame.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String newGameMessage = "New Game";
                ct.addToOutgoingMessages(newGameMessage);
                guessed_strings.clear();
                clearGuessedStringsLabel();
            }
        });

        Button exitGame = (Button) findViewById(R.id.button4);
        exitGame.setOnClickListener(new View.OnClickListener(){
            @Override
            @TargetApi(16)
            public void onClick(View v){
                GAME.this.finishAffinity();
            }
        });

        Bundle bundle = getIntent().getExtras();
        ip_addr =  bundle.getString("IP");
        port = bundle.getString("PORT");
        System.out.println("GUI ready");
        try{
            System.out.println("HEJ");
/*
            if(new TestSocket().execute(ip_addr, port).get() == -1){
                System.out.println("ERROR IN GUI");
                Intent fail = new Intent(GAME.this,FAIL.class);
                GAME.this.startActivity(fail);

            }
            else {*/
                System.out.println("Socket checked....DONE!");
                //System.out.println("CREATING CHECKING COMMUNICATION THREAD...");
                //createCheckingConnectionThread();
                //System.out.println("EXECUTING COMMUNICATION THREAD.... ");
                ct.execute(ip_addr, port);

               /* while(true){
                    try{
                        Thread.sleep(1000);
                        if(new TestSocket().execute(ip_addr,port).get() == -1){
                            System.out.println("CONNECTION INTERRUPTED...");
                            Intent fail = new Intent(GAME.this, FAIL.class);
                            GAME.this.startActivity(fail);
                        }
                        System.out.println("Conenction still up... ");
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    } catch (ExecutionException ee){
                        ee.printStackTrace();
                    }
                }
                */
            //}
        }catch(Exception e){
            System.err.println(e);
            Intent fail = new Intent(GAME.this,FAIL.class);
            GAME.this.startActivity(fail);
        }
/*
        TextView dashed_word = (TextView) findViewById(R.id.editText4);
        dashed_word.setText(received_dashed_string);
*/
    }

    private void createCheckingConnectionThread(){
        Thread thread = new Thread(){
          @Override
          public void run(){
              try{
                  while(true){
                      sleep(1000);
                      System.out.println("CHECKING CONNECTION...");
                      TestSocket test = new TestSocket();
                      System.out.println("TEST SOCKET CLASS CREATED... ");
                      AsyncTask task = test.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ip_addr, port);
                      System.out.println("Task executed? "+task.getStatus().toString());

                      if(task.get() == -1){

                          System.out.println("CONNECTION INTERRUPTED...");
                          Intent fail = new Intent(GAME.this, FAIL.class);
                          GAME.this.startActivity(fail);
                      }
                        System.out.println("Conenction still up... ");
                  }
              } catch (InterruptedException ie){
                    ie.printStackTrace();
              } catch (ExecutionException ee){
                    ee.printStackTrace();
              }
              System.out.println("SHOULDN'T COME HERE...");
          }
        };
        thread.start();
    }

    private void addToGuessedStringsLabel(String string){
        TextView guessedLabel = (TextView)findViewById(R.id.textView8);
        String text = guessedLabel.getText().toString();
        text += (string + ", ");
        guessedLabel.setText(text);
    }

    private void clearGuessedStringsLabel(){
        TextView guessedLabel = (TextView) findViewById(R.id.textView8);
        guessedLabel.setText("Guessed:");
    }
}
