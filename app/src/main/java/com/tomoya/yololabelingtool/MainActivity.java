package com.tomoya.yololabelingtool;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
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

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity implements View.OnClickListener{
    private Button changeClassButton, clearButton;
    private ImageButton configButton, nextButton, backButton;
    private ImageView imageView;
    private LinearLayout listLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializationANDsetOnClickListener();



    }
    private void initializationANDsetOnClickListener(){
        changeClassButton = (Button)findViewById(R.id.changeClassbtn);
        clearButton = (Button) findViewById(R.id.clearBtn);
        configButton = (ImageButton) findViewById(R.id.configBtn);
        nextButton = (ImageButton) findViewById(R.id.nextBtn);
        backButton = (ImageButton) findViewById(R.id.backBtn);
        imageView = (ImageView) findViewById(R.id.iv);
        listLinearLayout = (LinearLayout)findViewById(R.id.displayLL);
        changeClassButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        configButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){

        switch (view.getId()){
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

    private void onChangeClassBtn(){
        Log.d(TAG, "Clicked onChangeButton");
    }
    private void onClearBtn(){
        Log.d(TAG, "Clicked onClearButton");
        listLinearLayout.removeAllViews();

    }
    private void onConfigBtn(){
        Log.d(TAG, "Clicked onConfigButton");
        Intent intent = new Intent(this, ConfigActivity.class);
        startActivity(intent);

    }
    private void onNextBtn(){
        Log.d(TAG, "Clicked onNextButton");

    }
    private void onBackBtn(){
        Log.d(TAG, "Clicked onBackButton");

    }
}
