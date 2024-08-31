package com.example.main_activity.activity_controls;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.basicactivity.R;
import com.example.main_activity.backend.Product;
import com.example.main_activity.backend.SDSAccesser;
import com.example.main_activity.backend.Data;
import com.example.main_activity.backend.DownloadPdf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LinkDisplay {
    public static void show_link(String link, Data data1){
        Log.d("check", "in link_display, product name: "+data1.global_name);
        Log.d("check","link: "+link);
        if (SecondFragment.still_want_result) {
            ArrayList<Product> array_list = Json.get_array_list(data1.current_activity);
            Product just_found = new Product();
            if (data1.name_from_pdf!=null && !data1.name_from_pdf.isEmpty())
                just_found.pname=data1.name_from_pdf;
            else
                just_found.pname = data1.global_name;
            just_found.url = link;
            boolean repeat=false;
            String name_clean=Json.name_cleaner(just_found.pname);
            for (int i=0;i<array_list.size();i++){
                if (array_list.get(i).pname.equals(name_clean)){
                    repeat=true;
                    Log.d("check","repeat name: "+just_found.pname);
                }
            }
            if (!repeat) {
                array_list.add(just_found);
                Json.update_json(array_list, data1.current_activity);
            }

            SDSAccesser.save_sds(data1.global_name, link, data1.current_activity);

            Fragment fragment = SecondFragment.get_current();
            Log.d("check", "just received fragment: " + fragment);
            ArrayList<String> packet=new ArrayList<>();
            packet.add(data1.global_name);
            packet.add(link);
            Bundle result = new Bundle();
            result.putStringArrayList("bundleKey", packet);
            fragment.getParentFragmentManager().setFragmentResult("SuccessrequestKey", result);

            Success.received_packet=false;

            NavHostFragment.findNavController(fragment)
                    .navigate(R.id.action_SecondFragment_to_SuccessFragment);
        }
        else Log.d("check", "didn't want result anymore");

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



   //     data1.current_activity.onActivityResult();
        /*
        Navigation.findNavController(fragment)
                .navigate(R.id.action_SecondFragment_to_SuccessFragment);

         */


        /*
        Fragment navHostFragment = supportFragmentManager.findFragmentById(R.id.my_fragment_container_view_id) as NavHostFragment;
        String s= navHostFragment.navController;

         */
    }
}
