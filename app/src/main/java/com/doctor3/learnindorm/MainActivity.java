package com.doctor3.learnindorm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

import java.io.*;
import java.util.regex.Pattern;


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
        ((Button) findViewById(R.id.btn_edicustom)).setOnClickListener(this);
        ((Button) findViewById(R.id.resetDefault)).setOnClickListener(this);
        ((Button) findViewById(R.id.setOaid)).setOnClickListener(this);

    }


    public void setLocationCustom(){
        EditText latitudeText = findViewById(R.id.latitudeText);
        EditText longitudeText = findViewById(R.id.longitudeText);
        EditText addrText = findViewById(R.id.addrText);
        Editable latitudeTxt = longitudeText.getText();
        Editable longitudeTxt = addrText.getText();
        Editable addrTxt = latitudeText.getText();
        JSONObject jsonObject = new JSONObject();
        if (!CheckUtils.isNumeric(latitudeTxt.toString()) || !CheckUtils.isNumeric(longitudeTxt.toString())) {
            try {
                Toast.makeText(MainActivity.this.getApplicationContext(), "正在修改位置到" + latitudeTxt+longitudeTxt, Toast.LENGTH_SHORT).show();
                jsonObject.put("latitude", latitudeTxt);
                jsonObject.put("longitude", longitudeTxt);
                jsonObject.put("address", addrTxt);

            } catch (Exception e) {
                e.printStackTrace();
            }
            File file = new File(mContext.getFilesDir(), "config.json");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(jsonObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(MainActivity.this.getApplicationContext(), "输入无效，请检查", Toast.LENGTH_SHORT).show();
        }

    }

    public void resetDefault(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("latitude", "-1");
            jsonObject.put("longitude", "-1");
            jsonObject.put("address", "null");
        } catch (Exception e) {
            e.printStackTrace();
        }
        File file = new File(mContext.getFilesDir(), "config.json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(MainActivity.this.getApplicationContext(), "已设为不修改位置", Toast.LENGTH_SHORT).show();

    }

    public void setOaid(){

    }
    public void setLocation(String[] strArr, int i) {
        String longitude = "";
        String latitude = "";
        Toast.makeText(MainActivity.this.getApplicationContext(), "正在修改位置到" + strArr[i], Toast.LENGTH_SHORT).show();
        String str4 = strArr[i];
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
            case "信远1区":
                longitude = "108.846347";
                latitude = "34.130892";
                break;
            case "E楼I":
                longitude = "108.84096";
                latitude = "34.131023";
                break;
            case "E楼II":
                longitude = "108.842164";
                latitude = "34.130481";
                break;
            case "E楼III":
                longitude = "108.843776";
                latitude = "34.12976";
                break;

        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            jsonObject.put("address", "中国陕西省西安市长安区兴隆街道西太路西安电子科技大学(南校区)");
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
        final String[] strArr = {"A楼", "B楼左","B楼右", "C楼", "D楼","E楼I","E楼II","E楼III","信远1区","信远2区"};
        this.alert = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        this.builder = builder;
        if (view.getId() == R.id.btn1) {
            AlertDialog create = builder.setIcon(R.mipmap.ic_launcher).setTitle("选择目标地点:").setItems(strArr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    setLocation(strArr, i);
                }
            }).create();
            this.alert = create;
            create.show();
        } else if (view.getId() == R.id.btn_edicustom) {
            setLocationCustom();
        } else if (view.getId() == R.id.resetDefault) {
            resetDefault();
        }else if (view.getId() == R.id.resetDefault) {
            setOaid();
        }


    }
}