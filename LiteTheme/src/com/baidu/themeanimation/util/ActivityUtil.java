
package com.baidu.themeanimation.util;

import android.app.Activity;
import android.os.Bundle;

import com.baidu.themeanimation.util.Logger;

public class ActivityUtil extends Activity {

    private String TAG = "ActivityUtil";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate");
        this.finish();
    }
}
