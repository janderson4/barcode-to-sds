package com.example.main_activity.backend;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.basicactivity.databinding.FragmentSecondBinding;

public class Data {
    RequestQueue queue;
    public int searches_queued;
    public int retrieve_requests;
    public String global_name;

    public String mpn;

    public String brand;

    public String name_from_pdf;
    public String[] retrieved_top_urls;
    public int[] retrieved_top_scores;
    public String best_url;
    public int best_score;
    public boolean all_pdfs;
    public static String external_link;
    public FragmentSecondBinding binding;
    public int downloadID;
    public Fragment fragment;
    Activity current_activity;
    NavController session_navcontroller;
}
