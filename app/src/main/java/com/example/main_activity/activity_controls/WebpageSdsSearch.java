package com.example.main_activity.activity_controls;

import android.util.Log;

import com.example.main_activity.backend.Data;
import com.example.main_activity.backend.Qualifiers;
import com.example.main_activity.util.Util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebpageSdsSearch {
    public static String sds_search(Document doc, String response, String url3, Data data1) {
        Log.d("check", "starting sds_search for " + url3);
        String best = "";
        Elements links = doc.getElementsByTag("a");
        //    Log.d("check", "got links");
        String destination = "";
        String text = "";
        int possible_count = 0;
        int best_score = 0;


        for (Element link : links) {
            destination = link.attr("href");
            text = link.text();
            //      Log.d("check", "checking link");
            String[] destination_plus_text = {destination, text};
            if (Qualifiers.possible(destination_plus_text) && possible_count < 10) {
                try {
                    String possible_url = link.attr("abs:href");
                    //              Log.d("check", "url found using absolute method: " + possible_url);
                    if (possible_url.isEmpty()) {
                        possible_url = Util.URLcompleter(link.attr("href"), url3);
                    }
                    if (possible_url == null)
                        break;
                    int score = 0;
                    if (Qualifiers.likely(destination_plus_text))
                        score = score + 2;
                    if (Qualifiers.english(destination_plus_text))
                        score++;
                    if (possible_url.endsWith(".pdf"))
                        score = score + 10;
                    if (score > best_score) {
                        best = possible_url;
                        best_score = score;
                    }
                    possible_count++;
                } catch (Exception ex) {
                    Log.d("error", "try catch error for trying a link");
                }
            }
        }
        if (!best.isEmpty())
            return best;
        else {
            String[] bywords = {"SDS", "sds", "Safety", "safety", "SAFETY", "GHS", "ghs", "infotrac", "complyplus", "Chemical", "compound", "chemical,", "Compound"};
            boolean found = false;
            String s = response;
            String pdf = ".pdf";
            int end_index = 0;
            int start_index = 0;
            for (int i = 0; i < bywords.length; i++) {
                int index = s.indexOf(bywords[i]);
                if (index == -1)
                    continue;
                int j = 0;
                int all_char_count = 0;
                while (j < 150) {
                    if (s.substring(index + all_char_count, index + all_char_count + 4).equals(pdf)) {
                        //              Log.d("check", "found .pdf");
                        end_index = index + all_char_count + 4;
                        for (int k = 0; k < 150; k++) {
                            if (s.charAt(end_index - k - 2) == '\'' || s.charAt(end_index - k - 2) == '\"') {
                                start_index = end_index - k - 1;
                                found = true;
                                break;
                            }
                            if (found)
                                break;
                        }
                    }
                    if (found)
                        break;
                    all_char_count++;
                    if (!s.substring(index + all_char_count, index + all_char_count + 1).equals(" "))
                        j++;
                }
                if (found)
                    break;
            }
            if (found) {
                Log.d("check", "GOT IT " + s.substring(start_index, end_index));
                String sds_url = s.substring(start_index, end_index);
                if (!sds_url.contains("http")) {
                    sds_url = Util.URLcompleter(sds_url, url3);
                }
                return (sds_url);
            }
        }
        Log.d("check", "sds not found on that webpage");
        return (null);
    }
}
