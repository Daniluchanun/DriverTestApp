package services;

import java.util.*;

public class SectionCache {
    private static List<Map<String, Object>> cachedSections;

    public static void setSections(List<Map<String, Object>> sections) {
        cachedSections = sections;
    }

    public static List<Map<String, Object>> getSections() {
        return cachedSections;
    }

    public static boolean isLoaded() {
        return cachedSections != null && !cachedSections.isEmpty();
    }
}
