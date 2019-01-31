package com.github.rjbx.sample.data;

import android.content.Context;

import com.github.rjbx.rateraid.Rateraid.RatedObject;
import com.github.rjbx.sample.R;

import java.util.HashMap;
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
    private static Map<Integer, RatedObject<ColorItem>> sSavedItems = new HashMap<>();

    /**
     * A map of sample (color) items, by ID.
     */
    private static final Map<Integer, RatedObject<ColorItem>> ORIGINAL_ITEMS = new HashMap<>();

    private static final int [] COLORS = new int[] {
            R.color.colorCoolLight,
            R.color.colorCool,
            R.color.colorCoolDark,
            R.color.colorCheerLight,
            R.color.colorCheer,
            R.color.colorCheerDark,
            R.color.colorAttention,
            R.color.colorAttentionDark,
            R.color.colorAccent,
            R.color.colorHeatLight,
            R.color.colorHeat,
            R.color.colorHeatDark,
            R.color.colorConversionLight,
            R.color.colorConversion,
            R.color.colorConversionDark,
            R.color.colorComfortLight,
            R.color.colorComfort,
            R.color.colorComfortDark,
            R.color.colorNeutral,
            R.color.colorPrimary,
    };

    private static final int COUNT = COLORS.length;

    static {
        // Add some sample items.
        for (int i = 0; i < COUNT; i++) {
            addItem(createColorItem(i));
        }
    }

    private static void addItem(ColorItem item) {
        ORIGINAL_ITEMS.put(Integer.valueOf(item.id), item);
    }

    private static ColorItem createColorItem(int position) {
        return new ColorItem(
                String.valueOf(position + 1),
                "Item " + (position + 1),
                makeDetails(position),
                0d,
                COLORS[position]
        );
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position + 1);
        for (int i = 0; i < (position + 20); i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    public static Map<Integer, RatedObject<ColorItem>> getOriginalItems() {
        Map<Integer, RatedObject<ColorItem>> clones = new HashMap<>();
        for (RatedObject<ColorItem> ratedObject : ORIGINAL_ITEMS.values())
            clones.put(
                    Integer.parseInt(ratedObject.getObject().getId()),
                    ratedObject.getObject().clone()
            );
        return clones;
    }

    public static Map<Integer, RatedObject<ColorItem>> getSavedItems() { return sSavedItems; }
    public static void setSavedItems(Map<Integer, RatedObject<ColorItem>> items) { sSavedItems = items; }

    /**
     * A color item representing a piece of content.
     */
    public static class ColorItem implements RatedObject<ColorItem>, Cloneable {
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

        public String getId() { return id; }
        public String getContent() {
            return content;
        }
        public String getDetails() {
            return details;
        }
        public double getPercent() { return percent; }
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

        @Override public ColorItem clone() {
            ColorItem clone  = new ColorItem(
                    this.id, this.content, this.details, this.percent, this.colorRes
            );
            try { super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Class must implement Cloneable interface");
            }
            return clone;
        }
    }
}
