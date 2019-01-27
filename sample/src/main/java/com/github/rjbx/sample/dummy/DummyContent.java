package com.github.rjbx.sample.dummy;

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
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int position) {
        return new DummyItem(String.valueOf(position), "Item " + position, makeDetails(position), 0d);
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
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public String id;
        public String content;
        public String details;
        public double percent;

        public DummyItem(String id, String content, String details, double percent) {
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
