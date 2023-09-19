package com.doctor3.learnindorm;


import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XCallback;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


import static android.content.Context.MODE_PRIVATE;

public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        final Class<?> jsonClass = XposedHelpers.findClass("org.json.JSONObject", lpparam.classLoader);
        XposedBridge.hookAllMethods(jsonClass, "put", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String rootPath = android.os.Environment.getExternalStorageDirectory().toString();
                String path = rootPath + "/Android/data/" + "com.chaoxing.mobile.xuezaixidian" + "/files/";
                String filename = "latitude";
                String filename1 = "longitude";
                FileInputStream inputStream;

                File directory = new File(path);
                if (!directory.exists()) {
                    path= Environment.getExternalStorageDirectory().toString() + "/Android/obb/io.virtualapp.sandvxposed64/scopedStorage/com.chaoxing.mobile.xuezaixidian/files/";
                    directory = new File(path);
                }

                File file = new File(directory, filename);

                StringBuilder stringBuilder = new StringBuilder();
                try {
                    inputStream = new FileInputStream(file);
                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        stringBuilder.append(new String(buffer, 0, bytesRead));
                    }

                    inputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                File file1 = new File(directory, filename1);
                Log.d("Xposed", "File: "+directory );
                StringBuilder stringBuilder1 = new StringBuilder();
                try {
                    inputStream = new FileInputStream(file1);
                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        stringBuilder1.append(new String(buffer, 0, bytesRead));
                    }

                    inputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                String latitude = stringBuilder.toString();
                String longitude = stringBuilder1.toString();
                String addr = "中国陕西省西安市长安区兴隆街道西太路西安电子科技大学(南校区)";
                if (param.args.length == 2) {
                    String key = (String) param.args[0];
                    Object value = param.args[1];
                    Log.d("Xposed", "Key: " + key + ", Value: " + value);

                    final String[] choice = {null};
                    switch (key) {
                        case "latitude":
                            param.args[1] = Double.valueOf(latitude);
                            break;
                        case "longitude":
                            param.args[1] = Double.valueOf(longitude);
                            break;
                        case "address":
                            param.args[1] = addr;
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

}
