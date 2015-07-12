package com.android.settings.vrtoxin;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;

import com.android.settings.vrtoxin.views.GifWebView;

public class HiddenAnimActivity2 extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InputStream stream = null;
        try {
            stream = getAssets().open("bug.gif");
        } catch (IOException e) {
            e.printStackTrace();
        }

        GifWebView view = new GifWebView(this, "file:///android_asset/bug.gif");

        setContentView(view);
    }
}
