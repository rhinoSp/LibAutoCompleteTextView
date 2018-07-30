package com.rhino.actvd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.rhino.actv.AutoCompleteTextView;

public class MainActivity extends AppCompatActivity {

    AutoCompleteTextView mAutoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAutoCompleteTextView = findViewById(R.id.et_test);
        findViewById(R.id.bt_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutoCompleteTextView.saveInputCache();
            }
        });

    }
}
