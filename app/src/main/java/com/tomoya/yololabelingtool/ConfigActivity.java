package com.tomoya.yololabelingtool;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ConfigActivity extends Activity implements View.OnClickListener {
    private Button closeConfigBtn;
    private Button resetBtn;
    private Button addClassBtn;
    private EditText editText;
    private LinearLayout linearLayout;
    private String[] classNameOutput;
    private SharedPreferences classData;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        closeConfigBtn = (Button) findViewById(R.id.closeBtn);
        closeConfigBtn.setOnClickListener(this);
        resetBtn = (Button) findViewById(R.id.resetBtn);
        resetBtn.setOnClickListener(this);
        addClassBtn = (Button) findViewById(R.id.addClassBtn);
        addClassBtn.setOnClickListener(this);

        linearLayout = (LinearLayout) findViewById(R.id.classList);
        editText = (EditText) findViewById(R.id.edit);
        classData = getSharedPreferences("ClassDataSave", Context.MODE_PRIVATE);
    }

    @Override
    protected void onStart(){
        super.onStart();
        editor = classData.edit();
        int classCnt = classData.getInt("ClassCount", 0);
        for (int i = 0; i < classCnt; i ++){
            TextView textView = new TextView(this);
            textView.setText(classData.getString("ClassNum"+i, ""));
            textView.setTextColor(Color.BLACK);
            linearLayout.addView(textView);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closeBtn:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.addClassBtn:
                addClassName();
                outputViewName();
                break;
            case R.id.resetBtn:
                linearLayout.removeAllViews();
                editor.clear();
                editor.commit();
                break;

        }

    }

    private void addClassName() {
        String className;
        className = editText.getText().toString();
        TextView textView = new TextView(this);
        textView.setText(className);
        textView.setTextColor(Color.BLACK);
        linearLayout.addView(textView);
    }

    private void outputViewName() {
        classNameOutput = new String[linearLayout.getChildCount()];
        editor = classData.edit();
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            TextView classNameTV = (TextView) linearLayout.getChildAt(i);
            classNameOutput[i] = classNameTV.getText().toString();
            editor.putString("ClassNum" + i, classNameOutput[i]);
        }
        editor.putInt("ClassCount", linearLayout.getChildCount());
        editor.commit();
    }

}
