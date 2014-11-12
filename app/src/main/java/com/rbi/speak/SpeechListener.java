package com.rbi.speak;

import java.util.List;

/**
 * Created by ron on 06/11/14.
 */
public interface SpeechListener {

    public void speakingStatusChanged(boolean status);

    public void listeningStatusChanged(boolean status);

    public boolean resultsReady(List<String> heard, float[] scores, boolean partial);
}
