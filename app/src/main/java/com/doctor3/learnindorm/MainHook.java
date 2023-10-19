package com.doctor3.learnindorm;


import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;


public class MainHook implements IXposedHookLoadPackage {
    Context Main_context = null;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.chaoxing.mobile.xuezaixidian")) {
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Main_context = (Context) param.args[0];
                }
            });
            final Class<?> jsonClass = XposedHelpers.findClass("org.json.JSONObject", lpparam.classLoader);
            XposedBridge.hookAllMethods(jsonClass, "put", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String latitude = null;
                    String longitude = null;
                    String addr = "中国陕西省西安市长安区兴隆街道西太路西安电子科技大学(南校区)";
                    ContentResolver resolver = Main_context.getContentResolver();
                    Uri uri = Uri.parse("content://com.doctor3.learnindorm.lcprovider/");
                    Cursor cursor = resolver.query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int json = cursor.getColumnIndex("json");
                        String jsonData = cursor.getString(json);
                        try {
                            JSONObject jsonObject = new JSONObject(jsonData);
                            latitude = jsonObject.getString("latitude");
                            longitude = jsonObject.getString("longitude");
                            addr = jsonObject.getString("address");
                            Log.d("Xposed", "Got lati: " + latitude + ", long: " + longitude);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (param.args.length == 2 && !latitude.equals("-1") && !longitude.equals("-1") && !addr.equals("null")) {
                        Random random = new Random();
                        String key = (String) param.args[0];
                        switch (key) {
                            case "latitude":
                                double randomLatitude = Double.parseDouble(latitude) + (random.nextDouble() - 0.3) * 0.0001;
                                param.args[1] = randomLatitude;
                                break;
                            case "longitude":
                                double randomLongitude = Double.parseDouble(longitude) + (random.nextDouble() - 0.3) * 0.0001;
                                param.args[1] = randomLongitude;
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

}
