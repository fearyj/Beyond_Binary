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
     * Returns specific emojis for specific event types, falls back to category emoji
     */
    public static String getEmojiForEventType(String eventType) {
        if (eventType == null) {
            return "📅";
        }

        String lowerType = eventType.toLowerCase();

        // Sports - specific types
        if (lowerType.contains("soccer") || lowerType.contains("football")) return "⚽";
        if (lowerType.contains("basketball")) return "🏀";
        if (lowerType.contains("tennis")) return "🎾";
        if (lowerType.contains("volleyball")) return "🏐";
        if (lowerType.contains("running") || lowerType.contains("marathon")) return "🏃";
        if (lowerType.contains("gym") || lowerType.contains("fitness") || lowerType.contains("workout")) return "💪";
        if (lowerType.contains("cycling") || lowerType.contains("bike")) return "🚴";
        if (lowerType.contains("swimming") || lowerType.contains("pool")) return "🏊";
        if (lowerType.contains("ping pong") || lowerType.contains("table tennis")) return "🏓";
        if (lowerType.contains("badminton")) return "🏸";
        if (lowerType.contains("baseball")) return "⚾";
        if (lowerType.contains("golf")) return "⛳";
        if (lowerType.contains("boxing")) return "🥊";
        if (lowerType.contains("skiing") || lowerType.contains("ski")) return "⛷️";
        if (lowerType.contains("skating") || lowerType.contains("ice")) return "⛸️";

        // Social - specific types
        if (lowerType.contains("coffee")) return "☕";
        if (lowerType.contains("dinner")) return "🍽️";
        if (lowerType.contains("lunch")) return "🥗";
        if (lowerType.contains("breakfast") || lowerType.contains("brunch")) return "🥞";
        if (lowerType.contains("bbq") || lowerType.contains("barbecue")) return "🍖";
        if (lowerType.contains("party")) return "🎉";
        if (lowerType.contains("drinks") || lowerType.contains("bar") || lowerType.contains("beer")) return "🍻";
        if (lowerType.contains("pizza")) return "🍕";
        if (lowerType.contains("networking")) return "🤝";
        if (lowerType.contains("birthday")) return "🎂";
        if (lowerType.contains("picnic")) return "🧺";

        // Arts & Craft - specific types
        if (lowerType.contains("painting") || lowerType.contains("paint")) return "🎨";
        if (lowerType.contains("drawing") || lowerType.contains("sketch")) return "✏️";
        if (lowerType.contains("photography") || lowerType.contains("photo")) return "📷";
        if (lowerType.contains("pottery") || lowerType.contains("ceramics")) return "🏺";
        if (lowerType.contains("craft")) return "✂️";
        if (lowerType.contains("sculpture")) return "🗿";

        // Outdoor - specific types
        if (lowerType.contains("hiking") || lowerType.contains("hike")) return "🥾";
        if (lowerType.contains("beach")) return "🏖️";
        if (lowerType.contains("camping") || lowerType.contains("camp")) return "🏕️";
        if (lowerType.contains("nature") || lowerType.contains("walk")) return "🌳";
        if (lowerType.contains("park")) return "🏞️";
        if (lowerType.contains("climbing") || lowerType.contains("boulder")) return "🧗";
        if (lowerType.contains("fishing")) return "🎣";
        if (lowerType.contains("kayak") || lowerType.contains("canoe")) return "🛶";

        // Entertainment - specific types
        if (lowerType.contains("movie") || lowerType.contains("film") || lowerType.contains("cinema")) return "🎬";
        if (lowerType.contains("concert") || lowerType.contains("music") || lowerType.contains("band")) return "🎵";
        if (lowerType.contains("theater") || lowerType.contains("theatre") || lowerType.contains("play")) return "🎭";
        if (lowerType.contains("comedy") || lowerType.contains("standup")) return "😂";
        if (lowerType.contains("karaoke")) return "🎤";
        if (lowerType.contains("dance") || lowerType.contains("dancing")) return "💃";

        // Wellness - specific types (checked before Learning so "Yoga Class" → 🧘 not 🎓)
        if (lowerType.contains("yoga")) return "🧘";
        if (lowerType.contains("meditation")) return "🧘‍♀️";
        if (lowerType.contains("spa") || lowerType.contains("massage")) return "💆";
        if (lowerType.contains("wellness") || lowerType.contains("health")) return "💚";

        // Learning - specific types
        if (lowerType.contains("book") || lowerType.contains("reading")) return "📚";
        if (lowerType.contains("study")) return "📖";
        if (lowerType.contains("language")) return "🗣️";
        if (lowerType.contains("coding") || lowerType.contains("programming")) return "💻";
        if (lowerType.contains("workshop") || lowerType.contains("seminar")) return "👨‍🏫";
        if (lowerType.contains("class")) return "🎓";

        // Games - specific types
        if (lowerType.contains("board game") || lowerType.contains("boardgame")) return "🎲";
        if (lowerType.contains("card") || lowerType.contains("poker")) return "🃏";
        if (lowerType.contains("video game") || lowerType.contains("gaming")) return "🎮";
        if (lowerType.contains("chess")) return "♟️";

        // Food specific
        if (lowerType.contains("cooking") || lowerType.contains("baking")) return "👨‍🍳";
        if (lowerType.contains("wine") || lowerType.contains("tasting")) return "🍷";

        // Volunteer/Community
        if (lowerType.contains("volunteer") || lowerType.contains("charity")) return "🤲";
        if (lowerType.contains("cleanup") || lowerType.contains("clean up")) return "🧹";

        // Fall back to category emoji if no specific match
        String category = getCategoryForEventType(eventType);
        return getEmojiForCategory(category);
    }
}
