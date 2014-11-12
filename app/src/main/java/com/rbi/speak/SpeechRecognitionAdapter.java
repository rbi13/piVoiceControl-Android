package com.rbi.speak;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ron on 05/11/14.
 */
public class SpeechRecognitionAdapter implements RecognitionListener {

    Context context;
    private SpeechRecognizer sr;
    private SpeechListener listener;
    private Set<RecognitionListener> listeners;

    private Mutable<Boolean> listening, speaking;
    List<String> heard;
    float[] scores;

    int requestedAttempts, currentAttempt;

    public SpeechRecognitionAdapter(Context context, SpeechListener listener){

        this.context = context;
        this.listener = listener;
        listening = new Mutable<Boolean>(false);
        speaking = new Mutable<Boolean>(false);

        listeners = new HashSet<RecognitionListener>();
    }

    public void startRecognition(int attempts){

        requestedAttempts = currentAttempt = attempts;
        startRecognition();
    }

    private void startRecognition(){

        if(sr == null){
            sr = SpeechRecognizer.createSpeechRecognizer(context);
            sr.setRecognitionListener(this);
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getApplicationContext().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,100);

        sr.startListening(intent);
    }

    public void stopRecognition(){

        changeStatus(speaking, false);
        changeStatus(listening, false);

        if (sr != null) {
            sr.stopListening();
            sr.cancel();
            sr.destroy();
        }
        sr = null;
    }

    private void restartRecognition(boolean wasError){

        if(--currentAttempt ==0){
            stopRecognition();
            return;
        }

        if(wasError && sr!=null){
            sr.stopListening();
            sr.cancel();
            sr.destroy();
            sr = null;
        }
        startRecognition();
    }

    public boolean isListening() {
        return listening.value;
    }

    public boolean isSpeaking(){
        return speaking.value;
    }

    public void addRawListener(RecognitionListener l){
        listeners.add(l);
    }

    public void removeRawListener(RecognitionListener l){
        listeners.remove(l);
    }

    //---------------------------------------------------------+
    // Speech Recognizer

    public void onReadyForSpeech(Bundle params) {

        changeStatus(listening, true);
        for(RecognitionListener l : listeners){
            l.onReadyForSpeech(params);
        }
    }

    public void onBeginningOfSpeech() {

        changeStatus(speaking, true);
        for(RecognitionListener l : listeners){
            l.onBeginningOfSpeech();
        }
    }

    public void onRmsChanged(float rmsdB) {

        for(RecognitionListener l : listeners){
            l.onRmsChanged(rmsdB);
        }
    }

    public void onBufferReceived(byte[] buffer) {

        for(RecognitionListener l : listeners){
            l.onBufferReceived(buffer);
        }
    }

    public void onEndOfSpeech() {

        changeStatus(speaking, false);
        for(RecognitionListener l : listeners){
            l.onEndOfSpeech();
        }
    }

    public void onError(int error) {

        changeStatus(speaking, false);

        switch (error){
            case SpeechRecognizer.ERROR_NO_MATCH :
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT :
                restartRecognition(true);
                break;
            default:
                stopRecognition();
        }

        for(RecognitionListener l : listeners){
            l.onError(error);
        }
    }

    public void onResults(Bundle results) {

        boolean runAgain = false;
        if( receiveResults(results) ){
            runAgain = listener.resultsReady(heard,scores,false);
        }

        for(RecognitionListener l : listeners){
            l.onResults(results);
        }

        if(runAgain){
            restartRecognition(false);
        }
        else{
            stopRecognition();
        }
    }

    public void onPartialResults(Bundle partialResults) {

        if( receiveResults(partialResults) ){
            listener.resultsReady(heard, scores, true);
        }

        for(RecognitionListener l : listeners){
            l.onPartialResults(partialResults);
        }
    }

    public void onEvent(int eventType, Bundle params) {

        for(RecognitionListener l : listeners){
            l.onEvent(eventType,params);
        }
    }

    private boolean receiveResults(Bundle results){

        if ((results != null)
                && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION)){
            heard = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            scores = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);

            return true;
        }

        return false;
    }

    private <E> boolean changeStatus(Mutable<E> status, E update){

        if(listener !=null && status.changeValue(update)){
            if(status == listening){
                listener.listeningStatusChanged((Boolean)update);
            }
            else if(status == speaking){
                listener.speakingStatusChanged((Boolean)update);
            }
            return true;
        }
        return false;
    }

    //---------------------------------------------------------+
    // Helper classes
    private class Mutable<E>{

        private E value;

        public Mutable(E value){
            this.value = value;
        }

        public boolean changeValue(E desired){

            if(desired != this.value){
                this.value = desired;
                return true;
            }

            return false;
        }
    }

    //---------------------------------------------------------+
}
