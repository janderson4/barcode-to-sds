package com.example.main_activity.activity_controls;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.fragment.NavHostFragment;

import com.example.basicactivity.R;
import com.example.main_activity.backend.SDSAccesser;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.ArrayList;

public class Success extends Fragment {

    private LibraryPdfBinding binding;

    static private String url;

    static private String product_name;

    static private boolean pdf_saved;

    static boolean received_packet;

    static boolean waiting_for_download;

    static WebView webView;
    static PDFView pdfView;

    static Activity current_activity;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState

    ) {

        binding = LibraryPdfBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        webView=binding.webView;
        pdfView=binding.pdfView;
        current_activity=getActivity();
        Log.d("check","received packet? "+received_packet);
        if (received_packet) {
            Log.d("check","about to call show pdf");
            show_pdf();

        }

        getParentFragmentManager().setFragmentResultListener("SuccessrequestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                Log.d("check","bundle has been received");
                ArrayList<String> result = bundle.getStringArrayList("bundleKey");
                product_name=result.get(0);
                url=result.get(1);
                received_packet=true;
                /*
                while (waiting_for_download){

                }

                 */
            //    show_pdf();
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(Success.this)
                        .navigate(R.id.action_SuccessFragment_to_FirstFragment);
            }
        });
        binding.fabshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, url);
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });
    }

    public static void ready_to_show(){
        Log.d("error", "in ready to show");
        pdf_saved= SDSAccesser.does_file_exist(product_name, current_activity);
        show_pdf();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static void show_pdf(){

        if (pdf_saved){
            Log.d("check","pdf is saved, will get for "+product_name);
            webView.setVisibility(View.GONE);
            File filename = SDSAccesser.get_file_name(product_name, current_activity);
            pdfView.fromFile(filename).load();
        }
        else{
            pdfView.setVisibility(View.GONE);
            webView.setWebViewClient(new WebViewClient());
            Log.d("check","about to show pdf: "+url);
            String url_with_docs="https://docs.google.com/gview?embedded=true&url="+url;
            webView.loadUrl(url_with_docs);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
        }

    }
}

/*
public class Success extends Fragment {

    private SuccessBinding binding;

    private static String url;

    private static String product_name;

    private static boolean pdf_saved;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = SuccessBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(Success.this)
                        .navigate(R.id.action_SuccessFragment_to_FirstFragment);
            }
        });
        ArrayList<Product> retrieved=Json.get_array_list(getActivity());
        Product latest=retrieved.get(retrieved.size()-1);
        product_name=latest.pname;
        url=latest.url;
        pdf_saved=SDSAccesser.does_file_exist(product_name, getActivity());
        Log.d("check","does success file exist? "+pdf_saved);
        Log.d("check","about to show pdf in success for name: "+product_name);
        show_pdf();
        binding.fabshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, url);
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

        WebView webView=binding.webView;
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        String url_with_docs="https://docs.google.com/gview?embedded=true&url="+url;
        webView.loadUrl(url_with_docs);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void show_pdf(){
        WebView webView=binding.webView;
        PDFView pdfView=binding.pdfView;
        if (pdf_saved){
            Log.d("check","pdf is saved, will get for "+product_name);
            webView.setVisibility(View.GONE);
            File filename = SDSAccesser.get_file_name(product_name, getActivity());
            pdfView.fromFile(filename).load();
        }
        else{
            pdfView.setVisibility(View.GONE);
            webView.setWebViewClient(new WebViewClient());
            Log.d("check","about to show pdf: "+url);
            String url_with_docs="https://docs.google.com/gview?embedded=true&url="+url;
            webView.loadUrl(url_with_docs);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
        }

    }

}

 */
