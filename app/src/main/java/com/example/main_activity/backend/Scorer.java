package com.example.main_activity.backend;

import android.util.Log;

public class Scorer {
    public static int scorer(String url, Data data1){
        String[] link_array = {url};
        int score = 0;
        String score_reasons="";
        if (Qualifiers.likely(link_array)) {
            score = score + 3;
            score_reasons+="likely=3, ";
        }
        if (Qualifiers.english(link_array)){
            score++;
            score_reasons+="english=1, ";
        }
        if (url.endsWith(".pdf")){
            score = score + 10;
            score_reasons+=".pdf=10, ";
        }
        String[] to_compare = {data1.global_name, url};
        score += Qualifiers.compare(to_compare);
        score_reasons+="compare: "+Qualifiers.compare(to_compare);
        score -= data1.retrieve_requests;
        score_reasons+=", retrieve_requests=minus "+data1.retrieve_requests;
        Log.d("check", "results for url: " + url + " : " + score+", reasons: "+score_reasons);
        return score;
    }
}
