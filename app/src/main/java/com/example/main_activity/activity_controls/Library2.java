package com.example.main_activity.activity_controls;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.basicactivity.R;
import com.example.main_activity.BuildConfig;
import com.example.main_activity.backend.Product;
import com.example.main_activity.backend.SDSAccesser;

import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.util.Objects;


public class Library2 extends Fragment {
    private Library2Binding binding;
    private ArrayList<Product> global_array_list;

    private TextView emptyText;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = Library2Binding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        final Button back_button = binding.mytoolbar.backButton;
        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavHostFragment.findNavController(Library2.this)
                        .navigate(R.id.action_LibraryFragment_to_FirstFragment);
            }
        });
        // this version below was working fine, but it makes the app crash when the orientation changes, so went back to the binding way
    //    final Button button = getActivity().findViewById(R.id.export_button);
        final Button share_button = binding.mytoolbar.exportButton;
        share_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
   //             Toast.makeText(getActivity(), "trying to start share", Toast.LENGTH_SHORT).show();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("application/json");
                File file=Json.get_file(getActivity());
                Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(getActivity()),
                        BuildConfig.APPLICATION_ID + ".provider", file);
                Log.d("check", "uri: " + uri.toString());
                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

        final ListView listview = binding.listview;
      //  emptyText = (TextView) getActivity().findViewById(R.id.empty);
        //    listview.setEmptyView(emptyText);
        emptyText=binding.empty;
        emptyText.setVisibility(View.GONE);

        global_array_list=Json.get_array_list(getActivity());
        Log.d("check", "how many items should be in the library? "+global_array_list.size());
        check_empty();
            final StableArrayAdapter adapter = new StableArrayAdapter(getActivity(),
                    R.layout.list_item, global_array_list);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    LibraryPdf.received_packet=false;
                    Bundle result = new Bundle();
                    int pos = parent.getPositionForView(view);
                    ArrayList<String> packet=new ArrayList<>();
                    packet.add(global_array_list.get(position).pname);
                    packet.add(global_array_list.get(position).url);
                    result.putStringArrayList("bundleKey", packet);
                    getParentFragmentManager().setFragmentResult("requestKey", result);

     //               Toast.makeText(getActivity(), pos + "for overall item", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(Library2.this)
                            .navigate(R.id.action_LibraryFragment_to_LibraryPdfFragment);

                }
            });


    }

    public void check_empty(){
        if (global_array_list.size()==0 || global_array_list.isEmpty()){
            emptyText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class StableArrayAdapter extends ArrayAdapter {
        private final int mResource;
        private Context mContext;

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List objects) {
            super(context, textViewResourceId, global_array_list);
            this.mResource = textViewResourceId;
            this.mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(mContext);
                v = vi.inflate(mResource, null);
            }

            Product p = (Product) getItem(position);
            ImageView img = null;
            if (p != null) {
                TextView t1 = (TextView) v.findViewById(R.id.firstLine);
                img = (ImageView) v.findViewById(R.id.imageview);

                if (t1 != null) {
                    Log.d("check", "about to set text, have " + p.pname + " position is " + position);
                    t1.setText(p.pname);
                }
            }
            try {
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.imageview:

                                PopupMenu popup = new PopupMenu(getActivity(), v);
                                popup.getMenuInflater().inflate(R.menu.popup_menu,
                                        popup.getMenu());
                                popup.show();
                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {

                                        switch (item.getItemId()) {
                                            case R.id.delete:
                                                global_array_list.remove(position);
                                                Json.update_json(global_array_list, getActivity());
                                                notifyDataSetChanged();
                                                check_empty();
                                                SDSAccesser.delete_file(global_array_list.get(position).pname, getActivity());
                                                break;
                                            case R.id.share:
                                                Intent sendIntent = new Intent();
                                                sendIntent.setAction(Intent.ACTION_SEND);
                                                sendIntent.putExtra(Intent.EXTRA_TEXT, global_array_list.get(position).url);
                                                sendIntent.setType("text/plain");
                                                Log.d("check", "trying to share link: "+global_array_list.get(position).url+" from position: "+position+" from " +
                                                                "item: "+global_array_list.get(position).toString());
                                                Intent shareIntent = Intent.createChooser(sendIntent, null);
                                                startActivity(shareIntent);
                                                break;

                                            default:
                                                break;
                                        }

                                        return true;
                                    }
                                });

                                break;

                            default:
                                break;
                        }
                    }
                });

            } catch (Exception e) {

                e.printStackTrace();
            }
            return v;
        }
    }

}