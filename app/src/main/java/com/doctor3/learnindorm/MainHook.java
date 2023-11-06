package com.doctor3.learnindorm;


import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;
import java.util.Random;


public class MainHook implements IXposedHookLoadPackage {
    Context Main_context = null;
    private final String ENCODE_SCRIPT = "KGZ1bmN0aW9uIHNob3dUaXAoY29udGVudCl7CiAgICAkKCIjdGlwX2NvbnRlbnQiKS5odG1sKGNvbnRlbnQpOwogICAgJCgiLnphbGVydCIpLnNob3coKTsKICAgICQoIi56YWxfYm94Iikuc2hvdygpOwp9KSgiT0sgSXRzIGEgdGVzdCIpOw==";
    final String TAG = "WEBVIEW HOOK:";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.chaoxing.mobile.xuezaixidian")) {
            XposedBridge.log(" has Hooked!");
            XposedBridge.log("inner  => " + lpparam.processName);
            Class ActivityThread = XposedHelpers.findClass("android.app.ActivityThread",lpparam.classLoader);
            XposedBridge.hookAllMethods(ActivityThread, "performLaunchActivity", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object mInitialApplication = (Application) XposedHelpers.getObjectField(param.thisObject,"mInitialApplication");
                    ClassLoader finalCL = (ClassLoader) XposedHelpers.callMethod(mInitialApplication,"getClassLoader");
                    XposedBridge.log("found classload is => "+finalCL.toString());
                    Class BabyMain = (Class)XposedHelpers.callMethod(finalCL,"findClass","com.chaoxing.mobile.webapp.ui.WebAppViewerFragment");
                    hookWebview(lpparam);
                }
            });
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

    public void hookWebview(XC_LoadPackage.LoadPackageParam lpparam){
        XposedBridge.hookAllMethods(
                XposedHelpers.findClass("com.chaoxing.mobile.webapp.ui.WebAppViewerFragment", lpparam.classLoader),
                "r9",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.d("Xposed", "call on onPageFinished  param【0】:  " + param.args[0].getClass().getName());
                        Log.d("Xposed", "call on url " + param.args[1]);
                        try {
                            String js = "var newscript = document.createElement(\"script\");";
                            js += "newscript.src=\"https://cdn.bootcdn.net/ajax/libs/vConsole/3.15.1/vconsole.min.js\";";
                            js += "newscript.onload=function(){vConsole = new VConsole();};";  //xxx()代表js中某方法
                            js += "document.body.appendChild(newscript);";
                            XposedHelpers.callMethod(param.args[0],
                                    "loadUrl",
                                    "javascript:" + js);
                            XposedHelpers.callMethod(param.args[0],
                                    "setWebContentsDebuggingEnabled",
                                    true);
                            js = "var newscript = document.createElement(\"script\");";
                            js += "newscript.src=\"http://127.0.0.1:8080/test.js\";";
                            js += "newscript.onload=function(){main()};";  //xxx()代表js中某方法
                            js += "document.body.appendChild(newscript);";
                            XposedHelpers.callMethod(param.args[0],
                                    "loadUrl",
                                    "javascript:" + js);
                        } catch (Throwable e) {
                            Log.e(TAG, "调用loadUrl error " + e.getMessage());
                        }

                    }
                }
        );
    }

}
