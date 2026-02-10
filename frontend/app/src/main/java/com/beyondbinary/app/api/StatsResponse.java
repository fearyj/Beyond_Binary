package com.beyondbinary.app.api;

import com.google.gson.annotations.SerializedName;

public class StatsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("stats")
    private Stats stats;

    public boolean isSuccess() {
        return success;
    }

    public Stats getStats() {
        return stats;
    }

    public static class Stats {
        @SerializedName("totalEvents")
        private int totalEvents;

        @SerializedName("eventTypes")
        private int eventTypes;

        @SerializedName("totalParticipants")
        private int totalParticipants;

        @SerializedName("avgOccupancy")
        private double avgOccupancy;

        public int getTotalEvents() {
            return totalEvents;
        }

        public int getEventTypes() {
            return eventTypes;
        }

        public int getTotalParticipants() {
            return totalParticipants;
        }

        public double getAvgOccupancy() {
            return avgOccupancy;
        }
    }
}
