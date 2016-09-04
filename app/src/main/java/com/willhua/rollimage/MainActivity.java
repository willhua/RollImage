package com.willhua.rollimage;

import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RollImageView mRollImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRollImageView = (RollImageView)findViewById(R.id.roll);
        mRollImageView.setImageLoader(new DefaultImageLoader(RollImageView.SHOW_CNT));
        mRollImageView.setCellCalculator(new DefaultCellCalculator(RollImageView.SHOW_CNT));
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> paths = queryImages();
                LOG("queryimages cnt:" + paths.size());
                mRollImageView.setImagePaths(paths);
            }
        }).start();
    }


    private List<String> queryImages(){
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.ImageColumns.DATA},  null, null, null);
        List<String> paths = new ArrayList<>();
        try {
            while(cursor.moveToNext()){
                String path = cursor.getString(0);
                if(path != null){
                    paths.add(path);
                }
            }
            cursor.close();
        } catch (Exception e){

        }
        return  paths;
    }

    private void LOG(String msg){
        Log.d("MainActivity", "willhua: " + msg);
    }
}
