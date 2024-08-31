package com.example.main_activity.backend;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;

import java.io.File;

public class DownloadPdf {
    public static void pdf_downloader(String[] urls, int[] backup_scores, int index, boolean from_pdf, Data data1){
        String url=urls[index];
        Log.d("check", "");
        Log.d("check", "just got into downloader");
        String[] path_of_pdf={""};
        String fileName="test2.pdf";
        String path = getRootDirPath(data1.current_activity);
        Log.d("check", "about to start download for url: "+url+" path: "+path+" filename: "+fileName);
        data1.downloadID = PRDownloader.download(url, path, fileName)
                .build()
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
            //            path_of_pdf[0]= Environment.getExternalStorageDirectory().getPath() +
             //                   "/Android/data/com.example.sdsappdraft12withpdfdownload/files/"+"test.pdf";
                        path_of_pdf[0]=path+"/test2.pdf";
                        Log.d("check", "download complete, path pdf= "+path_of_pdf[0]);
                        Log.d("Files","");
                        Log.d("Files", "Path: " + path);
                        ParsePdf.pdf_parser(urls, backup_scores, index, path_of_pdf[0], from_pdf, data1);
                    }

                    @Override
                    public void onError(Error error) {
                        Log.d("error", "couldn't download");
                        Log.d("check","about to call fail_routine from downloader for index: "+index);
                        PdfFailRoutine.pdf_fail_routine(urls,backup_scores,index,from_pdf, data1);
                    }

                });
    }
    public static String getRootDirPath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = ContextCompat.getExternalFilesDirs(context.getApplicationContext(), null)[0];
            return file.getAbsolutePath();
        } else {
            return context.getApplicationContext().getFilesDir().getAbsolutePath();
        }
    }
}
