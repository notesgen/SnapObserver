package com.notesgenlab.snapobserver.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.notesgenlab.snapobserver.OnSnapTakenListener;
import com.notesgenlab.snapobserver.SnapperContentObserver;

public class MainActivity extends AppCompatActivity implements OnSnapTakenListener{

    private SnapperContentObserver snapContent;
    private String TAG = "SnapObserverSample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatCheckBox checkBox = (AppCompatCheckBox) findViewById(R.id.checkbox);
        snapContent = new SnapperContentObserver(this,this);

    }

    @Override
    public void onSnapTaken(String path) {
        Log.d(TAG,path==null?"Snap Deleted":"SnapPath : "+path);
    }

    @Override
    public void onError(String message) {
        Log.d(TAG,message);
    }


    @Override
    protected void onResume() {
        super.onResume();
        snapContent.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        snapContent.stop();
    }

    public void checkBoxClicked(View view) {
        /**
         * Delete snap automatically
         */
        snapContent.deleteSnapshot(((AppCompatCheckBox)view).isChecked());
    }
}
