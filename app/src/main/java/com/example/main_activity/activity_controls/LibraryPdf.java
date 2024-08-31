package com.example.main_activity.activity_controls;

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


public class LibraryPdf extends Fragment {

    private LibraryPdfBinding binding;

    static private String url;

    static private String product_name;

    static private boolean pdf_saved;

    static boolean received_packet;


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

        Log.d("check","view created, have url "+url+" and product name "
                +product_name+" and pdf saved ? "+pdf_saved);
        Log.d("check","received packet? "+received_packet);
        if (received_packet) {
            Log.d("check","about to call show pdf");
            show_pdf();

        }

        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                /*
                // We use a String here, but any type that can be put in a Bundle is supported.
                String result = bundle.getString("bundleKey");
                // Do something with the result.
                url=result;
                Toast.makeText(getActivity(),  "received link to show: "+result, Toast.LENGTH_SHORT).show();
                WebView webView=binding.webView;
                webView.setWebViewClient(new WebViewClient());
                //   url = "https://content.interlinebrands.com/product/document/41/316343445_SDS_E.pdf";
                //   url="https://files.wd40.com/pdf/sds/lava/lava-bar-us-ghs-sds.pdf";
                //url="https://en.wikipedia.org/wiki/Drag_coefficient";
                //     url=MainActivity.get_external_link();
                Log.d("check","about to show pdf: "+url);
                String url_with_docs="https://docs.google.com/gview?embedded=true&url="+url;
                webView.loadUrl(url_with_docs);
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);

                 */
                Log.d("check","bundle has been received");
                ArrayList<String> result = bundle.getStringArrayList("bundleKey");
                product_name=result.get(0);
                url=result.get(1);
                pdf_saved= SDSAccesser.does_file_exist(product_name, getActivity());
                received_packet=true;
                Log.d("check","about to show pdf from bundle");
                show_pdf();
            }
        });


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LibraryPdf.this)
                        .navigate(R.id.action_LibraryPdfFragment_to_Library2Fragment);
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