package com.example.main_activity.util;

import android.content.Context;
import android.os.Environment;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.Arrays;

public class Util {
    public static String url_cleaner(String url){
        url=url.replaceAll(" ", "+").replaceAll("\\+", "-")
                .replaceAll("/", "_").replaceAll("=", "")
                .replaceAll("&", "");
        String[] bing_endings={ "&form=QBLH&sp=-1&ghc=1&lq=0&sc=10-15&qs=n&sk=&cvid=E648A5646D8D41F5B79671DE64947882&ghsh=0&ghacc=0&ghpl=",
                "&form=QBLH&sp=-1&ghc=1&lq=0&pq=sds&sc=10-3&qs=n&sk=&cvid=497ABB17D0C446EEBCCA43AA6CFC6287&ghsh=0&ghacc=0&ghpl=",
                "&form=QBLH&sp=-1&lq=0&pq=sds&sc=10-3&qs=n&sk=&cvid=54F015A856C74F20A9C96E3BBB290F7A&ghsh=0&ghacc=0&ghpl=",
                "&form=QBLH&sp=-1&pq=sdw&sc=10-3&qs=n&sk=&cvid=BA139A585C364CCB8968EF9F1924BF98&ghsh=0&ghacc=0&ghpl=",
                "&form=QBLH&sp=-1&qs=n&sk=&ghsh=0&ghacc=0&ghpl=",
                "&form=QBLH&sp=-1&lq=1&pq=&sc=1-0&qs=n&sk=&cvid=8D5C93C5E3D743BDA61360B5AA07FD7B&ghsh=0&ghacc=0&ghpl=",// from moto
                "&form=QBLH&sp=-1&ghc=1&lq=1&pq=safety+data+sheet+stabil+starting+fluid+sds+pdf+fuel+treatment+and+stabilizer+protection&sc=1-88&qs=n&sk=&cvid=6B61C3F154B540EE92118149128905EF&ghsh=0&ghacc=0&ghpl=",
                "&id=wfwfwevdfe"};
        return url+bing_endings[5];
    }

    public static int findEmpty(String[]terms){
        int lowest=terms.length-1;
        for (int i=0;i<terms.length-1;i++){
            if ((terms[i].equals(""))&&i<lowest)
                lowest=i;
        }
        return lowest;
    }

    public static boolean stringContainsItemFromList(String inputStr, String[] items) {
        return Arrays.stream(items).anyMatch(inputStr::contains);
    }

    public static String URLcompleter(String end, String url3) {
        //     Log.d("check", "have to make up url");
        if (url3.contains(".com")) {
            int end_index = url3.indexOf(".com");
            String baseURI = url3.substring(0, end_index + 4);
            while (end.charAt(0) == '.') {
                //               Log.d("check", "sds_url_end: " + end);
                end = end.substring(1);
            }
            //         Log.d("check", "removed periods, " + end.charAt(0));
            if (end.charAt(0) != '/') {
                end = "/" + end;
            }
            return (baseURI + end);
        }
        return null;
    }
}
