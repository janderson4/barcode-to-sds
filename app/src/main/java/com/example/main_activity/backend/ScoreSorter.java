package com.example.main_activity.backend;

import android.util.Log;

import com.example.main_activity.backend.Data;

public class ScoreSorter {
    public static void sort_scores(Data data1){
        int temp_score;
        String temp_url;
        String print="";
        for (int i=0;i<data1.retrieved_top_scores.length;i++){
            for (int j=i+1;j<data1.retrieved_top_scores.length;j++){
                if (data1.retrieved_top_scores[i]<data1.retrieved_top_scores[j]){
                    temp_score=data1.retrieved_top_scores[i];
                    temp_url=data1.retrieved_top_urls[i];
                    data1.retrieved_top_scores[i]=data1.retrieved_top_scores[j];
                    data1.retrieved_top_urls[i]=data1.retrieved_top_urls[j];
                    data1.retrieved_top_scores[j]=temp_score;
                    data1.retrieved_top_urls[j]=temp_url;
                }
            }
            print+=" "+Integer.toString(data1.retrieved_top_scores[i]);
        }

        Log.d("check", "sorted: "+print);
    }
}
