package com.teddoll.fitness.intervaltrainer.session;

/**
 * Created by teddydoll on 5/29/16.
 */
interface SessionSelectionListener {

    void onSessionSelected(long sessionId);

    void onSessionReady(long sessionId);
}
