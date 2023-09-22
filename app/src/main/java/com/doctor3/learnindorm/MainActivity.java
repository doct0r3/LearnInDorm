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
import org.json.JSONObject;

import java.io.*;


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

    public void setLocationRoot(String[] strArr, int i) {
        String longitude = "";
        String latitude = "";
        Toast.makeText(MainActivity.this.getApplicationContext(), "正在修改位置到" + strArr[i], Toast.LENGTH_SHORT).show();
        String str4 = strArr[i];
        Log.d("Xposed", "STReq: " + str4.equals("A楼"));

        switch (str4) {
            case "A楼":
                longitude = "108.837088";
                latitude = "34.133223";
                break;
            case "B楼左":
                longitude = "108.837571";
                latitude = "34.132469";
                break;
            case "B楼右":
                longitude = "108.838842";
                latitude = "34.131953";
                break;
            case "C楼":
                longitude = "108.839756";
                latitude = "34.132059";
                break;
            case "D楼":
                longitude = "108.841965";
                latitude = "34.131153";
                break;
            case "信远2区":
                longitude = "108.845561";
                latitude = "34.131568";
                break;
            case "信远1区":
                longitude = "108.846347";
                latitude = "34.130892";
                break;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
        } catch (Exception e) {
            e.printStackTrace();
        }
        File file = new File(mContext.getFilesDir(), "config.json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        final String[] strArr = {"A楼", "B楼", "C楼", "D楼","信远2区","信远1区"};
        this.alert = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        this.builder = builder;
        if (view.getId() == R.id.btn1) {
            AlertDialog create = builder.setIcon(R.mipmap.ic_launcher).setTitle("选择目标地点:").setItems(strArr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    setLocationRoot(strArr, i);
                }
            }).create();
            this.alert = create;
            create.show();

        }


    }
}