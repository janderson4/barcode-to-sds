package com.example.main_activity.activity_controls;

import android.app.Activity;
import android.util.Log;

import com.example.main_activity.backend.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class Json{
    public static void update_json(ArrayList<Product> array_list, Activity current_activity){
        StringBuilder fake_json = new StringBuilder();
        fake_json.append("[");
        String name_clean;
        for(int i = 0;i<array_list.size();i++) {
            name_clean=name_cleaner(array_list.get(i).pname);
            Log.d("check","raw name to fix: "+array_list.get(i).pname);
            fake_json.append("{\"Product Name\":\"" + name_clean + "\",\"SDS URL\":\"" + array_list.get(i).url + "\"}");
            if (i < array_list.size() - 1) {
                fake_json.append(", \n");
            }
        }
        fake_json.append("]");
        Log.d("check","fake json in update: "+fake_json);

        File file=get_file(current_activity);

         /*
        File folder = current_activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        Log.d("check","got folder: "+folder);
        File file = new File(folder, "file.json");
        Log.d("check","got file: "+file);
        if (file==null) {
            file = new File(current_activity.getExternalFilesDir(
                    Environment.DIRECTORY_DOCUMENTS), "file.json");
            Log.d("check","just created file: "+file);
        }

          */
        try    {
            Writer output = new BufferedWriter(new FileWriter(file));
            output.write(fake_json.toString());
            output.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    public static String name_cleaner(String name){
        return name.replaceAll("\"", "").replaceAll("\'","");
    }
    public static ArrayList<Product> get_array_list(Activity current_activity){
        File file=get_file(current_activity);

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
        } catch (
                FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            line = bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (line != null) {
            stringBuilder.append(line).append("\n");
            try {
                line = bufferedReader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            bufferedReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String response = stringBuilder.toString();
        JSONArray jsonarray;
        ArrayList<Product> array_list = new ArrayList<>();
        if (response!=null && !response.isEmpty())
        {
            try {
                jsonarray = new JSONArray(response);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            Log.d("check", "returned jsonobject: " + jsonarray.toString());
            Log.d("check", "json length: " + jsonarray.length());
            JSONObject jsonobj;
            if (jsonarray != null) {
                for (int i = 0; i < jsonarray.length(); i++) {
                    Product x = new Product();
                    try {
                        jsonobj = jsonarray.getJSONObject(i);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        x.pname = jsonobj.getString("Product Name");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        x.url = jsonobj.getString("SDS URL");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    array_list.add(x);
                    Log.d("check", "just added to arraylist: " + x.pname + " " + x.url);
                }
            }
        }
        return array_list;
    }
    public static File get_file(Activity current_activity){
        File folder = current_activity.getExternalFilesDir("files");
        Log.d("check","got folder: "+folder);
        File file = new File(folder, "SDS_Link_List.json");
        Log.d("check","got file: "+file);
        Log.d("check","does file exist? "+file.exists());
        if (!file.exists()){
            try    {
                Writer output = new BufferedWriter(new FileWriter(file));
                output.write("");
                output.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("check","does file exist? "+file.exists());
        return file;
    }
}
