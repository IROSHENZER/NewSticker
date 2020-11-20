package com.sinhalastickers.srifunsticker.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sinhalastickers.srifunsticker.R;
import com.sinhalastickers.srifunsticker.adapters.PackAdapter;
import com.sinhalastickers.srifunsticker.adapters.SlideAdapter;
import com.sinhalastickers.srifunsticker.base.StickerPack;
import com.sinhalastickers.srifunsticker.clients.ApiClient;
import com.sinhalastickers.srifunsticker.helpers.Admob;
import com.sinhalastickers.srifunsticker.interfaces.StickerInterface;
import com.sinhalastickers.srifunsticker.models.Pack;
import com.sinhalastickers.srifunsticker.models.Packs;
import com.sinhalastickers.srifunsticker.models.Slide;
import com.sinhalastickers.srifunsticker.models.Slides;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreFragment extends Fragment {

    private View view;
    private ProgressBar progress_bar_explore;
    private RecyclerView recyclerView;
    private Context context;
    private SweetAlertDialog dialog;
    public static ArrayList<StickerPack> stickerPackList;
    public ArrayList<Pack> pack_list;
    public PackAdapter dataAdapter;
    public TextView loadingPack;
    private RecyclerView slide_recylieview;

    public ExploreFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ExploreFragment newInstance(String param1, String param2) {
        ExploreFragment fragment = new ExploreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_explore, container, false);

        progress_bar_explore = view.findViewById(R.id.progress_bar_explore);

        slide_recylieview = view.findViewById(R.id.slide_recylieview);
        LinearLayoutManager linearLayoutManager_slide = new LinearLayoutManager(getActivity());
        linearLayoutManager_slide.setOrientation(LinearLayoutManager.HORIZONTAL);
        slide_recylieview.setHasFixedSize(true);
        slide_recylieview.setLayoutManager(linearLayoutManager_slide);

        getSlides();

        context = getActivity();
        loadingPack = view.findViewById(R.id.loadingPack);
        progress_bar_explore = view.findViewById(R.id.progress_bar_explore);
        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        getAllPacks();

        Admob.createLoadBanner(getContext(), view);

        return view;
    }

    private void getSlides() {

        StickerInterface apiService =
                ApiClient.getClient().create(StickerInterface.class);

        retrofit2.Call<Slides> call = apiService.getAllSticker();

        call.enqueue(new Callback<Slides>() {
            @Override
            public void onResponse(retrofit2.Call<Slides> call, Response<Slides> response) {

                Slides jsonResponse = response.body();

                ArrayList<Slide> slides  = jsonResponse.getSlides();
                SlideAdapter dataAdapter = new SlideAdapter(getContext(), slides);
                slide_recylieview.setAdapter(dataAdapter);
                dataAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(retrofit2.Call<Slides> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });

    }


    private void getAllPacks() {

        StickerInterface apiService =
                ApiClient.getClient().create(StickerInterface.class);

        retrofit2.Call<Packs> call = apiService.getAllPacks();

        call.enqueue(new Callback<Packs>() {
            @Override
            public void onResponse(retrofit2.Call<Packs> call, Response<Packs> response) {

                Packs jsonResponse = response.body();

                progress_bar_explore.setVisibility(View.GONE);

                pack_list = jsonResponse.getPacks();

                dataAdapter = new PackAdapter(context, pack_list,progress_bar_explore,loadingPack);
                recyclerView.setAdapter(dataAdapter);
                dataAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(retrofit2.Call<Packs> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });


    }


}
