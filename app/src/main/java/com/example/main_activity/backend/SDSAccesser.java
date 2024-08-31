package com.example.main_activity.backend;

import android.app.Activity;
import android.util.Log;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.example.main_activity.activity_controls.Success;

import java.io.File;

public class SDSAccesser {
    public static void save_sds(String orig_name, String url, Activity current_activity){
        String name_for_file = orig_name.replaceAll("\\W+", "");
        if (name_for_file.length()>25){
            name_for_file=name_for_file.substring(0,25);
        }
        String path= DownloadPdf.getRootDirPath(current_activity);
        String fileName=name_for_file+".pdf";
        int downloadID=0;
        Success.waiting_for_download=true;
        Log.d("check", "about to download in accessor, path is: "+path+", name is: "+fileName);
        downloadID = PRDownloader.download(url, path, fileName)
                .build()
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Log.d("check", "download complete in accessor, path pdf= "+path);
                        Success.waiting_for_download=false;
                        Log.d("error", "about to call ready to show");
                        Success.ready_to_show();
                    }

                    @Override
                    public void onError(Error error) {
                        Log.d("error", "couldn't download in accessor");
           //             Success.waiting_for_download=false;
                        Log.d("error", "about to call ready to show");
                        Success.ready_to_show();
                    }

                });
    }
    public static boolean does_file_exist(String orig_name, Activity current_activity){
        if (orig_name==null || orig_name.equals(""))
            return false;
        else {
            String name_for_file = orig_name.replaceAll("\\W+", "");
            if (name_for_file.length() > 25) {
                name_for_file = name_for_file.substring(0, 25);
            }
            String path = DownloadPdf.getRootDirPath(current_activity);
            String fileName = path + "/" + name_for_file + ".pdf";
            File file = new File(fileName);
            return file.exists();
        }
    }
    public static void delete_file(String orig_name, Activity current_activity){
        String name_for_file = orig_name.replaceAll("\\W+", "");
        if (name_for_file.length() > 25) {
            name_for_file = name_for_file.substring(0, 25);
        }
        String path = DownloadPdf.getRootDirPath(current_activity);
        String fileName = path + "/" + name_for_file + ".pdf";
        File file = new File(fileName);
        if (file.exists()){
            file.delete();
            Log.d("error", "deleted file: "+orig_name);
        }
        else
            Log.d("error", "couldn't delete file: "+orig_name);
    }
    public static File get_file_name(String orig_name, Activity current_activity){
        String name_for_file = orig_name.replaceAll("\\W+", "");
        if (name_for_file.length()>25){
            name_for_file=name_for_file.substring(0,25);
        }
        String path=DownloadPdf.getRootDirPath(current_activity);
        String fileName=path+"/"+name_for_file+".pdf";
        File file=new File(fileName);
        return file;
    }
}
