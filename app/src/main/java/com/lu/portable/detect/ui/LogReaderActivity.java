package com.lu.portable.detect.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kernal.demo.plateid.R;

public class LogReaderActivity extends Activity {
    private boolean isReading = true;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_log_reader);
        final LinearLayout log_content = findViewById(R.id.tv_log_content);
        Handler handler = new Handler() {
            public void handleMessage(Message paramMessage) {
                if (paramMessage.what == 100) {
                    TextView localTextView = new TextView(LogReaderActivity.this);
                    localTextView.setText((String) paramMessage.obj);
                    localTextView.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
                    log_content.addView(localTextView);
                }
            }
        };

    }

    public void onDestroy() {
        this.isReading = false;
        super.onDestroy();
    }
}
