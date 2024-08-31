package com.example.main_activity.backend;

import android.util.Log;

import com.example.main_activity.activity_controls.LinkDisplay;

import java.util.Arrays;
import java.util.List;

public class AnalyzePdf {
    public static void pdf_analysis(String[] urls, int[] backup_scores, int index, String sds_text, boolean from_pdf, Data data1){
        Log.d("check", "Starting pdf_analysis for: " + urls[index]+" source is "+index);

        String[] sds_terms={"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
        boolean sds=true;
        if (!(sds_text.contains("Identification")||sds_text.contains("identification")||sds_text.contains("IDENTIFICATION")))
            sds=false;
        if (!(sds_text.contains("Safety Data Sheet")||sds_text.contains("safety data sheet")||sds_text.contains("SAFETY DATA SHEET")))
            sds=false;
        if (sds) {
            String name_found="";
            String[] name_words={"Product Name", "product name", "Product name", "PRODUCT NAME", "Name", "name", "NAME",
                    "Identification", "identification", "IDENTIFICATION", "Hazards Identification", "hazards identification",
                    "HAZARDS IDENTIFICATION"};
            int[] name_words_indices=new int[13];
            for (int i=0;i< name_words.length;i++)
                name_words_indices[i]=sds_text.indexOf(name_words[i]);
            boolean found=false;
            int record_index=10000;
            int which_name=-2;
            for (int i=0;i<4;i++){
                if (name_words_indices[i]!=-1 && name_words_indices[i]<record_index){
                    record_index=name_words_indices[i];
                    found=true;
                    which_name=i;
                }
            }
            if (!found){
                for (int i=4;i<7;i++){
                    if (name_words_indices[i]!=-1 && name_words_indices[i]<record_index){
                        record_index=name_words_indices[i];
                        found=true;
                        which_name=i;
                    }
                }
            }
            if (!found){
                int max_hazard=Math.max(name_words_indices[10],Math.max(name_words_indices[11],name_words_indices[12]));
                for (int i=7;i<10;i++){
                    if (name_words_indices[i]!=-1 && name_words_indices[i]<record_index){
                        record_index=name_words_indices[i];
                        which_name=i;
                    }
                }
                if (max_hazard!=-1&& record_index<max_hazard && which_name!=-2){
                    found=true;
                }
            }

            boolean found_with_enter=false;
            if (found) {
                String sds_trimmed;
                if (which_name<4){
                    sds_trimmed = sds_text.substring(record_index+12);
                }
                else if (which_name<7){
                    sds_trimmed = sds_text.substring(record_index+4);
                }
                else
                    sds_trimmed = sds_text.substring(record_index+14);
                while (sds_trimmed.substring(0,1).equals(" ")|| sds_trimmed.substring(0,1).equals(":")||
                        sds_trimmed.substring(0,2).equals("\n")) {
                    if (sds_trimmed.substring(0,2).equals("\n"))
                        sds_trimmed = sds_trimmed.substring(2);
                    else
                        sds_trimmed = sds_trimmed.substring(1);
                }
                if (sds_trimmed.contains("\n") && sds_trimmed.indexOf("\n")<100){
                    int enter_index = sds_trimmed.indexOf("\n");
                    name_found = sds_trimmed.substring(0, enter_index);
                    Log.d("check", "it's an sds, name: " + name_found);
                    found_with_enter=true;
                }
                else
                    Log.d("check", "no enter found");
            }
            else
                Log.d("check", "no name found");
            int similar_score=0;
            String[] name_split=name_found.split(" ");
            if (found_with_enter){
                int name_split_l=name_split.length;
                String[] array_for_compare=new String[name_split_l+1];
                array_for_compare[0]=data1.global_name;
                System.arraycopy(name_split,0,array_for_compare,1,name_split_l);
                int similarity= Qualifiers.compare(array_for_compare);
                //     similar_score+=(similarity);
                if (similarity==0 && name_found.length()>20){
                    //         similar_score-=2;
                }
                // this was intended to make the name of the product shown in the library more accurate, but for rust
                // inhibitor it returned CRC, so skip it
                if (name_found.length()>8) {
      //              data1.name_from_pdf = name_found;
                }
            }
            String sds_text_lower=sds_text.toLowerCase();
            String global_name_lower=data1.global_name.toLowerCase();
            String[] global_name_split=global_name_lower.split(" ");
            for (int i=0;i< global_name_split.length;i++){
                if(global_name_split[i].endsWith(":") || global_name_split[i].endsWith(";")
                        ||global_name_split[i].endsWith(".")){
                    global_name_split[i]=global_name_split[i].substring(0,global_name_split[i].length()-1);
                }
            }
            String[]banned_words= {"type", "product","from"};
            List banned_words_list = Arrays.asList(banned_words);
            for (int i=0;i<global_name_split.length;i++){
                if (global_name_split[i].length()>3 && sds_text_lower.contains(global_name_split[i])
                        &&!banned_words_list.contains(global_name_split[i])) {
                    similar_score += 1;
                    Log.d("check", "same word found: "+global_name_split[i]);
                }
            }
            if (data1.mpn!=null && !data1.mpn.isEmpty()) {
                if (sds_text_lower.contains(data1.mpn)) {
                    Log.d("check", "we have an mpn match: " + data1.mpn);
                    similar_score += 3;
                }
            }
            Log.d("check","similar_score: "+similar_score);
            if (similar_score>2) {
                Log.d("check","got to end of analysis, score: "+similar_score);
                LinkDisplay.show_link(urls[index], data1);
            }
            else {
                backup_scores[index] = similar_score;
                PdfFailRoutine.pdf_fail_routine(urls, backup_scores, index, from_pdf, data1);
            }
        }
        else {
            backup_scores[index] = -10;
            Log.d("check", "not an sds");
            Log.d("check", "about to call fail_routine from analysis for index: " + index);
            PdfFailRoutine.pdf_fail_routine(urls, backup_scores, index, from_pdf, data1);
        }
    }
}
