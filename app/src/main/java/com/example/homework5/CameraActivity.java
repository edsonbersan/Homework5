package com.example.homework5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


/**
 * As you may guess, yes, I used lab 8 code too
 */
public class CameraActivity extends AppCompatActivity {

    private ImageView imageView;
    MyCanvas myCanvas;

    Button redB, greenB, blueB, undoB, clearB, doneB;

    Bitmap imageBitmap;

    public ConstraintLayout constraintLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ////// FINDING IDS //////////////////
        myCanvas = findViewById(R.id.myCanvas);
        myCanvas.setActivity(this);

        redB = findViewById(R.id.redButton);
        greenB = findViewById(R.id.greenButton);
        blueB = findViewById(R.id.blueButton);
        undoB = findViewById(R.id.undoButton);
        clearB = findViewById(R.id.clearButton);
        doneB = findViewById(R.id.doneButton);

        constraintLayout = findViewById(R.id.constraintLayoutBottom);

        imageView = findViewById(R.id.picture);
        String currentPhotoPath = getIntent().getStringExtra(ButtonActivity.PICTURE_KEY);

        imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);

        imageView.setImageBitmap(imageBitmap);


        redB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCanvas.setPaintFieldColor(Color.RED);
            }
        });

        greenB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCanvas.setPaintFieldColor(Color.GREEN);
            }
        });

        blueB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCanvas.setPaintFieldColor(Color.BLUE);
            }
        });


        undoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCanvas.undoPath();
            }
        });

        clearB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCanvas.clearPath();
            }
        });

        doneB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toNextActivity = new Intent(getBaseContext(), ButtonActivity.class);
                startActivity(toNextActivity);
            }
        });
    } // ON CREATE END
}