package com.beyondbinary.app.data.providers;

import android.content.Context;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;

public class HealthDataProvider {

    public static class HealthData {
        public int steps;
        public int heartRate;
    }

    public static HealthData getHealthData(Context context) {
        try {
            int resId = context.getResources().getIdentifier("mock_health_data", "raw", context.getPackageName());
            InputStream is = context.getResources().openRawResource(resId);
            InputStreamReader reader = new InputStreamReader(is);
            HealthData data = new Gson().fromJson(reader, HealthData.class);
            reader.close();
            return data;
        } catch (Exception e) {
            HealthData fallback = new HealthData();
            fallback.steps = 5000;
            fallback.heartRate = 72;
            return fallback;
        }
    }
}
