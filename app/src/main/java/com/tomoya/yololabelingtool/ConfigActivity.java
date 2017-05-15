package com.tomoya.yololabelingtool;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ConfigActivity extends Activity implements View.OnClickListener {
    private Button closeButton;
    private Button mkdirButton;
    private ImageButton nextButton;
    private ImageButton backButton;
    private LinearLayout dirListLL;
    TextView[] pathTV;
    private String[] dirList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        dirListLL = (LinearLayout) findViewById(R.id.pathListLL);
        closeButton = (Button) findViewById(R.id.closeConfigBtn);
        closeButton.setOnClickListener(this);
        mkdirButton = (Button) findViewById(R.id.mkdirBtn);
        mkdirButton.setOnClickListener(this);
        nextButton = (ImageButton) findViewById(R.id.nextBtn2);
        nextButton.setOnClickListener(this);
        backButton = (ImageButton) findViewById(R.id.backBtn2);
        backButton.setOnClickListener(this);

        dirListLL.removeAllViews();
        dirList = sdDirList("");
        pathTV = new TextView[dirList.length];
        for (int i = 0; i < pathTV.length; i++)
            pathTV[i] = new TextView(this);
        addTextViewToLL();


    }

    private void addTextViewToLL() {
        dirListLL.removeAllViews();
        dirList = sdDirList("");
        for (int i = 0; i < dirList.length; i++) {
            pathTV[i].setText("/" + dirList[i]);
            pathTV[i].setTextSize(10 * getResources().getDisplayMetrics().density);
            if (i % 2 != 0)
                pathTV[i].setBackgroundColor(Color.DKGRAY);
            if(i % 2 == 0)
                pathTV[i].setBackgroundColor(Color.LTGRAY);
            pathTV[i].setTextColor(Color.BLACK);
            dirListLL.addView(pathTV[i]);
        }
    }


    private String[] sdDirList(String dirName) {

        ArrayList<String> arraylistStr = new ArrayList<>();
        String[] listStr;
        File[] extDirs = getExternalFilesDirs(Environment.DIRECTORY_PICTURES);
        String sdpath = extDirs[extDirs.length - 1].toString();
        File sdPicDir = new File(sdpath + "/" + dirName);

        File[] list = sdPicDir.listFiles();
        for (int i = 0; i < list.length; i++) {
            Log.d("LIST", list[i].getName());
            if (list[i].isDirectory()) {
                arraylistStr.add(list[i].getName());
            }
        }
        listStr = new String[arraylistStr.size()];
        for (int i = 0; i < arraylistStr.size(); i++) {
            listStr[i] = arraylistStr.get(i);
        }
        return listStr;
    }

    private void mkdir(String path, String dirName) {
        File file = new File(path + "/" + dirName);
        if (!file.exists()) {
            file.mkdir();
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closeConfigBtn:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.mkdirBtn:
                break;
            case R.id.nextBtn:
                break;
            case R.id.backBtn:
                break;
        }

    }
}
