package com.doctor3.learnindorm;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LocationConfigProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Context context = getContext();
        File file = new File(context.getFilesDir(), "config.json");
        StringBuilder content = new StringBuilder();
        String latitude =null;
        String longitude = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            JSONObject jsonObject = new JSONObject(content.toString());
            latitude = jsonObject.getString("latitude");
            longitude = jsonObject.getString("longitude");
        } catch (IOException | org.json.JSONException e) {
            e.printStackTrace();
        }
        MatrixCursor cursor = new MatrixCursor(new String[]{"json"});
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cursor.addRow(new Object[]{jsonObject.toString()});
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
