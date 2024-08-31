package com.example.main_activity.backend;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.basicactivity.R;
import com.example.main_activity.activity_controls.SecondFragment;
import com.example.main_activity.util.Util;
import com.example.main_activity.activity_controls.LinkDisplay;

import java.io.File;
import java.io.IOException;

public class PdfFailRoutine {
    public static void pdf_fail_routine(String[] urls, int[] backup_scores, int index, boolean from_pdf, Data data1) {
        //     Log.d("check", "");
        Log.d("check", "in fail_routine for index: "+index+" from_pdf? "+from_pdf);
        if (backup_scores[index] > -1 && backup_scores[index] > data1.best_score) {
            data1.best_url = urls[index];
            data1.best_score = backup_scores[index];
            Log.d("check", "just set new best score, best_url is "+data1.best_url+" best_score is "+data1.best_score);
        }
        if (from_pdf) {
            BingPages.bing_pages(data1);
        }
        if (!from_pdf) {
            int empty = Util.findEmpty(urls);
            for (int i=0;i<empty;i++){
                Log.d("check", "url: "+urls[i]+" backup_score: "+backup_scores[i]);
            }
            if (index + 1 >= empty) {
                if (!data1.best_url.equals(""))
                    LinkDisplay.show_link(data1.best_url, data1);
                else {
                    Log.d("check", "got to end, nothing decent found");
                    PdfFailRoutine.fail_launcher(data1);
                }
            } else
                DownloadPdf.pdf_downloader(urls, backup_scores, index + 1, false, data1);
        }
    }
    public static void fail_launcher(Data data1){
        Log.d("check", "in fail_launcher");
        String name_for_file = data1.global_name.replaceAll("\\W+", "");
        if (name_for_file.length()>15){
            name_for_file=name_for_file.substring(0,15);
        }
        try {
            File filename = new File(DownloadPdf.getRootDirPath(data1.current_activity)+"/product4_"+name_for_file+".txt");
            filename.createNewFile();
            String cmd = "logcat -d -f "+filename.getAbsolutePath();
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (SecondFragment.still_want_result) {
            Fragment fragment = SecondFragment.get_current();
            Log.d("check", "fail just received fragment: " + fragment);
            NavHostFragment.findNavController(fragment)
                    .navigate(R.id.action_SecondFragment_to_FailureFragment);
        }
        else Log.d("check", "didn't want result in fail");

    }
}
