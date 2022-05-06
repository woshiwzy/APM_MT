package com.sand.apm.mt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.commontech.basemodule.utils.KLog;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private ArrayList<SandObjectClass> sandsList = new ArrayList<>();


    private ImageView imageView;
    private Button buttonTest, buttonTest2;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        buttonTest = findViewById(R.id.buttonTest);

        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Test.method1();
//                    }
//                }).start();

                Bitmap bmap = BitmapFactory.decodeResource(getResources(), R.drawable.m);

                int bWidth = bmap.getWidth();
                int bHeight = bmap.getHeight();

                int nativeCount = bmap.getAllocationByteCount();
                int byteCount = bmap.getByteCount();
                int rotBytes = bmap.getRowBytes();


                bitmaps.add(bmap);
                imageView.setImageBitmap(bmap);
                buttonTest.setText(String.valueOf(++count));

                for (int i = 0; i < 10; i++) {
                    SandObjectClass sandObjectClas = new SandObjectClass();
                    sandsList.add(sandObjectClas);
                    KLog.d(App.tag, "自定义Object:" + sandObjectClas.toString());
                }

//                String info= ClassLayout.parseClass(SandObjectClass.class).toPrintable();
//                KLog.d(App.tag,"自定义类占用信息:"+info);
                KLog.d(App.tag, "bitmap hash:" + bmap.toString() + " hash code:" + bmap.hashCode() + " ==> size:" + nativeCount);

            }
        });

        buttonTest2 = findViewById(R.id.buttonTest2);
        buttonTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testDelay();
            }
        });



    }


    public void testDelay() {
        try {
            Thread.sleep(5* 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    //内存警告，或者所有的view,被隐藏的时候回调
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        //TRIM_MEMORY_UI_HIDDEN //所有的UI隐藏时候调用
    }
}