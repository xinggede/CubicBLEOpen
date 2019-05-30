package com.xingge.carble.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.xingge.carble.base.CBApp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 本地存储
 *
 * @author 星
 */
public class Prefs {

    private static Prefs instance;

    public synchronized static Prefs getInstance(Context context) {
        if (instance == null) {
            new Prefs(context);
        }
        return instance;
    }

    public synchronized static Prefs getInstance() {
        if (instance == null) {
            new Prefs(CBApp.getInstance());
        }
        return instance;
    }

    private SharedPreferences sharedPreferences;

    public Prefs(Context context) {
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        instance = this;
    }

    public synchronized void clear() {
        Editor editor = sharedPreferences.edit();
        editor.clear().apply();
    }

    public synchronized void remove(String key) {
        Editor editor = sharedPreferences.edit();
        editor.remove(key).apply();
    }

    public synchronized boolean getBoolean(String key, boolean value) {
        return sharedPreferences.getBoolean(key, value);
    }

    public synchronized int getInt(String key, int value) {
        return sharedPreferences.getInt(key, value);
    }

    public synchronized long getLong(String key, long value) {
        return sharedPreferences.getLong(key, value);
    }

    public synchronized float getFloat(String key, float value) {
        return sharedPreferences.getFloat(key, value);
    }

    public synchronized String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public synchronized String getString(String key, String value) {
        return sharedPreferences.getString(key, value);
    }

    public synchronized boolean saveBoolean(String key, boolean value) {
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public synchronized boolean saveInt(String key, int value) {
        Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public synchronized boolean saveLong(String key, long value) {
        Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public synchronized boolean saveFloat(String key, float value) {
        Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    public synchronized boolean saveString(String key, String value) {
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public synchronized void saveObject(String key, Object value) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    byteArrayOutputStream);
            objectOutputStream.writeObject(value);
            String serStr = byteArrayOutputStream.toString("ISO-8859-1");
            serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
            objectOutputStream.close();
            byteArrayOutputStream.close();
            saveString(key, serStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized Object getObject(String key) {
        Object object = new Object();
        try {
            String redStr = java.net.URLDecoder.decode(getString(key, ""), "UTF-8");
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                    redStr.getBytes("ISO-8859-1"));
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    byteArrayInputStream);
            object = objectInputStream.readObject();
            objectInputStream.close();
            byteArrayInputStream.close();
            return object;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }
}
