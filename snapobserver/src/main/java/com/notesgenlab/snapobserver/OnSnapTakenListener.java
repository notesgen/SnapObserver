package com.notesgenlab.snapobserver;


/**
 * Created by Farhan on 5/29/2017.
 */

public interface OnSnapTakenListener {
    void onSnapTaken(String path);
    void onError(String message);
}
