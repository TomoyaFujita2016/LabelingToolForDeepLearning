package com.tomoya.yololabelingtool;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity implements View.OnClickListener {
    private Button changeClassButton, clearButton;
    private ImageButton configButton, nextButton, backButton, loadButton;
    private ImageView imageView;
    private LinearLayout listLinearLayout;
    private int classCount;
    private SharedPreferences classData, imageData;
    private SharedPreferences.Editor editor;
    private String[] classNames;
    private int classNumber;
    private TextView classNameText;
    private int imageNumber;
    private File tmp;
    private Bitmap bitmap;
    private File[] tmpFiles;
    private int imageCount;
    private int[] newXY1, newXY2, oldXY1, oldXY2;
    private int[] startPoint;
    private int[] pointerIDs;
    private int ptrIndex;
    private boolean byFirstTouch = true;
    private boolean byExistImage = false;
    private int[] rectStartXY;
    private File[] images;
    private int[] rectEndXY;
    private int[] colors;
    private String sdPath;
    private String label;
    private BufferedWriter bw;
    private String[] readText;
    private CanvasBitmap canvasBitmap;
    private TextView imageNumTv;
    private ToggleButton toggleButton;
    private Toast shortToast;

    private int pointerID1, pointerID2;
    //private int[] newX, newY, oldX, oldY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializationANDsetOnClickListener();
    }

    private void initializationANDsetOnClickListener() {
        newXY1 = new int[2];
        newXY2 = new int[2];
        oldXY1 = new int[2];
        oldXY2 = new int[2];
        readText = new String[100];
        startPoint = new int[2];
        pointerIDs = new int[2];
        rectStartXY = new int[2];
        rectEndXY = new int[2];
        colors = new int[5];
        colors[0] = 0x00FF0000;
        colors[1] = 0x0000FF00;
        colors[2] = 0x000000FF;
        colors[3] = 0x00FFFF00;
        colors[4] = 0x0000FFFF;

        shortToast = Toast.makeText(this, "This Toast has No Text.", Toast.LENGTH_SHORT);
        loadButton = (ImageButton) findViewById(R.id.loadBtn);
        changeClassButton = (Button) findViewById(R.id.changeClassbtn);
        clearButton = (Button) findViewById(R.id.clearBtn);
        configButton = (ImageButton) findViewById(R.id.configBtn);
        nextButton = (ImageButton) findViewById(R.id.nextBtn);
        backButton = (ImageButton) findViewById(R.id.backBtn);
        imageView = (ImageView) findViewById(R.id.iv);
        listLinearLayout = (LinearLayout) findViewById(R.id.displayLL);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        changeClassButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        configButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        loadButton.setOnClickListener(this);
        classNameText = (TextView) findViewById(R.id.classname);
        classData = getSharedPreferences("ClassDataSave", MODE_PRIVATE);
        imageData = getSharedPreferences("ImageData", MODE_PRIVATE);
        editor = imageData.edit();
        imageNumTv = (TextView) findViewById(R.id.displayImageNum);
    }

    private void getDataFromSP() {

        classCount = classData.getInt("ClassCount", 0);
        if (classCount != 0)
            classNames = new String[classCount];
        for (int i = 0; i < classCount; i++) {
            classNames[i] = classData.getString("ClassNum" + i, "");
        }
        classNumber = 0;

        imageCount = imageData.getInt("ImageCount", 0);
        imageNumber = imageData.getInt("ImageNumber", 0);
    }

    private void initialization() {
        classNumber = 0;
        imageNumber = 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {    //TODO change the way moving crossHair.
        //Log.i("TOGGLE", toggleButton.isChecked() + "");
        if (byExistImage) {
            if (toggleButton.isChecked()) {
                newXY1[0] = (int) event.getX();
                newXY1[1] = (int) event.getY();
                Log.d("NORMAL", startPoint[0] + "  " + startPoint[1] + "  " + newXY1[0] + "  " + newXY1[1]);
                //TODO classNames was accessed wrong index
                if (classCount != 0)
                    canvasBitmap.drawRectangle(imageNumber, startPoint, newXY1, classNames[classNumber], Color.RED, 0, false);
                if (classCount == 0)
                    canvasBitmap.drawRectangle(imageNumber, startPoint, newXY1, "NULL", Color.RED, 0, false);

                if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    Log.d("ACTION_UP", startPoint[0] + "  " + startPoint[1] + "  " + newXY1[0] + "  " + newXY1[1]);
                    canvasBitmap.drawRectangle(imageNumber, startPoint, oldXY1, classNames[classNumber], Color.RED, 0, true);
                    toggleButton.setChecked(false);
                    byFirstTouch = true;
                }

                oldXY1[0] = canvasBitmap.rectEndXY[0];
                oldXY1[1] = canvasBitmap.rectEndXY[1];
            }

            if (!toggleButton.isChecked()) {
                newXY1[0] = (int) event.getX();
                newXY1[1] = (int) event.getY();
                if (!byFirstTouch) {
                    startPoint[0] += (newXY1[0] - oldXY1[0]) / 2;
                    startPoint[1] += (newXY1[1] - oldXY1[1]) / 2;
                    canvasBitmap.drawCrossHair(imageNumber, startPoint, Color.WHITE, 0);

                }
                oldXY1[0] = (int) event.getX();
                oldXY1[1] = (int) event.getY();

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    byFirstTouch = false;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    byFirstTouch = true;
                }
            }
        }
        return true;
    }

    private void classNameChange() {
        if (classCount != 0)
            classNameText.setText("Class" + classNumber + ": " + classNames[classNumber]);
        else {
            cancelShowToast("PLEASE MAKE A CLASS NAME !");
        }
    }

    private void addViewToLL(String string) {
        TextView textView = new TextView(this);
        textView.setText(string);
        listLinearLayout.addView(textView);
    }

    private boolean importImagesFromSD() {
        File[] extDirs = getExternalFilesDirs(Environment.DIRECTORY_PICTURES);
        if (extDirs.length != 1) {

            sdPath = extDirs[extDirs.length - 1].toString();

            File imagesDir = new File(sdPath + "/images");
            if (!(imagesDir.exists())) {
                imagesDir.mkdir();
            }

            tmpFiles = new File(imagesDir.getPath()).listFiles();

            if (tmpFiles.length != 0) {
                imageCount = 0;
                for (int i = 0; i < tmpFiles.length; i++) {
                    if (tmpFiles[i].isFile() && (tmpFiles[i].getPath().endsWith(".jpg") || tmpFiles[i].getPath().endsWith(".png")))
                        imageCount++;
                }
                images = new File[imageCount];
                for (int i = 0, n = 0; i < tmpFiles.length; i++) {
                    if (tmpFiles[i].isFile() && (tmpFiles[i].getPath().endsWith(".jpg") || tmpFiles[i].getPath().endsWith(".png"))) {
                        images[n] = tmpFiles[i];
                        n++;
                    }
                }
                for (int i = 0; i < images.length - 1; i++) {
                    for (int j = images.length - 1; j > i; j--) {
                        if (images[j].compareTo(images[j - 1]) < 0) {
                            tmp = images[j - 1];
                            images[j - 1] = images[j];
                            images[j] = tmp;
                        }
                    }
                }
                editor.putInt("ImageCount", images.length);
                editor.commit();

                if (tmpFiles.length != 0)
                    canvasBitmap = new CanvasBitmap(images, imageView, this);

                return true;
            } else {

                cancelShowToast("NOT FOUND IMAGE DATA !!");
                editor.putInt("ImageCount", 0);
                editor.putInt("ImageNumber", 0);
                editor.commit();
                imageCount = 0;
                imageNumber = 0;
                imageNumTv.setText(0 + " / " + 0);
                imageView.setImageResource(R.drawable.no_images);
                return false;
            }
        } else {
            cancelShowToast("NOT FOUND IMAGE DATA !!");
            editor.putInt("ImageCount", 0);
            editor.putInt("ImageNumber", 0);
            editor.commit();
            imageCount = 0;
            imageNumber = 0;
            imageNumTv.setText(0 + " / " + 0);
            imageView.setImageResource(R.drawable.no_images);
            return false;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();

        byExistImage = importImagesFromSD();
        getDataFromSP();
        classNameChange();
        setImageNumToTv();
        displayImage(imageNumber);


    }

    private void cancelShowToast(String text) {
        //if (shortToast != null)
        //shortToast.cancel();
        shortToast.setText(text);
        shortToast.show();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //fileFromDrawable("no_images");
    }

    private void displayImage(int imgNumber) {
        if (imgNumber < imageCount && imageCount != 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            Bitmap bitmap = BitmapFactory.decodeFile(images[imgNumber].getPath(), options);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                //initialization();
                // setImageNumToTv();
            } else imageView.setImageResource(R.drawable.no_images);
        } else {
            Log.d("DisplayImage", " images.length:  < imageNumber: ");

        }
    }

    private void setImageNumToTv() {
        if (imageCount != 0)
            imageNumTv.setText(imageNumber + 1 + " / " + imageCount);
    }

    private void fileFromDrawable(String filename){

        File[] fileDrawable = new File[1];
        fileDrawable[0] = new File("/res/drawable/"+filename);
        canvasBitmap = new CanvasBitmap(fileDrawable, imageView, this);

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
            case R.id.loadBtn:
                onLoadBtn();
        }
    }

    private void onChangeClassBtn() {
        classNumber++;
        if (classNumber == classCount)
            classNumber = 0;

        classNameChange();

    }

    private void onClearBtn() {

    }

    private void onConfigBtn() {
        Log.d(TAG, "Clicked onConfigButton");
        Intent intent = new Intent(this, ConfigActivity.class);
        startActivity(intent);

    }

    private void onNextBtn() {
        Log.d(TAG, "Clicked onNextButton");
        if (imageNumber != imageCount - 1 && imageCount != 0) {
            imageNumber++;
            editor.putInt("ImageNumber", imageNumber);
            editor.commit();
            setImageNumToTv();
        } else cancelShowToast("UPPER LIMIT !");

        displayImage(imageNumber);
    }

    private void onBackBtn() {
        Log.d(TAG, "Clicked onBackButton");
        if (imageNumber != 0) {
            imageNumber--;
            editor.putInt("ImageNumber", imageNumber);
            editor.commit();
            setImageNumToTv();
        } cancelShowToast("LOWER LIMIT !");

        displayImage(imageNumber);
    }

    private void onLoadBtn() {
        Log.d(TAG, "Clicked onLoadButton");
        byExistImage = importImagesFromSD();
        initialization();
        setImageNumToTv();
        displayImage(imageNumber);
    }
}
