package com.rbi.speak;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


/**
 * Created by ron on 05/11/14.
 */
public class ExampleSpeechActivity extends Activity implements View.OnClickListener, SpeechListener {

    protected TextView infoText;
    private Button speakButton;

    private SpeechRecognitionAdapter sl;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        infoText = (TextView) findViewById(R.id.textView);
        speakButton = (Button) findViewById(R.id.button);
        speakButton.setOnClickListener(this);
        speakButton.setText("start");

        sl = new SpeechRecognitionAdapter(this,this);

    }

    @Override
    protected void onPause() {
        super.onPause();

        sl.stopRecognition();
    }

    public void onClick(View v) {

        if(!sl.isListening()){
            sl.startRecognition(3); // -1 for continuous
        }
        else{
            sl.stopRecognition();
        }
    }

    public void toast(final String message){

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ExampleSpeechActivity.this,message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    //------------------------------------------------------+
    // SpeechListener

    @Override
    public void speakingStatusChanged(boolean status) {

        this.toast( (status) ? "speaking" : "speech ended"  );
    }

    @Override
    public void listeningStatusChanged(boolean status) {

        speakButton.setText( (status) ? "Stop" : "Start" );
    }

    @Override
    public boolean resultsReady(List<String> heard, float[] scores, boolean partial) {

        if(heard !=null && heard.size()>0){
            infoText.setText(heard.get(0));
        }
        else{
            infoText.setText("No speech detected");
        }

        return true;
    }

    //------------------------------------------------------+
}
