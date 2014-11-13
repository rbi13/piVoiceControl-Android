package com.rbi.speak;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rbi.speak.network.NetworkCommandFactory;
import com.rbi.speak.network.NetworkSingleton;
import com.rbi.speak.network.VolleyRequestListener;
import com.rbi.speak.storage.Logger;
import com.rbi.speak.storage.PreferencesHelper;
import com.rbi.speak.widgets.WidgetFactory;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;


/**
 * Created by ron on 13/11/14.
 */
public class NetworkSpeechActivity extends ExampleSpeechActivity implements VolleyRequestListener {

    String url;
    final String httpPrefix = "http://";
    final String httpPort = ":8888";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        url = PreferencesHelper.get(this.getApplicationContext()).getString(
                getString(R.string.saved_server_address),
                getString(R.string.default_server_address));

        this.setTitle(url);
    }

    @Override
    public boolean resultsReady(List<String> heard, float[] scores, boolean partial) {

        if(heard !=null && heard.size()>0){
            infoText.setText(heard.get(0));

            if(!partial){
                sendCommand(heard.get(0));
            }
        }
        else{
            infoText.setText("No speech detected");
        }

        return true;
    }

    public void sendCommand(String command){

        try {
            Request req =
                    NetworkCommandFactory.getVoiceCommand(command, httpPrefix + url+ httpPort, this);
            NetworkSingleton.getInstance(this).addRequest(req);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    //------------------------------------------------------+
    // Volley Listener
    @Override
    public void onErrorResponse(VolleyError error) {
        Logger.log(error.getMessage().toString());
    }

    @Override
    public void onResponse(Object response) {

        JSONObject obj = (JSONObject) response;
        Logger.log(obj.toString());
    }

    //------------------------------------------------------+
    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.server_address:

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                url = WidgetFactory.getDialogTextInput(NetworkSpeechActivity.this)
                                        .replaceAll(httpPrefix, "");
                                NetworkSpeechActivity.this.updateServerAddress();
                                break;
                        }
                    }
                };

                WidgetFactory.getEditTextAlertDialog(this, url, InputType.TYPE_CLASS_PHONE)
                        .setTitle("Server Address")
                        .setPositiveButton("Save", listener)
                        .setNeutralButton("Cancel", listener)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateServerAddress(){

        PreferencesHelper.set(
                this.getApplicationContext(),
                getString(R.string.saved_server_address),
                url);

        NetworkSpeechActivity.this.setTitle(url);
    }

    //------------------------------------------------------+
}
