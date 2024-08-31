package com.example.main_activity.backend;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.main_activity.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class BingPdf {
    public static void bing_pdf(Data data1){
        String name_mod= Util.url_cleaner(data1.global_name);
        String bing_url="https://www.bing.com/search?q="+"sds+pdf+"+name_mod;
        Log.d("check", "search for bing pdf: "+bing_url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, bing_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //     System.out.println(response);
                        Document doc = Jsoup.parse(response);
                        Elements resultDivs = doc.getElementsByClass("b_algo");
                        if (!resultDivs.isEmpty()) {
                            //             Log.d("check","first resultdiv:"+resultDivs.first());
                        }
                        else
                            Log.d("check","no resultdivs");
                        int try_count = 0;
                        boolean found_legit = false;
                        boolean found_final=false;
                        while (!found_legit && try_count<resultDivs.size()) {
                            Element resultDiv = resultDivs.get(try_count);
                            String tag = resultDiv.tagName();
                            if (tag.equals("li")) {
                                boolean pdf=false;
                                found_legit = true;
                                Elements resultSpans = resultDiv.select("div[class=b_algoheader] > a " +
                                        "> div[class=b_title] > span[class=sb_doct_txt b_float]");
                                Log.d("check", "found span or not, size: " + resultSpans.size());
                                Element resultLink = resultDiv.select("div[class=b_algoheader] > a").first();
                                String product_page = resultLink.attr("abs:href");
                                String link_text=resultLink.text();
                                if (resultSpans.size() > 0 || product_page.endsWith(".pdf"))
                                    pdf = true;
                                Log.d("check", "for bing_pdf first pdf = "+pdf);
                                Log.d("check", "product page: "+product_page);
                                Log.d("check", "link text: "+link_text);
                                Log.d("check", "possible?: "+ Qualifiers.possible(new String[]{product_page,
                                        link_text}));
                                if (pdf && Qualifiers.possible(new String[]{product_page, link_text})) {
                                    // show_link(product_page);
                                    found_final=true;
                                    int score= Scorer.scorer(product_page, data1);
                                    DownloadPdf.pdf_downloader(new String[]{product_page},
                                            new int[]{score-7}, 0, true, data1);
                                }
                            }
                            try_count++;
                        }
                        if (!found_final)
                            BingPages.bing_pages(data1);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("check", "volleyerror");
                BingPages.bing_pages(data1);
            }
        }){
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("User-Agent","Mozilla/5.0 (Linux; Android 8.0.0; SM-G960F Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.84 Mobile Safari/537.36");
                return headers;
            }

        };

// Add the request to the RequestQueue.
        data1.queue.add(stringRequest);
    }
}
