package com.doctor3.learnindorm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private boolean[] checkItems;
    private Context mContext;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        this.mContext = this;
        bindView();
    }

    private void bindView() {
        ((Button) findViewById(R.id.btn1)).setOnClickListener(this);
    }
    public void setLocationRoot(String[] strArr,int i ){
        String longitude = "";
        String latitude = "";
        Toast.makeText(MainActivity.this.getApplicationContext(), "正在修改位置到" + strArr[i], Toast.LENGTH_SHORT ).show();
        String str4 = strArr[i];
        String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.chaoxing.mobile.xuezaixidian/files/";
        switch (str4) {
            case "A楼":
                longitude = "108.837088";
                latitude = "34.133223";
                break;
            case "B楼":
                longitude = "108.838713";
                latitude = "34.132203";
                break;
            case "C楼":
                longitude = "108.839756";
                latitude = "34.132059";
                break;
            case "D楼":
                longitude = "108.841965";
                latitude = "34.131153";
                break;
        }

        try {
            String format = String.format("su -c echo %s > %s%s", latitude, path, "latitude");
            String format2 = String.format("su -c echo %s > %s%s", longitude, path, "longitude");
            Process exec = Runtime.getRuntime().exec(format);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    Log.d("Xposed", "Creating File: " + readLine);
                } else {
                    Process exec2 = Runtime.getRuntime().exec(format2);
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(exec2.getInputStream()));
                    while (true) {
                        String readLine2 = bufferedReader2.readLine();
                        if (readLine2 != null) {
                            Log.d("Xposed", "Creating File: " + readLine2);
                        } else {
                            exec.waitFor();
                            exec2.waitFor();
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setLocationVXP(String[] strArr,int i ){
        String longitude = "";
        String latitude = "";
        Toast.makeText(MainActivity.this.getApplicationContext(), "正在修改位置到" + strArr[i], Toast.LENGTH_SHORT ).show();
        String str4 = strArr[i];
        String path = Environment.getExternalStorageDirectory().toString() + "/Android/obb/io.virtualapp.sandvxposed64/scopedStorage/com.chaoxing.mobile.xuezaixidian/files/";
        switch (str4) {
            case "A楼":
                longitude = "108.837088";
                latitude = "34.133223";
                break;
            case "B楼":
                longitude = "108.838713";
                latitude = "34.132203";
                break;
            case "C楼":
                longitude = "108.839756";
                latitude = "34.132059";
                break;
            case "D楼":
                longitude = "108.841965";
                latitude = "34.131153";
                break;
        }

        try {
            String format = String.format("su -c echo %s > %s%s", latitude, path, "latitude");
            String format2 = String.format("su -c echo %s > %s%s", longitude, path, "longitude");
            Process exec = Runtime.getRuntime().exec(format);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    Log.d("Xposed", "Creating File: " + readLine);
                } else {
                    Process exec2 = Runtime.getRuntime().exec(format2);
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(exec2.getInputStream()));
                    while (true) {
                        String readLine2 = bufferedReader2.readLine();
                        if (readLine2 != null) {
                            Log.d("Xposed", "Creating File: " + readLine2);
                        } else {
                            exec.waitFor();
                            exec2.waitFor();
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        final String[] strArr = {"A楼", "B楼", "C楼", "D楼"};
        this.alert = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        this.builder = builder;
        if (view.getId() == R.id.btn1) {

            AlertDialog create = builder.setIcon(R.mipmap.ic_launcher).setTitle("选择目标地点:").setItems(strArr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    setLocationRoot(strArr,i);
                }
            }).create();
            this.alert = create;
            create.show();

        } else if (view.getId() == R.id.btn2) {
            AlertDialog create = builder.setIcon(R.mipmap.ic_launcher).setTitle("选择目标地点:").setItems(strArr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    setLocationVXP(strArr,i);
                }
            }).create();
            this.alert = create;
            create.show();
        }


    }
}