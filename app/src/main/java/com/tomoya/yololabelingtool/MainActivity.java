package com.tomoya.yololabelingtool;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity implements View.OnClickListener {
    private Button changeClassButton, clearButton;
    private ImageButton configButton, nextButton, backButton;
    private ImageView imageView;
    private LinearLayout listLinearLayout;
    private int classNum;
    private SharedPreferences classData;
    private SharedPreferences.Editor editor;
    private String[] classNames;
    private int classNumber;
    private TextView classNameText;
    private int imageNum;
    private File tmp;
    private Bitmap bitmap;
    private File[] tmp_images;
    private int imageCnt;
    private ImageProcessing imageProcessing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializationANDsetOnClickListener();
        imageProcessing = new ImageProcessing();
    }

    private void initializationANDsetOnClickListener() {
        changeClassButton = (Button) findViewById(R.id.changeClassbtn);
        clearButton = (Button) findViewById(R.id.clearBtn);
        configButton = (ImageButton) findViewById(R.id.configBtn);
        nextButton = (ImageButton) findViewById(R.id.nextBtn);
        backButton = (ImageButton) findViewById(R.id.backBtn);
        imageView = (ImageView) findViewById(R.id.iv);
        listLinearLayout = (LinearLayout) findViewById(R.id.displayLL);
        changeClassButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        configButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        classNameText = (TextView) findViewById(R.id.classname);
    }

    @Override
    protected void onStart() {
        super.onStart();
        classData = getSharedPreferences("ClassDataSave", MODE_PRIVATE);
        editor = classData.edit();
        classNum = classData.getInt("ClassCount", 0);
        if (classNum != 0)
            classNames = new String[classNum];
        for (int i = 0; i < classNum; i++) {
            classNames[i] = classData.getString("ClassNum" + i, "");
        }

        File[] extDirs = getExternalFilesDirs(Environment.DIRECTORY_PICTURES);
        String sdpath = extDirs[extDirs.length - 1].toString();

        File f = new File(sdpath + "/images");
        if (f.exists()) {
            tmp_images = new File(sdpath + "/images").listFiles();

            imageCnt = 0;
            for (int i = 0; i < tmp_images.length; i++) {
                if (tmp_images[i].isFile() && tmp_images[i].getPath().endsWith(".jpg") && tmp_images[i].getPath().endsWith(".png"))
                    imageCnt++;
            }
            File[] images = new File[imageCnt];
            int n = 0;
            for (int i = 0; i < tmp_images.length; i++) {
                if (tmp_images[i].isFile() && (tmp_images[i].getPath().endsWith(".jpg") || tmp_images[i].getPath().endsWith(".png"))) {
                    images[n]= tmp_images[i];
                    n ++;
                }

            }

            for (int i = 0; i < images.length; i++) {
                for (int m = 0; m < images.length; m++) {
                    if (images[m].compareTo(images[m + 1]) < 0) {
                        tmp = images[m];
                        images[m] = images[m + 1];
                        images[m + 1] = tmp;
                    }
                }
            }

            bitmap = BitmapFactory.decodeFile(images[imageNum].toString());
            bitmap = imageProcessing.bitmapToMutable(imageView, bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.changeClassbtn:
                onChangeClassBtn();
                break;
            case R.id.clearBtn:
                onClearBtn();
                break;
            case R.id.configBtn:
                onConfigBtn();
                break;
            case R.id.nextBtn:
                onNextBtn();
                break;
            case R.id.backBtn:
                onBackBtn();
                break;
        }
    }

    private void onChangeClassBtn() {
        Log.d(TAG, "Clicked onChangeButton");
        if (classNumber > classNum - 1)
            classNumber = 0;
        if (classNum != 0)
            classNameText.setText("Class" + classNumber + ":" + classNames[classNumber]);
        classNumber++;
    }

    private void onClearBtn() {
        Log.d(TAG, "Clicked onClearButton");
        listLinearLayout.removeAllViews();

    }

    private void onConfigBtn() {
        Log.d(TAG, "Clicked onConfigButton");
        Intent intent = new Intent(this, ConfigActivity.class);
        startActivity(intent);

    }

    private void onNextBtn() {
        Log.d(TAG, "Clicked onNextButton");
        File[] extDirs = getExternalFilesDirs(Environment.DIRECTORY_PICTURES);
        String sdpath = extDirs[extDirs.length - 1].toString();

    }

    private void onBackBtn() {
        Log.d(TAG, "Clicked onBackButton");

    }
}
