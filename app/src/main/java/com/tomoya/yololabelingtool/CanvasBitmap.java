package com.tomoya.yololabelingtool;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by Tomoya on 2017/05/19.
 */

public class CanvasBitmap {


    private ImageView imageView;
    private Activity activity;
    private Canvas canvas;
    private Paint paintCrossHair, paintRect;
    private File[] images;
    private BitmapFactory.Options options;
    private int saveImageNumber;
    private SharedPreferences saveNumber;
    private SharedPreferences.Editor editor;
    private String data;

    public Bitmap bitmap, tmpBitmap;
    public float[] imageRatio;
    public int[] crossHairXY;
    public int[] rectStartXY, rectEndXY;
    public int[] imageSizeH, imageSizeW;
    public int[] touchPointOnView;

    CanvasBitmap(File[] images, ImageView imageView, Activity activity) {
        Log.d("Constructor", "Loaded Constructor");

        this.images = images;
        this.imageView = imageView;
        this.activity = activity;

        imageRatio = new float[2];
        touchPointOnView = new int[2];
        options = new BitmapFactory.Options();
        options.inMutable = true;
        crossHairXY = new int[2];
        rectStartXY = new int[2];
        rectEndXY = new int[2];
        imageSizeH = new int[images.length];
        imageSizeW = new int[images.length];

        //canvas = new Canvas(bitmap);
        //canvas.drawBitmap(bitmap, 0, 0, null);


        paintCrossHair = new Paint();
        paintCrossHair.setStyle(Paint.Style.STROKE);
        paintCrossHair.setAntiAlias(true);

        paintRect = new Paint();
        paintRect.setStyle(Paint.Style.STROKE);
        paintRect.setAntiAlias(true);
        getSize();
        fileToBitmap(0, true);

    }


    private int[] GLtoLC(int[] XY) {
        Rect rectView = new Rect();
        imageView.getGlobalVisibleRect(rectView);

        XY[0] -= rectView.left;
        XY[1] -= rectView.top;
        XY[0] = Math.round(XY[0] * imageRatio[0]);
        XY[1] = Math.round(XY[1] * imageRatio[1]);

        return XY;
    }

    private int[] ToInner(int[] XY) {
        //XY = GLtoLC(XY);
        if (XY[0] < 0) {
            XY[0] = 0;
        }
        if (bitmap.getWidth() < XY[0]) {
            XY[0] = bitmap.getWidth();
        }
        if (XY[1] < 0) {
            XY[1] = 0;
        }
        if (bitmap.getHeight() < XY[1]) {
            XY[1] = bitmap.getHeight();
        }
        return XY;
    }

    private void getSize() {
        Bitmap bmp;
        for (int i = 0; i < images.length; i++) {
            bmp = BitmapFactory.decodeFile(images[i].getPath(), options);
            imageSizeW[i] = bmp.getWidth();
            imageSizeH[i] = bmp.getHeight();
        }
    }

    private Boolean fileToBitmap(int imageNumber, boolean by0) {
        try {
            if (saveImageNumber != imageNumber || by0) {
                bitmap = BitmapFactory.decodeFile(images[imageNumber].getPath(), options);
                tmpBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                canvas = new Canvas(bitmap);
            }
            saveImageNumber = imageNumber;
            makeRatio();
        }catch (Throwable e){
            Toast.makeText(activity,"ERROR: TOO BIG SIZE OF IMAGE", Toast.LENGTH_SHORT).show();
            //e.printStackTrace();
            return false;
        }
        return true;
    }

    public void resetCanvas(int imageNumber) {
        tmpBitmap = BitmapFactory.decodeFile(images[imageNumber].getPath(), options).copy(Bitmap.Config.ARGB_8888, true);
        canvas.drawBitmap(tmpBitmap, 0, 0, null);
        imageView.setImageBitmap(tmpBitmap);
    }

    private void makeRatio() {

        float a = bitmap.getWidth();
        float b = imageView.getWidth();
        imageRatio[0] = a / b;

        a = bitmap.getHeight();
        b = imageView.getHeight();
        imageRatio[1] = a / b;
    }

    public boolean drawCrossHair(int imageNumber, int[] XY, int color, int thickness) {
        if(!(fileToBitmap(imageNumber, false)))
            return false;
        //XY = changeImageRatio(XY);

        XY = ToInner(XY);
        crossHairXY[0] = XY[0];
        crossHairXY[1] = XY[1];

        touchPointOnView[0] = XY[0];
        touchPointOnView[1] = XY[1];
        Log.d("CROSSHAIR", XY[0] + " " + XY[1]);

        paintCrossHair.setColor(color);
        paintCrossHair.setStrokeWidth(thickness);

        canvas.drawBitmap(tmpBitmap, 0, 0, null);
        canvas.drawLine(0, XY[1], bitmap.getWidth(), XY[1], paintCrossHair);
        canvas.drawLine(XY[0], 0, XY[0], bitmap.getHeight(), paintCrossHair);
        imageView.setImageBitmap(bitmap);
        return true;

    }

    private int[] changeImageRatio(int[] XY) {
        for (int i = 0; i < XY.length; i++) {
            XY[i] *= imageRatio[i];
        }
        return XY;
    }

    public String drawRectangle(int imageNumber, int[] startXY, int[] endXY, String className, int color, int thickness, boolean bySave) {
        if(!(fileToBitmap(imageNumber, false))) {
            imageView.setImageResource(R.drawable.error_image);
            return null;
        }
        if (!bySave) {
            //startXY = GLtoLC(startXY);
            startXY = ToInner(startXY);
            rectStartXY[0] = startXY[0];
            rectStartXY[1] = startXY[1];

            endXY = GLtoLC(endXY);
            endXY = ToInner(endXY);
            rectEndXY[0] = endXY[0];
            rectEndXY[1] = endXY[1];

            touchPointOnView[0] = endXY[0];
            touchPointOnView[1] = endXY[1];

            paintRect.setStrokeWidth(thickness);
            paintRect.setColor(color);
            paintRect.setTextSize(bitmap.getWidth()/50);

            canvas.drawBitmap(tmpBitmap, 0, 0, null);

            paintRect.setStyle(Paint.Style.FILL_AND_STROKE);
            if (startXY[1] < 11 || endXY[1] < 11) {  //11 is about a letter height
                canvas.drawText(className, startXY[0] + 1, startXY[1] + 11, paintRect);
            } else {
                canvas.drawText(className, startXY[0], startXY[1] - 3, paintRect);
            }
            paintRect.setStyle(Paint.Style.STROKE);
            Log.d("CANVAS", endXY[0] + " " + endXY[1]);
            canvas.drawRect(startXY[0], startXY[1], endXY[0], endXY[1], paintRect);
            imageView.setImageBitmap(bitmap);
            return null;
        } else {
            paintRect.setColor(color);
            paintRect.setStrokeWidth(thickness);
            paintRect.setColor(color);
            paintRect.setTextSize(bitmap.getWidth()/50);
            canvas.drawBitmap(tmpBitmap, 0, 0, null);
            paintRect.setStyle(Paint.Style.FILL_AND_STROKE);
            if (startXY[1] < 11 || endXY[1] < 11) {  //11 is about a height of letters
                canvas.drawText(className, startXY[0] + 1, startXY[1] + 11, paintRect);
            } else {
                canvas.drawText(className, startXY[0], startXY[1] - 3, paintRect);
            }
            paintRect.setStyle(Paint.Style.STROKE);
            Log.d("CANVAS", endXY[0] + " " + endXY[1]);

            touchPointOnView[0] = endXY[0];
            touchPointOnView[1] = endXY[1];

            canvas.drawRect(startXY[0], startXY[1], endXY[0], endXY[1], paintRect);
            imageView.setImageBitmap(bitmap);
            tmpBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);

            data = (startXY[0] + endXY[0]) / ((double) 2 * bitmap.getWidth()) + " " +
                    (startXY[1] + endXY[1]) / ((double) 2 * bitmap.getHeight()) + " " +
                    Math.abs((double) endXY[0] - startXY[0]) / bitmap.getWidth() + " " +
                    Math.abs((double) endXY[1] - startXY[1]) / bitmap.getHeight();

            return data;

        }
    }


}
