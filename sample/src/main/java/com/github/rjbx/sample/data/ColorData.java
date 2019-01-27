package com.github.rjbx.sample.data;

import com.github.rjbx.sample.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ColorData {

    /**
     * An array of sample (color) items.
     */
    public static final List<ColorItem> ITEMS = new ArrayList<ColorItem>();

    /**
     * A map of sample (color) items, by ID.
     */
    public static final Map<String, ColorItem> ITEM_MAP = new HashMap<String, ColorItem>();

    public static final int [] OPTIONS = new int[] {
            R.color.colorCoolLight,
            R.color.colorCool,
            R.color.colorCoolDark,
            R.color.colorCheer,
            R.color.colorCheerDark,
            R.color.colorAttention,
            R.color.colorAttentionDark,
            R.color.colorAccent,
            R.color.colorHeat,
            R.color.colorHeatDark,
            R.color.colorConversion,
            R.color.colorConversionDark,
            R.color.colorComfort,
            R.color.colorComfortDark,
            R.color.colorNeutral,
            R.color.colorNeutralDark,
            R.color.colorPrimary,
            R.color.colorPrimaryDark
    };

    private static final int COUNT = OPTIONS.length;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(ColorItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static ColorItem createDummyItem(int position) {
        return new ColorItem(
                String.valueOf(position),
                "Item " + position,
                makeDetails(position),
                0d);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A color item representing a piece of content.
     */
    public static class ColorItem {
        public String id;
        public String content;
        public String details;
        public double percent;

        public ColorItem(String id, String content, String details, double percent) {
            this.id = id;
            this.content = content;
            this.details = details;
            this.percent = percent;
        }

        public String getId() {
            return id;
        }
        public String getContent() {
            return content;
        }
        public String getDetails() {
            return details;
        }
        public double getPercent() {
            return percent;
        }
        public void setId(String id) {
            this.id = id;
        }
        public void setContent(String content) {
            this.content = content;
        }
        public void setDetails(String details) {
            this.details = details;
        }
        public void setPercent(double percent) {
            this.percent = percent;
        }

        @Override public String toString() {
            return content;
        }
    }
}
