package com.teddoll.fitness.intervaltrainer.session;

/**
 * Created by teddydoll on 5/29/16.
 */
public interface SessionSelectionListener {

    void onSessionSelected(long sessionId);

    void onSessionReady(long sessionId);
}
