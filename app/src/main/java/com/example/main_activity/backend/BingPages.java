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

public class BingPages {
    public static void bing_pages(Data data1){
        String product_name=data1.global_name;
        if (data1.brand!=null && !data1.brand.isEmpty()) {
            if (!product_name.contains(data1.brand))
                product_name += data1.brand;
        }
        String product_name_mod= Util.url_cleaner(product_name);
        String bing_url="https://www.bing.com/search?q="+"Safety+Data+Sheet+"+product_name_mod;
        Log.d("check","bing search: "+bing_url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, bing_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Document doc = Jsoup.parse(response);
                        Elements resultDivs=doc.getElementsByClass("b_algo");
                        Log.d("check","about to go through how many divs? "+resultDivs.size());
                        for (Element resultDiv: resultDivs) {
                            Log.d("check", "link");
                            try {
                                String tag = resultDiv.tagName();
                                Log.d("check", "tag: " + tag);
                                if (tag.equals("li")) {
                                    boolean pdf = false;
                                    Elements resultSpans = resultDiv.select("div[class=b_algoheader] > a > div[class=b_title] > span[class=sb_doct_txt b_float]");
                                    //      Log.d("check", "found span or not, size: " + resultSpans.size());
                                    if (resultSpans.size() > 0)
                                        pdf = true;
                                    //      Log.d("check", "found first pdf or not");
                                    Elements resultLinks = resultDiv.select("div[class=b_algoheader] > a");
                                    //      Log.d("check", "got links, size: " + resultLinks.size());
                                    Element resultLink = resultLinks.get(0);
                                    //      Log.d("check", "got link");
                                    String product_page = resultLink.attr("abs:href");
                                    if (product_page.endsWith(".pdf"))
                                        pdf = true;
                                    Log.d("check", "link: " + product_page + " is it pdf? " + pdf);
                                    if (product_page!=null &! product_page.isEmpty()) {
                                        if (!pdf){
                                            Log.d("check", "found a not pdf for all_pdfs");
                                            data1.all_pdfs=false;
                                        }
                                        RetrievePage.retrieve_page(product_page, data1.global_name, pdf, data1);
                                    }
                                }
                            }
                            catch(Exception e){
                                Log.d("check", "that element didn't have what you're looking for");
                            }
                        }
                        if (data1.all_pdfs){
                            Log.d("check", "all are pdfs, running dummy retrieve_page");
                            RetrievePage.retrieve_page("", data1.global_name, false, data1);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("check", "volleyerror");
            }
        }){
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("User-Agent","Mozilla/5.0 (Linux; Android 8.0.0; SM-G960F Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.84 Mobile Safari/537.36");
                return headers;
            }

        };
        data1.queue.add(stringRequest);
    }
}
