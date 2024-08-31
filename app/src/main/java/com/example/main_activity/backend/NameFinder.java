package com.example.main_activity.backend;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.main_activity.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NameFinder {

    public static void go_upc_name(String upc, Data data1){
        /*
        String json="{\n" +
                "  \"code\": \"829576019311\",\n" +
                "  \"codeType\": \"UPC\",\n" +
                "  \"product\": {\n" +
                "    \"name\": \"Goodfellow and Co No. 01 Blue Sage Tonka Texturizing Fiber, 4 Oz.\",\n" +
                "    \"description\": \"No. 01 Blue Sage and Tonka Texturizing Fiber from Goodfellow and Co gives you...\",\n" +
                "    \"imageUrl\": \"https://go-upc.s3.amazonaws.com/images/54066938.jpeg\",\n" +
                "    \"brand\": \"Goodfellow and Co\",\n" +
                "    \"specs\": [\n" +
                "      [\"Item Form\", \"Clay\"],\n" +
                "      [\"Liquid Volume\", \"4 Fluid Ounces\"],\n" +
                "      [\"Scent\", \"Lime, Amber\"]\n" +
                "    ],\n" +
                "    \"category\": \"Hair Care\"\n" +
                "  },\n" +
                "  \"barcodeUrl\": \"https://go-upc.com/barcode/829576019311\"\n" +
                "}";

         */


        final String upcfinal=upc;
        String key="501a0a9446f09846e529431990687867db737a3aa20f09b2407246fa2a028e96";
        // String go_upc_url="https://go-upc.com/api/v1/code/"+upc;
        String go_upc_url="https://go-upc.com/api/v1/code/"+upc+"?key="+key;
        Log.d("check","go upc url: "+go_upc_url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, go_upc_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("check", "got response: "+response);
                        JSONObject jsonarray;
                        try {
                            jsonarray = new JSONObject(response);
                            Log.d("check", "got json: "+jsonarray);
                            JSONObject product=(JSONObject) jsonarray.get("product");
                            Log.d("check", "got product: "+product);
                            String name=product.getString("name");
                            Log.d("check", "got name: "+name);
                            data1.global_name=name;
                            JSONArray specs=product.getJSONArray("specs");
                            String MPN = "";
                            /*
                            if (specs.("MPN")){
                                MPN=specs.getString("MPN");
                            }
                            else if (specs.has("Product Number")){
                                MPN=specs.getString("Product Number");
                            }

                             */
                            for (int i = 0; i < specs.length(); i++)
                            {
                                JSONArray item = specs.getJSONArray(i);
                                Log.d("check", "got item: "+item);
                                Log.d("check", "item get 0: "+item.get(0));
                                if (((String)item.get(0)).equals("MPN"))
                                {
                                    Log.d("check", "got mpn");
                                    MPN=(String) item.get(1);
                                    break;
                                }
                                else if(((String)item.get(0)).equals("Part Number"))
                                {
                                    Log.d("check", "got product code");
                                    MPN=(String) item.get(1);
                                    break;
                                }
                            }
                            data1.mpn=MPN;
                            String brand1="";
                            if (product.has("brand")){
                                brand1=product.getString("brand");
                            }
                            data1.brand=brand1;
                            Log.d("check", "mpn: "+data1.mpn+" and brand is "+data1.brand);
                            if (name.length()>6){
                                BingPdf.bing_pdf(data1);
                            }
                            else
                                bing_names(upc, data1);
                        } catch (JSONException e) {
                            bing_names(upc, data1);
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("check", "volleyerror");
                bing_names(upc, data1);
            }
        }){
            /*
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("User-Agent","Authorization: Bearer 501a0a9446f09846e529431990687867db737a3aa20f09b2407246fa2a028e96");
                return headers;
            }

             */

        };
        data1.queue.add(stringRequest);

    }

    public static void bing_names(String upc, Data data1){
        final String upcfinal=upc;
        if (upc.length()==11)
            upc="0"+upc;
        String upc_for_bing= Util.url_cleaner(upc);
        String[] banned_terms={"Barcode Lookup", "UPC Barcode Search"};
        String bing_url="https://www.bing.com/search?q="+"product+for+upc+"+upc_for_bing;
        Log.d("check","url for search: "+bing_url);
        Log.d("check","bing search: "+bing_url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, bing_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Document doc = Jsoup.parse(response);
                        Elements resultDivs=doc.getElementsByClass("b_algo");
                        ArrayList<String> link_names=new ArrayList<>();
                        Log.d("check","about to go through how many divs? "+resultDivs.size());
                        for (Element resultDiv: resultDivs) {
                            Log.d("check", "link");
                            try {
                                String tag = resultDiv.tagName();
                                Log.d("check", "tag: " + tag);
                                if (tag.equals("li")) {
                                    Element resultName;
                                    Elements resultNames= resultDiv.select("div[class=b_algoheader] > a > div[class=b_title] > h2");
                                    if (resultNames.size()>0)
                                        resultName=resultNames.get(0);
                                    else{
                                        resultNames=resultDiv.select("div[class=b_algoheader] > a > h2");
                                        resultName=resultNames.get(0);
                                    }
                                    Log.d("check", "just got a link name: "+resultName.text());
                                    String result=resultName.text();
                                    if (!Util.stringContainsItemFromList(result, banned_terms)){
                                        if (result.endsWith(" - e…"))
                                            result=result.substring(0,result.length()-5);
                                        link_names.add(result);
                                        if (result.endsWith("…")) {
                                            int last_space=result.lastIndexOf(" ");
                                            result = result.substring(0, last_space);
                                        }
                                        link_names.add(result);
                                    }
                                    else
                                        Log.d("check", "rejecting result: "+result);
                                }
                            }
                            catch(Exception e){
                                Log.d("check", "that element didn't have what you're looking for");
                            }
                        }
                        if (link_names.size()>0)
                            name_mixer(link_names, data1, upcfinal);
                        else {
                            Toast.makeText(data1.current_activity, "fail in bing_names, not enough link", Toast.LENGTH_SHORT).show();
                            PdfFailRoutine.fail_launcher(data1);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("check", "volleyerror");
                Toast.makeText(data1.current_activity, "fail volleyerror in bing_names", Toast.LENGTH_SHORT).show();
                PdfFailRoutine.fail_launcher(data1);
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
    public static void name_mixer(ArrayList<String> link_names, Data data1, String upc){
        Log.d("check", "just got into name_mixer with upc: "+upc);
        String[] meaningless_words={upc, "and", "upc", "code", "ups", "lookup", "free", "with", "retailers", "tracking",
                "universal", "product", "united", "states", "buycott", "radwell", "repair", "buy", "database",
        "information", "gs1", "home", "amazon", "2023", "how", "for", "generator", "create", "online", "print-ready",
        "download"};
        HashMap<String, Integer> words_to_watch=new HashMap<>();
    //    ArrayList<keyword> words_to_watch =new ArrayList<>();
        for (int i=0;i< link_names.size(); i++){
            Log.d("check", "about to start link: "+link_names.get(i));
            String[] words_in_name = link_names.get(i).split(" ");
            for (int words=0;words<words_in_name.length;words++) {
                String this_word=words_in_name[words];
                String this_word_lowercase=this_word.toLowerCase();
                    if (this_word_lowercase.length() > 2 && !Util.stringContainsItemFromList(this_word_lowercase, meaningless_words)) {
                        /*
                        keyword keyword1=new keyword();
                        keyword1.word=this_word;
                        keyword1.count=1;

                         */
                        if (words_to_watch.containsKey(this_word_lowercase)) {
                            words_to_watch.put(this_word_lowercase, words_to_watch.get(this_word_lowercase)+1);
                        }
                        else
                            words_to_watch.put(this_word_lowercase, 1);
                    }
            }
        }
        int record=0;
        int record_index=-1;
        for (int i=0;i< link_names.size();i++){
            int score=0;
            String[] words_in_name = link_names.get(i).split(" ");
            Log.d("check", "starting for "+link_names.get(i));
            for (int words=0;words<words_in_name.length;words++) {
                String this_word=words_in_name[words];
                String this_word_lowercase=this_word.toLowerCase();
                if (words_to_watch.containsKey(this_word_lowercase)) {
                    score+=words_to_watch.get(this_word_lowercase)-1;
                    Log.d("check", "adding "+(words_to_watch.get(this_word_lowercase)-1)+" for word: "
                            +this_word_lowercase);
                }
            }

            Log.d("check", "score: "+score);
            Log.d("check", "");
            if (score>record){
                record=score;
                record_index=i;
            }
        }
        if (record_index!=-1 && record>4){
            Log.d("check", "chosen name: "+link_names.get(record_index));
            data1.global_name=link_names.get(record_index);
            BingPdf.bing_pdf(data1);
        }
        else {
            Log.d("check", "failed to get enough in bing_name");
            PdfFailRoutine.fail_launcher(data1);
        }

    }
    public class keyword{
        public String word;
        public int count;
    }



    public void barcodelookup_name(String upc, Data data1){
        ArrayList<String> link_names=new ArrayList<>();
        String url = "https://www.barcodelookup.com/"+upc;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Document doc = Jsoup.parse(response);
                        Element h4 = doc.getElementsByTag("h4").first();
                        Elements manufs=doc.select("div[class=product-text-label]:contains(Manufacturer)>span");
                        Log.d("check","manufs size: "+manufs.size());
                        String name=h4.text();
                        if (name.length()>80) {
                            name = name.substring(0, 80);
                            int last_space=name.lastIndexOf(" ");
                            if (65<last_space)
                                name = name.substring(0, last_space);
                        }
                        if (manufs.size()>0){
                            String manuf=manufs.get(0).text();
                            if (!name.contains(manuf))
                                name=name+" "+manuf;
                        }
                        Log.d("check","Product name: "+name);
                        data1.global_name=name;
                        data1.global_name=name;
                        if (!name.isEmpty() && !name.equals("") && !name.contains("API"))
                            bing_names(upc, data1);
                        else {
                            Log.d("check", "That UPC wasn't found");
                            Toast.makeText(data1.current_activity, "fail upc not found, name empty or api", Toast.LENGTH_SHORT).show();
                            PdfFailRoutine.fail_launcher(data1);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("check","That UPC wasn't found2");
                Toast.makeText(data1.current_activity, "fail volleyerror in findname", Toast.LENGTH_SHORT).show();
                PdfFailRoutine.fail_launcher(data1);
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
