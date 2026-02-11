package com.beyondbinary.app.data.providers;

import android.content.Context;

public class HealthDataProvider {

    public static class HealthData {
        public int steps;
        public int stepGoal;
        public int heartRate;
        public double sleepHours;
        public int caloriesBurned;
        public int calorieGoal;
        public int activeMinutes;
        public int activeMinuteGoal;
        public double distanceKm;
        public String stressLevel;

        public String getSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== User Health Data (Samsung Health) ===\n");
            sb.append("Daily steps: ").append(steps).append(" / ").append(stepGoal).append(" goal\n");
            sb.append("Resting heart rate: ").append(heartRate).append(" BPM\n");
            sb.append("Average sleep: ").append(sleepHours).append(" hours per night\n");
            sb.append("Calories burned: ").append(caloriesBurned).append(" / ").append(calorieGoal).append(" goal\n");
            sb.append("Active minutes: ").append(activeMinutes).append(" / ").append(activeMinuteGoal).append(" goal\n");
            sb.append("Daily distance: ").append(distanceKm).append(" km\n");
            sb.append("Stress level: ").append(stressLevel).append("\n");
            return sb.toString();
        }
    }

    public static HealthData getHealthData(Context context) {
        HealthData data = new HealthData();
        data.steps = 3200;
        data.stepGoal = 10000;
        data.heartRate = 78;
        data.sleepHours = 5.5;
        data.caloriesBurned = 1400;
        data.calorieGoal = 2200;
        data.activeMinutes = 15;
        data.activeMinuteGoal = 60;
        data.distanceKm = 1.8;
        data.stressLevel = "High";
        return data;
    }
}
