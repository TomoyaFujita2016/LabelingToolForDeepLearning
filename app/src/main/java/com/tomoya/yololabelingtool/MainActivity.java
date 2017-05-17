package com.tomoya.yololabelingtool;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.Image;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static android.content.ContentValues.TAG;
import static java.lang.Math.abs;

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
    private int[] newXY1, newXY2, oldXY1, oldXY2;
    private int[] startPoint;
    private int[] pointerIDs;
    private int ptrIndex;
    private boolean byTwoFingerFirst = true;
    private boolean bySingleFingerMove = false;
    private int[] rectStartXY;
    private File[] images;
    private int[] rectEndXY;
    private int[] colors;
    private String sdpath;
    private String label;
    private BufferedWriter bw;
    private String[] readText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializationANDsetOnClickListener();
        imageProcessing = new ImageProcessing();
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
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getActionMasked();
        int pointerIndex = event.getActionIndex();
        int pointerID = event.getPointerId(pointerIndex);


        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                pointerIDs[0] = pointerID;
                pointerIDs[1] = -1;
                ptrIndex = event.findPointerIndex(pointerIDs[0]);
                newXY1[0] = (int) event.getX(ptrIndex);
                newXY1[1] = (int) event.getY(ptrIndex);
                newXY1 = imageProcessing.globalCoordinateToLocal(imageView, newXY1, this);

                if (!bySingleFingerMove) {
                    bySingleFingerMove = true;
                    rectStartXY[0] = startPoint[0];
                    rectStartXY[1] = startPoint[1];
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                bySingleFingerMove = false;
                if (pointerIDs[1] == -1) {
                    pointerIDs[1] = pointerID;
                } else if (pointerIDs[0] >= 0) {
                    pointerIDs[0] = pointerID;
                }

                if (pointerIDs[0] >= 0) {
                    ptrIndex = event.findPointerIndex(pointerIDs[0]);
                    newXY1[0] = (int) event.getX(ptrIndex);
                    newXY1[1] = (int) event.getY(ptrIndex);
                    newXY1 = imageProcessing.globalCoordinateToLocal(imageView, newXY1, this);
                }
                if (pointerIDs[1] >= 0) {
                    ptrIndex = event.findPointerIndex(pointerIDs[1]);
                    newXY2[0] = (int) event.getX(ptrIndex);
                    newXY2[1] = (int) event.getY(ptrIndex);
                    newXY2 = imageProcessing.globalCoordinateToLocal(imageView, newXY2, this);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                bySingleFingerMove = false;
                byTwoFingerFirst = true;
                if (pointerIDs[0] == pointerID) {
                    pointerIDs[0] = -1;
                } else if (pointerIDs[1] == pointerID) {
                    pointerIDs[1] = -1;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (bySingleFingerMove) {
                    bitmap = imageProcessing.drawRectangle(imageView, bitmap, rectStartXY[0], rectStartXY[1], rectEndXY[0], rectEndXY[1], 0, colors[classNumber]);
                    float[] centerRatioXY = new float[2];
                    float[] rectWidHei = new float[2];
                    centerRatioXY[0] = (float) ((rectStartXY[0] + rectEndXY[0]) / 2) / imageView.getWidth();
                    centerRatioXY[1] = (float) ((rectStartXY[1] + rectEndXY[1]) / 2) / imageView.getHeight();
                    rectWidHei[0] = (float) abs(rectStartXY[0] - rectEndXY[0]) / imageView.getWidth();
                    rectWidHei[1] = (float) abs(rectStartXY[1] - rectEndXY[1]) / imageView.getHeight();
                    label = classNumber + " " + String.format("%1$.3f", centerRatioXY[0]) + " " + String.format("%1$.3f", centerRatioXY[1]) + " " + String.format("%1$.3f", rectWidHei[0]) + " " + String.format("%1$.3f", rectWidHei[1]);
                    TextView textView = new TextView(this);
                    textView.setText(label);
                    textView.setTextSize(7);
                    textView.setTextColor(Color.BLACK);
                    listLinearLayout.addView(textView);
                    outPutTextFile();
                }
                byTwoFingerFirst = false;
                pointerIDs[0] = -1;
                pointerIDs[1] = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                if (pointerIDs[0] >= 0) {
                    ptrIndex = event.findPointerIndex(pointerIDs[0]);
                    newXY1[0] = (int) event.getX(ptrIndex);
                    newXY1[1] = (int) event.getY(ptrIndex);
                    newXY1 = imageProcessing.globalCoordinateToLocal(imageView, newXY1, this);

                    if (bySingleFingerMove) {
                        if (newXY1[0] < 0)
                            newXY1[0] = 0;
                        if (bitmap.getWidth() < newXY1[0])
                            newXY1[0] = bitmap.getWidth();
                        if (newXY1[1] < 0)
                            newXY1[1] = 0;
                        if (bitmap.getHeight() < newXY1[1])
                            newXY1[1] = bitmap.getHeight();

                        rectEndXY[0] = newXY1[0];
                        rectEndXY[1] = newXY1[1];

                        imageProcessing.drawRectangle(imageView, bitmap, rectStartXY[0], rectStartXY[1], newXY1[0], newXY1[1], 0, colors[classNumber]);
                    }

                }
                if (pointerIDs[1] >= 0) {
                    ptrIndex = event.findPointerIndex(pointerIDs[1]);
                    newXY2[0] = (int) event.getX(ptrIndex);
                    newXY2[1] = (int) event.getY(ptrIndex);
                    newXY2 = imageProcessing.globalCoordinateToLocal(imageView, newXY2, this);
                }
                if (pointerIDs[0] >= 0 && pointerIDs[1] >= 0 && !byTwoFingerFirst) {
                    startPoint[0] += ((newXY1[0] + newXY2[0]) - (oldXY1[0] + oldXY2[0])) / 2;
                    startPoint[1] += ((newXY1[1] + newXY2[1]) - (oldXY1[1] + oldXY2[1])) / 2;

                    if (startPoint[0] < 0)
                        startPoint[0] = 0;
                    if (bitmap.getWidth() < startPoint[0])
                        startPoint[0] = bitmap.getWidth();
                    if (startPoint[1] < 0)
                        startPoint[1] = 0;
                    if (bitmap.getHeight() < startPoint[1])
                        startPoint[1] = bitmap.getHeight();

                    imageProcessing.drawCrossHair(imageView, bitmap, startPoint[0], startPoint[1], 0, 0x00000000);

                }
                if (pointerIDs[0] >= 0) {
                    ptrIndex = event.findPointerIndex(pointerIDs[0]);
                    oldXY1[0] = (int) event.getX(ptrIndex);
                    oldXY1[1] = (int) event.getY(ptrIndex);
                    oldXY1 = imageProcessing.globalCoordinateToLocal(imageView, oldXY1, this);
                }
                if (pointerIDs[1] >= 0) {
                    ptrIndex = event.findPointerIndex(pointerIDs[1]);
                    oldXY2[0] = (int) event.getX(ptrIndex);
                    oldXY2[1] = (int) event.getY(ptrIndex);
                    oldXY2 = imageProcessing.globalCoordinateToLocal(imageView, oldXY2, this);
                }
                byTwoFingerFirst = false;
                break;
        }

        return true;
    }

    private void outPutTextFile() {
        try {
            File fil = new File(sdpath + "/textFiles/" + imageNum+ ".txt");
            FileOutputStream fos;
            fos = new FileOutputStream(fil, true);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            bw = new BufferedWriter(osw);
            bw.append(label+ "\r\n");
            bw.close();


        }catch (IOException e){

        }
    }
    @Override
    protected void onStart() {
        super.onStart();


    }

    private String[][] inputRectangle(){
        int i = 0;
        try {
            File fil = new File(sdpath + "/textFiles/" + imageNum + ".txt");
            FileInputStream fis = openFileInput(fil.getName().toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

            while ((readText[i] = reader.readLine()) != null){
                i++;
            }
        }catch (IOException e){

        }

        String[][] splitText = new String[i][100];
        for (int n = 0; n < i; n++){
            splitText[n] = readText[n].split(" ");
            float stX = (Float.valueOf(splitText[n][1]) - (Float.valueOf(splitText[n][3])/ 2)) * bitmap.getWidth();
            float stY = (Float.valueOf(splitText[n][2]) - (Float.valueOf(splitText[n][4])/2)) * bitmap.getHeight();
            float endX = (Float.valueOf(splitText[n][1]) + (Float.valueOf(splitText[n][3])/2)) * bitmap.getWidth();
            float endY = (Float.valueOf(splitText[n][2]) + (Float.valueOf(splitText[n][4])/2)) * bitmap.getHeight();

            //bitmap = imageProcessing.drawRectangle(imageView, bitmap,stX, stY, endX, endY, 0, colors[Integer.valueOf(splitText[n][0]));
        }



        return splitText;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //get some data from SP
        classData = getSharedPreferences("ClassDataSave", MODE_PRIVATE);
        editor = classData.edit();
        classNum = classData.getInt("ClassCount", 0);

        if (classNum != 0)
            classNames = new String[classNum];
        for (int i = 0; i < classNum; i++) {
            classNames[i] = classData.getString("ClassNum" + i, "");
        }


        classNumber = 0;
        classNameText.setText("Class" + classNumber + ":" + classNames[classNumber]);


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        importImagesFromSD();
        Rect rectView = new Rect();
        imageView.getGlobalVisibleRect(rectView);
        startPoint[0] = rectView.left + (rectView.width() / 2);
        startPoint[1] = rectView.top + (rectView.height() / 2);
    }


    private void importImagesFromSD() {


        File[] extDirs = getExternalFilesDirs(Environment.DIRECTORY_PICTURES);
        sdpath = extDirs[extDirs.length - 1].toString();

        File f = new File(sdpath + "/images");
        if (f.exists()) {
            tmp_images = new File(sdpath + "/images").listFiles();

            imageCnt = 0;
            for (int i = 0; i < tmp_images.length; i++) {
                if (tmp_images[i].isFile() && (tmp_images[i].getPath().endsWith(".jpg") || tmp_images[i].getPath().endsWith(".png")))
                    imageCnt++;
            }
            images = new File[imageCnt];
            int n = 0;
            for (int i = 0; i < tmp_images.length; i++) {
                if (tmp_images[i].isFile() && (tmp_images[i].getPath().endsWith(".jpg") || tmp_images[i].getPath().endsWith(".png"))) {
                    images[n] = tmp_images[i];
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
        classNumber++;
        Log.d(TAG, "Clicked onChangeButton");
        if (classNumber > classNum - 1)
            classNumber = 0;
        if (classNum != 0)
            classNameText.setText("Class" + classNumber + ":" + classNames[classNumber]);
    }

    private void onClearBtn() {
        Log.d(TAG, "Clicked onClearButton");
        File fil = new File(sdpath + "/textFiles/" + imageNum+ ".txt");
        fil.delete();
        listLinearLayout.removeAllViews();
        bitmap = BitmapFactory.decodeFile(images[imageNum].toString());
        bitmap = imageProcessing.bitmapToMutable(imageView, bitmap);
        imageView.setImageBitmap(bitmap);
        listLinearLayout.removeAllViews();

    }

    private void onConfigBtn() {
        Log.d(TAG, "Clicked onConfigButton");
        Intent intent = new Intent(this, ConfigActivity.class);
        startActivity(intent);

    }

    private void onNextBtn() {
        Log.d(TAG, "Clicked onNextButton");
        listLinearLayout.removeAllViews();
        File[] extDirs = getExternalFilesDirs(Environment.DIRECTORY_PICTURES);
        String sdpath = extDirs[extDirs.length - 1].toString();
        if (imageNum != imageCnt - 1)
            imageNum++;
        bitmap = BitmapFactory.decodeFile(images[imageNum].toString());
        bitmap = imageProcessing.bitmapToMutable(imageView, bitmap);
        imageView.setImageBitmap(bitmap);

    }

    private void onBackBtn() {
        Log.d(TAG, "Clicked onBackButton");
        listLinearLayout.removeAllViews();
        if (imageNum != 0) {
            imageNum--;
        }
        bitmap = BitmapFactory.decodeFile(images[imageNum].toString());
        bitmap = imageProcessing.bitmapToMutable(imageView, bitmap);
        imageView.setImageBitmap(bitmap);
    }
}
