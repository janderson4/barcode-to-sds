package com.example.main_activity.activity_controls;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.fragment.NavHostFragment;

import com.example.basicactivity.R;
import com.android.volley.toolbox.Volley;
import com.downloader.PRDownloader;
import com.example.main_activity.backend.Data;
import com.example.main_activity.backend.NameFinder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/*
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

 */

public class SecondFragment extends Fragment {
    private FragmentSecondBinding binding;

    public Data data1;

    static Fragment fragment;

    static boolean still_want_result;

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    private static final String TAG = "MyActivity";

 //   private InterstitialAd interstitialAd;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        data1=new Data();

        data1.queue = Volley.newRequestQueue(getActivity());
        data1.searches_queued = 0;
        data1.global_name="";
        data1.brand="";
        data1.mpn="";
        data1.name_from_pdf="";
        data1.retrieved_top_urls=new String[]{"","","","","","",""};
        data1.retrieved_top_scores= new int[7];
        data1.retrieve_requests=0;
        data1.best_url="";
        data1.best_score=-10;
        data1.all_pdfs=true;
        data1.external_link="";
        data1.downloadID=0;
        data1.fragment=SecondFragment.this;
        data1.current_activity=getActivity();
        data1.session_navcontroller=NavHostFragment.findNavController(SecondFragment.this);

        // Log the Mobile Ads SDK version.
    //    Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());

        /*
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

         */



        getParentFragmentManager().setFragmentResultListener("requestKey_upc", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey_upc, @NonNull Bundle bundle) {
                String result = bundle.getString("bundleKey_upc");
                Toast.makeText(getActivity(), "got upc: "+result, Toast.LENGTH_SHORT).show();
                Log.d("check","in secondfragment, got barcode: "+result);
                NameFinder.go_upc_name(result, data1);
      //          loadAd_and_show();
            }
        });


        PRDownloader.initialize(getActivity());

        fragment=SecondFragment.this;
        still_want_result=true;
        Log.d("check","just redefined fragment: "+fragment);

        super.onViewCreated(view, savedInstanceState);


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                still_want_result=false;
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        File folder = getActivity().getExternalFilesDir("files");
        Log.d("check","got folder: "+folder);
        File file = new File(folder, "Fragment.txt");
        Log.d("check","got file: "+file);
        Log.d("check","does file exist? "+file.exists());
            try    {
                Writer output = new BufferedWriter(new FileWriter(file));
                output.write(SecondFragment.this.toString());
                Log.d("check","just wrote to file: "+SecondFragment.this.toString());
                output.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public static Fragment get_current(){
        Log.d("check","returning fragment: "+fragment);
        return fragment;
    }
    public static boolean want_result() {
        return still_want_result;
    }

    /*
    public void loadAd_and_show() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                getContext(),
                AD_UNIT_ID,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd1) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        interstitialAd = interstitialAd1;
                        Log.i(TAG, "onAdLoaded");
                        Toast.makeText(getActivity(), "onAdLoaded()", Toast.LENGTH_SHORT).show();
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        interstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        interstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                        showInterstitial();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        interstitialAd = null;

                        String error =
                                String.format(
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                        Toast.makeText(
                                        getActivity(), "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null) {
            interstitialAd.show(getActivity());
        } else {
            Toast.makeText(getActivity(), "Ad did not load", Toast.LENGTH_SHORT).show();
        }
    }

     */

}