package kth.hangman_3;

import android.content.Context;
import android.widget.Toast;


public class FunctionalityMethods {

    private Context context;

    public FunctionalityMethods(Context context){
        this.context = context;
    }

    public void error(String e){
        context = context.getApplicationContext();
        CharSequence text = e;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


}
