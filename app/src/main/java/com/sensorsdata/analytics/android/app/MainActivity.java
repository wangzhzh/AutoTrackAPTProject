package com.sensorsdata.analytics.android.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;

import com.sensorsdata.analytics.android.annotation.SensorsDataBindView;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

public class MainActivity extends AppCompatActivity {
    @SensorsDataBindView(R.id.button)
    AppCompatButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SensorsDataAPI.bindView(this);

        button.setText("New Text");
    }
}
