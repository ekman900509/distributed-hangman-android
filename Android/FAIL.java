package kth.hangman_3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FAIL extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fail);

        Button back = (Button) findViewById(R.id.button5);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(FAIL.this, Start_Screen.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                FAIL.this.startActivity(myIntent);
            }
        });
    }
}
