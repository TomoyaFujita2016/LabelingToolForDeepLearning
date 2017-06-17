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

import java.io.File;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by Tomoya on 2017/05/19.
 */

public class CanvasBitmap {

    private Bitmap bitmap, tmpBitmap;
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

    public float[] imageRatio;
    public int[] crossHairXY;
    public int[] rectStartXY, rectEndXY;
    public int[] imageSizeH, imageSizeW;

    CanvasBitmap(File[] images, ImageView imageView, Activity activity) {
        Log.d("Constructor", "Loaded Constructor");

        this.images = images;
        this.imageView = imageView;
        this.activity = activity;

        imageRatio = new float[2];
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

    private void fileToBitmap(int imageNumber) {
        if (saveImageNumber != imageNumber || imageNumber == 0) {
            bitmap = BitmapFactory.decodeFile(images[imageNumber].getPath(), options);
            tmpBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            canvas = new Canvas(bitmap);
        }
        saveImageNumber = imageNumber;
        makeRatio();
    }

    private void makeRatio() {

        float a = bitmap.getWidth();
        float b = imageView.getWidth();
        imageRatio[0] = a / b;

        a = bitmap.getHeight();
        b = imageView.getHeight();
        imageRatio[1] = a / b;
    }

    public void drawCrossHair(int imageNumber, int[] XY, int color, int thickness) {
        fileToBitmap(imageNumber);
        //XY = changeImageRatio(XY);

        XY = ToInner(XY);
        crossHairXY[0] = XY[0];
        crossHairXY[1] = XY[1];

        Log.d("CROSSHAIR", XY[0] + " "+ XY[1]);

        paintCrossHair.setColor(color);
        paintCrossHair.setStrokeWidth(thickness);

        canvas.drawBitmap(tmpBitmap, 0, 0, null);
        canvas.drawLine(0, XY[1], bitmap.getWidth(), XY[1], paintCrossHair);
        canvas.drawLine(XY[0], 0, XY[0], bitmap.getHeight(), paintCrossHair);
        imageView.setImageBitmap(bitmap);


    }

    private int[] changeImageRatio(int[] XY) {
        for (int i = 0; i < XY.length; i++) {
            XY[i] *= imageRatio[i];
        }
        return XY;
    }

    public String drawRectangle(int imageNumber, int[] startXY, int[] endXY, String className, int color, int thickness, boolean bySave) {
        fileToBitmap(imageNumber);

        if (!bySave) {
            //startXY = GLtoLC(startXY);
            startXY = ToInner(startXY);
            rectStartXY[0] = startXY[0];
            rectStartXY[1] = startXY[1];

            endXY = GLtoLC(endXY);
            endXY = ToInner(endXY);
            rectEndXY[0] = endXY[0];
            rectEndXY[1] = endXY[1];

            paintRect.setStrokeWidth(thickness);
            paintRect.setColor(color);

            canvas.drawBitmap(tmpBitmap, 0, 0, null);

            if (startXY[1] < 11 || endXY[1] < 11){  //11 is about a letter height
                canvas.drawText(className, startXY[0] + 1, startXY[1] + 11, paintRect);
            }else{
                canvas.drawText(className, startXY[0], startXY[1]-3, paintRect);
            }

            Log.d("CANVAS", endXY[0] + " " + endXY[1]);
            canvas.drawRect(startXY[0], startXY[1], endXY[0], endXY[1], paintRect);
            imageView.setImageBitmap(bitmap);
            return null;
        } else {
            paintRect.setColor(color);
            paintRect.setStrokeWidth(thickness);
            paintRect.setColor(color);
            canvas.drawBitmap(tmpBitmap, 0, 0, null);

            if (startXY[1] < 11 || endXY[1] < 11){  //11 is about a height of letters
                canvas.drawText(className, startXY[0] + 1, startXY[1] + 11, paintRect);
            }else{
                canvas.drawText(className, startXY[0], startXY[1]-3, paintRect);
            }
            
            Log.d("CANVAS", endXY[0] + " " + endXY[1]);
            canvas.drawRect(startXY[0], startXY[1], endXY[0], endXY[1], paintRect);
            tmpBitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);

            data = (startXY[0]+endXY[0])/((double)2*bitmap.getWidth()) + " " +
                    (startXY[1]+endXY[1])/((double)2*bitmap.getHeight()) + " " +
                    Math.abs((double) endXY[0] - startXY[0])/bitmap.getWidth() + " " +
                    Math.abs((double) endXY[1] - startXY[1])/bitmap.getHeight();
            imageView.setImageBitmap(bitmap);
            return data;

        }
    }


}