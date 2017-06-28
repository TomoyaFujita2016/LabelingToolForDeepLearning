package com.tomoya.yololabelingtool;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

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
    private TextView imageNameTv;
    private TextView imageSizeTv;
    private int imageNumber;
    private File tmp;
    private Bitmap bitmap;
    private File[] tmpFiles;
    private int imageCount;
    private int[] newXY1, newXY2, oldXY1, oldXY2;
    private int[] startPoint;
    private int[] pointerIDs;
    private boolean byFirstTouch = true;
    private boolean byExistImage = false;
    private int[] rectStartXY;
    private File[] images;
    private int[] rectEndXY;
    private String sdPath;
    private String label;
    private BufferedWriter bw;
    private String[] readText;
    private CanvasBitmap canvasBitmap;
    private TextView imageNumTv;
    private ToggleButton toggleButton;
    private Toast shortToast;
    private String annotation;
    private TypedArray colors;
    private int colorIndex = 0;
    private float[][] annotationData;
    private ImageView[] imageViews;


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
        imageViews = new ImageView[4];

        classNames = null;
        colors = getResources().obtainTypedArray(R.array.colorList);
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
        imageNameTv = (TextView) findViewById(R.id.displayImageName);
        imageSizeTv = (TextView) findViewById(R.id.displayImageSize);
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
        imageViews[0] = (ImageView) findViewById(R.id.leftTopIv);
        imageViews[1] = (ImageView) findViewById(R.id.leftBottomIv);
        imageViews[2] = (ImageView) findViewById(R.id.rightTopIv);
        imageViews[3] = (ImageView) findViewById(R.id.rightBottomIv);
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
        colorIndex = 0;
    }

    private String getNameWithoutExtension(File file) {
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        if (index != -1) {
            return fileName.substring(0, index);
        }
        return "";
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.i("TOGGLE", toggleButton.isChecked() + "");
        if (byExistImage) {
            if (toggleButton.isChecked()) {
                newXY1[0] = (int) event.getX();
                newXY1[1] = (int) event.getY();
                newXY1[1] = (int) event.getY();
                Log.d("NORMAL", startPoint[0] + "  " + startPoint[1] + "  " + newXY1[0] + "  " + newXY1[1]);
                if (classCount != 0)
                    canvasBitmap.drawRectangle(imageNumber, startPoint, newXY1, classNames[classNumber], colors.getColor(colorIndex, 0), 0, false);
                if (classCount == 0)
                    canvasBitmap.drawRectangle(imageNumber, startPoint, newXY1, "NULL", colors.getColor(colorIndex, 0), 0, false);

                if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    Log.d("ACTION_UP", startPoint[0] + "  " + startPoint[1] + "  " + newXY1[0] + "  " + newXY1[1]);

                    if (classCount != 0) {
                        annotation = classNumber + " " + canvasBitmap.drawRectangle(imageNumber, startPoint, newXY1, classNames[classNumber], colors.getColor(colorIndex, 0), 0, true);
                        Log.i("ANNOTATION", annotation);

                        if (outputTextToSD(getNameWithoutExtension(images[imageNumber]), annotation))
                            addViewToLL(annotation, colorIndex);
                    }
                    if (classCount == 0)
                        canvasBitmap.drawRectangle(imageNumber, startPoint, newXY1, "NULL", colors.getColor(colorIndex, 0), 0, true);

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
                    if (!(canvasBitmap.drawCrossHair(imageNumber, startPoint, Color.BLACK, 0)))
                        cancelShowToast("ERROR: LOAD IMAGE");


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

    private void addViewToLL(String string, int colorIx) {
        TextView textView = new TextView(this);
        textView.setText(string);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        textView.setTextColor(colors.getColor(colorIx, 0));
        listLinearLayout.addView(textView);
    }

    private void removeTextInSD(String fileName) {
        File[] extDirs = getExternalFilesDirs(Environment.DIRECTORY_PICTURES);
        if (extDirs.length != 1) {
            sdPath = extDirs[extDirs.length - 1].toString();
            File tmpDir = new File(sdPath);
            File newFile = new File(tmpDir.getParent() + "/texts/" + fileName + ".txt");
            if (newFile.exists()) {
                newFile.delete();
            }
        }
    }

    private boolean inputTextFromSD(String fileName) {
        List<String> tmp = new ArrayList<>();
        try {
            File[] extDirs = getExternalFilesDirs(Environment.DIRECTORY_PICTURES);
            if (extDirs.length != 1) {
                sdPath = extDirs[extDirs.length - 1].toString();
                File tmpDir = new File(sdPath);
                File newFile = new File(tmpDir.getParent() + "/texts/" + fileName + ".txt");
                if (newFile.exists()) {
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(newFile));
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            tmp.add(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    annotationData = new float[tmp.size()][5];

                    for (int i = 0; i < tmp.size(); i++) {
                        String[] strings = tmp.get(i).split(" ");
                        for (int n = 0; n < 5; n++) {
                            annotationData[i][n] = Float.parseFloat(strings[n]);
                        }
                        annotationData[i][0] = (int) annotationData[i][0];
                    }

                    return true;
                } else {
                    Log.i("INPUT_TEXT", "The Text File doesn't exist.");
                    return false;
                }
            } else
                return false;
        } catch (Exception e) {
            cancelShowToast("ERROR: INPUT TEXT FILE");
            return false;
        }
    }

    private boolean outputTextToSD(String fileName, String text) {
        File[] extDirs = getExternalFilesDirs(Environment.DIRECTORY_PICTURES);
        try {
            if (extDirs.length != 1) {
                sdPath = extDirs[extDirs.length - 1].toString();
                File tmpDir = new File(sdPath);
                File textsDir = new File(tmpDir.getParent() + "/texts");
                if (!(textsDir.exists())) {
                    textsDir.mkdir();
                }
                File newFile = new File(tmpDir.getParent() + "/texts/" + fileName + ".txt");
                if (!(newFile.exists())) {
                    try {
                        newFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    FileWriter fileWriter = new FileWriter(newFile, true);
                    fileWriter.write(text + "\r\n");
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return true;
            } else {
                cancelShowToast("SAVE FAILED!\nNO SD CARD !");
                return false;
            }
        } catch (Exception e) {
            cancelShowToast("ERROR: OUTPUT TEXT FILE");
            return false;
        }
    }

    private boolean importImagesFromSD() {
        File[] extDirs = getExternalFilesDirs(Environment.DIRECTORY_PICTURES);
        if (extDirs.length != 1) {

            sdPath = extDirs[extDirs.length - 1].toString();
            File tmpDir = new File(sdPath);
            File imagesDir = new File(tmpDir.getParent() + "/images");
            if (!(imagesDir.exists())) {
                imagesDir.mkdir();
            }

            tmpFiles = new File(imagesDir.getPath()).listFiles();

            if (tmpFiles.length != 0) {
                imageCount = 0;
                for (int i = 0; i < tmpFiles.length; i++) {
                    if (tmpFiles[i].isFile() && (tmpFiles[i].getPath().endsWith(".jpg") || tmpFiles[i].getPath().endsWith(".png") || tmpFiles[i].getPath().endsWith(".jpeg")))
                        imageCount++;
                }
                images = new File[imageCount];
                for (int i = 0, n = 0; i < tmpFiles.length; i++) {
                    if (tmpFiles[i].isFile() && (tmpFiles[i].getPath().endsWith(".jpg") || tmpFiles[i].getPath().endsWith(".png") || tmpFiles[i].getPath().endsWith(".jpeg"))) {
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
        listLinearLayout.removeAllViews();
        byExistImage = importImagesFromSD();
        getDataFromSP();
        classNameChange();
        if (byExistImage) {
            try {
                displayImageData();
                displayImage(imageNumber);
            } catch (Exception e) {
                cancelShowToast("ERROR: DISPLAY IMAGE DATA");
            }
        }

    }

    private void cancelShowToast(String text) {
        shortToast.setText(text);
        shortToast.show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (byExistImage && inputTextFromSD(getNameWithoutExtension(images[imageNumber])))
            restoreTheRects();
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

    private void displayImageData() {
        if (byExistImage) {
            imageNumTv.setText(imageNumber + 1 + " / " + imageCount);
            imageNameTv.setText(images[imageNumber].getName());
            imageSizeTv.setText(canvasBitmap.imageSizeW[imageNumber] + " × " + canvasBitmap.imageSizeH[imageNumber]);
        }
    }

    private void displayZoomImg() {
        if (byExistImage) {
            final double ZoomRatio = 0.28;
            int width, height;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            Bitmap bitmap = canvasBitmap.bitmap.copy(Bitmap.Config.ARGB_8888, true);
            width = bitmap.getWidth();
            height = bitmap.getHeight();
            int rectWidth = (int) (width * ZoomRatio);
            int rectHeight = (int) (height * ZoomRatio);

            Bitmap windowBitmap = Bitmap.createBitmap(rectWidth, rectHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(windowBitmap);
            Rect rect = new Rect(
                    canvasBitmap.touchPointOnView[0] - (rectWidth / 2),
                    canvasBitmap.touchPointOnView[1] - (rectHeight / 2),
                    canvasBitmap.touchPointOnView[0] + (rectWidth / 2),
                    canvasBitmap.touchPointOnView[1] + (rectHeight / 2));
            Rect rectAll = new Rect(0, 0, rectWidth, rectWidth);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            try {
                canvas.drawBitmap(bitmap, rect, rectAll, paint);

                if (canvasBitmap.touchPointOnView[0] < (width / 2) && canvasBitmap.touchPointOnView[1] < (height / 2)) {   //left top
                    setImageViewsGone(0);
                    imageViews[0].setImageBitmap(windowBitmap);
                }
                if (canvasBitmap.touchPointOnView[0] < (width / 2) && canvasBitmap.touchPointOnView[1] >= (height / 2)) {   //left bottom
                    setImageViewsGone(1);
                    imageViews[1].setImageBitmap(windowBitmap);
                }
                if (canvasBitmap.touchPointOnView[0] >= (width / 2) && canvasBitmap.touchPointOnView[1] < (height / 2)) {   //right top
                    setImageViewsGone(2);
                    imageViews[2].setImageBitmap(windowBitmap);
                }
                if (canvasBitmap.touchPointOnView[0] >= (width / 2) && canvasBitmap.touchPointOnView[1] >= (height / 2)) {   //right bottom
                    setImageViewsGone(3);
                    imageViews[3].setImageBitmap(windowBitmap);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void setImageViewsGone(int ivNum) {
        for (int i = 0; i < imageViews.length; i++)
            imageViews[i].setVisibility(View.GONE);
        imageViews[ivNum].setVisibility(View.VISIBLE);
    }


    private void restoreTheRects() {
        int width, height, centerX, centerY;
        int[] sXY = new int[2];
        int[] eXY = new int[2];
        String className;
        int colorIdx;
        if (classCount != 0 && annotationData.length != 0) {
            for (int i = 0; i < annotationData.length; i++) {
                width = (int) (annotationData[i][3] * canvasBitmap.imageSizeW[imageNumber]);
                height = (int) (annotationData[i][4] * canvasBitmap.imageSizeH[imageNumber]);
                centerX = (int) (annotationData[i][1] * canvasBitmap.imageSizeW[imageNumber]);
                centerY = (int) (annotationData[i][2] * canvasBitmap.imageSizeH[imageNumber]);

                sXY[0] = centerX - (width / 2);
                sXY[1] = centerY - (height / 2);
                eXY[0] = centerX + (width / 2);
                eXY[1] = centerY + (height / 2);

                if (classNames.length <= (int) annotationData[i][0]) {
                    className = "ClassNum: " + (int) annotationData[i][0];
                    cancelShowToast("CLASS IS NOT ENOUGH TO DRAW RECTS!");
                } else {
                    className = classNames[(int) annotationData[i][0]];
                }

                if (colors.length() <= (int) annotationData[i][0]) {
                    colorIdx = (int) annotationData[i][0] - colors.length();
                } else {
                    colorIdx = (int) annotationData[i][0];
                }

                canvasBitmap.drawRectangle(imageNumber, sXY, eXY, className, colors.getColor(colorIdx, 0), 0, true);
                addViewToLL((int) annotationData[i][0] + " " + annotationData[i][1] + " " + annotationData[i][2] + " " + annotationData[i][3] + " " + annotationData[i][4], colorIdx);
            }
        } else {
            for (int i = 0; i < annotationData.length; i++) {
                width = (int) (annotationData[i][3] * canvasBitmap.imageSizeW[imageNumber]);
                height = (int) (annotationData[i][4] * canvasBitmap.imageSizeH[imageNumber]);
                centerX = (int) (annotationData[i][1] * canvasBitmap.imageSizeW[imageNumber]);
                centerY = (int) (annotationData[i][2] * canvasBitmap.imageSizeH[imageNumber]);

                sXY[0] = centerX - (width / 2);
                sXY[1] = centerY - (height / 2);
                eXY[0] = centerX + (width / 2);
                eXY[1] = centerY + (height / 2);
                if (colors.length() <= (int) annotationData[i][0]) {
                    colorIdx = (int) annotationData[i][0] - colors.length();
                } else {
                    colorIdx = (int) annotationData[i][0];
                }
                className = "ClassNum: " + (int) annotationData[i][0];

                canvasBitmap.drawRectangle(imageNumber, sXY, eXY, className, colors.getColor(colorIdx, 0), 0, true);
                addViewToLL((int) annotationData[i][0] + " " + annotationData[i][1] + " " + annotationData[i][2] + " " + annotationData[i][3] + " " + annotationData[i][4], colorIdx);
            }
            cancelShowToast("CLASS IS NOT ENOUGH TO DRAW RECTS!");
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
            case R.id.loadBtn:
                onLoadBtn();
        }
    }

    private void onChangeClassBtn() {
        classNumber++;
        if (classNumber == classCount)
            classNumber = 0;

        colorIndex = classNumber;
        if (colors.length() <= colorIndex)
            colorIndex = 0;
        classNameChange();

    }

    private void onClearBtn() {
        if (byExistImage) {
            removeTextInSD(getNameWithoutExtension(images[imageNumber]));
            canvasBitmap.resetCanvas(imageNumber);
            annotationData = null;
        }

        listLinearLayout.removeAllViews();
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
            listLinearLayout.removeAllViews();
            displayImageData();
            displayImage(imageNumber);

            if (inputTextFromSD(getNameWithoutExtension(images[imageNumber])))
                restoreTheRects();

        } else cancelShowToast("UPPER LIMIT !");


    }

    private void onBackBtn() {
        Log.d(TAG, "Clicked onBackButton");
        if (imageNumber != 0) {
            imageNumber--;
            editor.putInt("ImageNumber", imageNumber);
            editor.commit();
            listLinearLayout.removeAllViews();
            displayImageData();
            displayImage(imageNumber);
            if (inputTextFromSD(getNameWithoutExtension(images[imageNumber])))
                restoreTheRects();
        } else cancelShowToast("LOWER LIMIT !");
    }


    private void onLoadBtn() {
        Log.d(TAG, "Clicked onLoadButton");
        listLinearLayout.removeAllViews();
        byExistImage = importImagesFromSD();
        initialization();
        if (byExistImage) {
            displayImageData();
            displayImage(imageNumber);
        }
        if (byExistImage && inputTextFromSD(getNameWithoutExtension(images[imageNumber])))
            restoreTheRects();
    }
}
