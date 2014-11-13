package com.rbi.speak.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ron on 13/11/14.
 */
public class WidgetFactory {

    private static Map<Context,View> alertViews;

    static{
        alertViews = new HashMap<Context, View>();
    }

    public static AlertDialog.Builder getEditTextAlertDialog(Context context,String defaultText){

        return getEditTextAlertDialog(context,defaultText,InputType.TYPE_CLASS_TEXT);
    }

    public static AlertDialog.Builder getEditTextAlertDialog(Context context,String defaultText,int inputType){

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        EditText input = new EditText(context);
        input.setInputType(inputType);
        input.setText(defaultText);
        alert.setView(input);

        alertViews.put(context,input);

        return alert;
    }

    public static View getDialogView(Context context){

        View ret = alertViews.get(context);
        alertViews.remove(context);
        return ret;
    }

    public static String getDialogTextInput(Context context){

        return ((EditText)getDialogView(context)).getText().toString();
    }
}
