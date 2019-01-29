package com.github.rjbx.sample.data;

import android.content.Context;

import com.github.rjbx.rateraid.Rateraid;
import com.github.rjbx.sample.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ColorData {

    /**
     * A saved array of sample (color) items.
     */
    private static List<ColorItem> sSavedItems = new ArrayList<ColorItem>();

    /**
     * A map of sample (color) items, by ID.
     */
    private static final Map<String, ColorItem> ITEM_MAP = new HashMap<String, ColorItem>();

    private static final int [] COLORS = new int[] {
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
            R.color.colorPrimary,
    };

    private static final int COUNT = COLORS.length;

    static {
        // Add some sample items.
        for (int i = 0; i < COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(ColorItem item) {
        ITEM_MAP.put(item.id, item);
    }

    private static ColorItem createDummyItem(int position) {
        return new ColorItem(
                String.valueOf(position),
                "Item " + position,
                makeDetails(position),
                0d,
                COLORS[position]
        );
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < (position + 20); i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    public static Map<String, ColorItem> getItemMap() { return new HashMap<>(ITEM_MAP); }
    public static List<ColorItem> getItemMapValues() { return new ArrayList<>(ITEM_MAP.values()); }

    public static List<ColorItem> getSavedItems() { return sSavedItems; }
    public static void setSavedItems(List<ColorItem> items) { sSavedItems = items; }


    /**
     * A color item representing a piece of content.
     */
    public static class ColorItem implements Rateraid.RatedObject<ColorItem> {
        private String id;
        private String content;
        private String details;
        private double percent;
        private int colorRes;

        private ColorItem(String id, String content, String details, double percent, int colorRes) {
            this.id = id;
            this.content = content;
            this.details = details;
            this.percent = percent;
            this.colorRes = colorRes;
        }

        @Override
        public ColorItem getObject() {
            return this;
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
        public int getColorRes() { return colorRes; }
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
        public void setColorRes(int colorRes) {
            this.colorRes = colorRes;
        }

        @Override public String toString() { return content; }
        public String colorResToString(Context context) {
            return String.format(
                Locale.getDefault(),
                "#%06X", 0xFFFFFF & context.getResources().getColor(colorRes)
            );
        }
    }
}
