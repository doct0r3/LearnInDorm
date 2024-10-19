package com.doctor3.learnindorm;


import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

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
    final String TAG = "Xposed:";

    private void hookBDLocation(XC_LoadPackage.LoadPackageParam lpparam) {
        final Class<?> BDLoc = XposedHelpers.findClass("com.baidu.location.BDLocation", lpparam.classLoader);
        XposedBridge.hookAllMethods(BDLoc, "getMockGnssStrategy", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(0);
            }
        });
        XposedBridge.hookAllMethods(BDLoc, "getMockGnssProbability", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(-1);
            }
        });
        XposedBridge.hookAllMethods(BDLoc, "getLocType", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(161);
            }
        });
        XposedBridge.hookAllMethods(BDLoc, "getAddrStr", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult("中国陕西省西安市长安区兴隆街道西太路西安电子科技大学(南校区)");
            }
        });


    }

    private void loadDebugHooks(XC_LoadPackage.LoadPackageParam lpparam) {
        final Class<?> a = XposedHelpers.findClass("zp.b", lpparam.classLoader);
        XposedBridge.hookAllMethods(a, "F", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.i(TAG, "Method F called:" + param.args[0]);
            }
        });
    }

    private void loadOaidHooks(XC_LoadPackage.LoadPackageParam lpparam) {
        final Class<?> a = XposedHelpers.findClass("zp.b", lpparam.classLoader);
        XposedBridge.hookAllMethods(a, "F", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                ContentResolver resolver = Main_context.getContentResolver();
                String oaid = null;
                Uri uri = Uri.parse("content://com.doctor3.learnindorm.dcprovider/");
                Cursor cursor = resolver.query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int json = cursor.getColumnIndex("json");
                    String jsonData = cursor.getString(json);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        oaid = jsonObject.getString("oaid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "Replace oaid:" + oaid);
                    param.args[0] = oaid;
                }

            }
        });
    }

    private void replaceUserAgent(WebView webView) {
        WebSettings webSettings= webView.getSettings();
        ContentResolver resolver = Main_context.getContentResolver();
        String ua = null;
        Uri uri = Uri.parse("content://com.doctor3.learnindorm.dcprovider/");
        Cursor cursor = resolver.query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int json = cursor.getColumnIndex("json");
            String jsonData = cursor.getString(json);
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                ua = jsonObject.getString("user-agent");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "Replace user agent:" + ua);
            webSettings.setUserAgentString(ua);
        }

    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.chaoxing.mobile.xuezaixidian")) {

            Class ActivityThread = XposedHelpers.findClass("android.app.ActivityThread", lpparam.classLoader);
            XposedBridge.hookAllMethods(ActivityThread, "performLaunchActivity", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object mInitialApplication = (Application) XposedHelpers.getObjectField(param.thisObject, "mInitialApplication");
                    ClassLoader finalCL = (ClassLoader) XposedHelpers.callMethod(mInitialApplication, "getClassLoader");
                    Class BabyMain = (Class) XposedHelpers.callMethod(finalCL, "findClass", "com.chaoxing.mobile.webapp.ui.WebAppViewerFragment");
                    hookWebview(lpparam);
//                    loadDebugHooks(lpparam);
                    hookBDLocation(lpparam);
                    loadOaidHooks(lpparam);
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
                            case "mockData":
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("strategy", 0);
                                jsonObject.put("probability", -1);
                                param.args[1] = jsonObject;
                                break;
                            default:
                                break;
                        }
                    }
                }
            });
        }
    }

    public void hookWebview(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedBridge.hookAllMethods(
                XposedHelpers.findClass("com.chaoxing.mobile.webapp.ui.WebAppViewerFragment", lpparam.classLoader),
                "ca",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.d("Xposed", "call on onPageFinished  param【0】:  " + param.args[0].getClass().getName());
                        Log.d("Xposed", "call on url " + param.args[1]);
                        try {
                            String js = "var newscript = document.createElement(\"script\");";
                            js += "newscript.src=\"https://cdn.jsdelivr.net/npm/vconsole@3.15.1/dist/vconsole.min.js\";";
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

                            WebView webView = (WebView) param.args[0];
                            replaceUserAgent(webView);
                        } catch (Throwable e) {
                            Log.e(TAG, "调用loadUrl error " + e.getMessage());
                        }

                    }
                }
        );
    }

}
