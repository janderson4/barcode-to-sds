package com.example.main_activity.backend;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.main_activity.activity_controls.WebpageSdsSearch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class RetrievePage {
    public static void retrieve_page(String url, String product_name, boolean pdf, Data data1) {
        Log.d("check","starting retrieve page");
        data1.retrieve_requests++;
        if (pdf) {
            int score=Scorer.scorer(url, data1);
            Log.d("check","got back to retrievepage");
            data1.retrieved_top_scores[6]=score;
            data1.retrieved_top_urls[6]=url;
            Log.d("check","just assigned score and urls to position 6");
            ScoreSorter.sort_scores(data1);
            Log.d("check","just ran sort_scores");
            Log.d("check", "");
        }
        else {
            data1.searches_queued++;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d("check", "response received");
                                Document doc = Jsoup.parse(response);
                                String sds_url = WebpageSdsSearch.sds_search(doc, response, url, data1);
                                if (sds_url!=null) {
                                    Log.d("check", "sds_search complete, received link: " + sds_url);
                                    int score=Scorer.scorer(sds_url, data1);
                                    Log.d("check", "searches_queued: " + data1.searches_queued);

                                    data1.retrieved_top_scores[6]=score;
                                    data1.retrieved_top_urls[6]=sds_url;
                                    ScoreSorter.sort_scores(data1);
                                    Log.d("check", "");
                                }
                            } catch (Exception ex) {
                                Log.d("error", "try catch");
                                Log.d("VolleyError", ex.getMessage());
                            }
                            data1.searches_queued--;
                            Log.d("check", "just dequeued another search, searches_queued is "+data1.searches_queued);
                            if (data1.searches_queued==0) {
                                for (int i=0;i<data1.retrieved_top_urls.length;i++){
                                    data1.retrieved_top_scores[i]=data1.retrieved_top_scores[i]-7;
                                }
                                DownloadPdf.pdf_downloader(data1.retrieved_top_urls,
                                        data1.retrieved_top_scores, 0,false, data1);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("error", "webpage not received: " + url);
                    data1.searches_queued--;
                    Log.d("check", "just dequeued another search, searches_queued is "+data1.searches_queued);
                    if (data1.searches_queued==0) {
                        for (int i=0;i<data1.retrieved_top_urls.length;i++){
                            data1.retrieved_top_scores[i]=data1.retrieved_top_scores[i]-7;
                        }
                        DownloadPdf.pdf_downloader(data1.retrieved_top_urls, data1.retrieved_top_scores, 0,false, data1);
                    }
                }
            });
            data1.queue.add(stringRequest);
            //    Log.d("check", "just queued");
            Log.d("check", "");
        }
    }
}
