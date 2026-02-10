package com.beyondbinary.app.utils;

public class EventCategoryHelper {

    /**
     * Maps event types to broader categories
     */
    public static String getCategoryForEventType(String eventType) {
        if (eventType == null) {
            return "Other";
        }

        String lowerType = eventType.toLowerCase();

        // Sports category
        if (lowerType.contains("soccer") || lowerType.contains("basketball") ||
                lowerType.contains("tennis") || lowerType.contains("volleyball") ||
                lowerType.contains("running") || lowerType.contains("gym") ||
                lowerType.contains("cycling") || lowerType.contains("ping pong") ||
                lowerType.contains("sports") || lowerType.contains("fitness")) {
            return "Sports";
        }

        // Social category
        if (lowerType.contains("coffee") || lowerType.contains("dinner") ||
                lowerType.contains("lunch") || lowerType.contains("bbq") ||
                lowerType.contains("party") || lowerType.contains("social") ||
                lowerType.contains("meetup") || lowerType.contains("networking")) {
            return "Social";
        }

        // Arts & Craft category
        if (lowerType.contains("painting") || lowerType.contains("art") ||
                lowerType.contains("craft") || lowerType.contains("pottery") ||
                lowerType.contains("drawing") || lowerType.contains("photography")) {
            return "Arts & Craft";
        }

        // Outdoor category
        if (lowerType.contains("hiking") || lowerType.contains("beach") ||
                lowerType.contains("outdoor") || lowerType.contains("camping") ||
                lowerType.contains("nature") || lowerType.contains("park")) {
            return "Outdoor";
        }

        // Entertainment category
        if (lowerType.contains("movie") || lowerType.contains("concert") ||
                lowerType.contains("music") || lowerType.contains("theater") ||
                lowerType.contains("show") || lowerType.contains("entertainment")) {
            return "Entertainment";
        }

        // Learning category
        if (lowerType.contains("book") || lowerType.contains("reading") ||
                lowerType.contains("study") || lowerType.contains("language") ||
                lowerType.contains("coding") || lowerType.contains("workshop") ||
                lowerType.contains("class") || lowerType.contains("learning")) {
            return "Learning";
        }

        // Wellness category
        if (lowerType.contains("yoga") || lowerType.contains("meditation") ||
                lowerType.contains("wellness") || lowerType.contains("spa") ||
                lowerType.contains("relax")) {
            return "Wellness";
        }

        // Games category
        if (lowerType.contains("game") || lowerType.contains("board") ||
                lowerType.contains("cards") || lowerType.contains("gaming")) {
            return "Games";
        }

        return "Other";
    }

    /**
     * Gets emoji icon for a category
     */
    public static String getEmojiForCategory(String category) {
        switch (category) {
            case "Sports":
                return "⚽";
            case "Social":
                return "🍽️";
            case "Arts & Craft":
                return "🎨";
            case "Outdoor":
                return "🏞️";
            case "Entertainment":
                return "🎬";
            case "Learning":
                return "📚";
            case "Wellness":
                return "🧘";
            case "Games":
                return "🎮";
            default:
                return "📅";
        }
    }

    /**
     * Gets emoji icon directly from event type
     */
    public static String getEmojiForEventType(String eventType) {
        String category = getCategoryForEventType(eventType);
        return getEmojiForCategory(category);
    }
}
