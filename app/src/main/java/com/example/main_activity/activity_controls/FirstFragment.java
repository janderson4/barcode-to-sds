package com.example.main_activity.activity_controls;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.basicactivity.R;

import com.journeyapps.barcodescanner.ScanOptions;

import com.journeyapps.barcodescanner.ScanContract;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    private static final String AD_UNIT_ID = "";
    private static final String TAG = "MyActivity";

  //  private InterstitialAd interstitialAd;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("BUTTONS", "User tapped the Supabutton");
                barcodeLauncher.launch(new ScanOptions());
                /*
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SuccessFragment);

                 */
            }


        });
        binding.buttonLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_LibraryFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /*
    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                getContext(),
                MainActivity.AD_UNIT_ID,
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

     */
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_LONG).show();
                } else {
        //            Toast.makeText(getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    Log.d("check","barcode: "+result.getContents());

                    Bundle bundle_result = new Bundle();
                    bundle_result.putString("bundleKey_upc", result.getContents());
                    getParentFragmentManager().setFragmentResult("requestKey_upc", bundle_result);

                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment);

                }
            });
}