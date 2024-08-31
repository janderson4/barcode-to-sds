package com.example.main_activity.backend;

import com.example.main_activity.util.Util;

public class Qualifiers {
    public static boolean possible(String[] terms) {
        boolean contains = false;
        String[] bywords = {"SDS", "sds", "Safety", "safety", "SAFETY", "GHS", "ghs", "infotrac", "complyplus", "Chemical", "compound", "chemical,", "Compound"};
        for (int i = 0; i < terms.length; i++) {
            if (Util.stringContainsItemFromList(terms[i], bywords) && !terms[i].contains("Consumer")) {
                contains = true;
            }
        }
        return contains;
    }

    public static boolean likely(String[] terms) {
        boolean contains = false;
        String[] bywords = {"SDS", "sds", "safetydatasheet", "SAFETYDATASHEET", "SafetyDataSheet", "Safety Data Sheet", "safety data sheet", "SAFETY DATA SHEET"};
        for (int i = 0; i < terms.length; i++) {
            if (Util.stringContainsItemFromList(terms[i], bywords)) {
                contains = true;
            }
        }
        return contains;
    }

    public static boolean english(String[] terms) {
        boolean contains = false;
        String[] bywords = {"English, EN, en"};
        for (int i = 0; i < terms.length; i++) {
            if (Util.stringContainsItemFromList(terms[i], bywords)) {
                contains = true;
            }
        }
        return contains;
    }

    public static int compare(String[] terms) {
        String[] desired_words = terms[0].split(" ");
        int count = 0;
        for (int last_strings=1;last_strings<terms.length;last_strings++) {
            for (int i = 0; i < desired_words.length; i++) {
                if (desired_words[i].length() > 4 && terms[last_strings].contains(desired_words[i]))
                    count++;
            }
        }
        return count;
    }
}
