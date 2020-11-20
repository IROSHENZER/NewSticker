package com.sinhalastickers.srifunsticker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.sinhalastickers.srifunsticker.adapters.QueryAdapter;
import com.sinhalastickers.srifunsticker.clients.ApiClient;
import com.sinhalastickers.srifunsticker.helpers.Admob;
import com.sinhalastickers.srifunsticker.interfaces.StickerInterface;
import com.sinhalastickers.srifunsticker.models.Sticker;
import com.sinhalastickers.srifunsticker.models.Stickers;

import java.util.ArrayList;

import retrofit2.Callback;
import retrofit2.Response;

public class StickersCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Context context;
    public ArrayList<Sticker> stickerPackList;
    public QueryAdapter dataAdapter;
    public ProgressBar progress_bar_explore;
    public TextView loadingPack;
    String pack_name,pack_publisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stickers_category);

        View view=getWindow().getDecorView().getRootView();
        Admob.createLoadBanner(getApplicationContext(), view);

        context = this;
        Intent i = getIntent();
        pack_name = i.getStringExtra("pack_name");
        pack_publisher = i.getStringExtra("pack_publisher");
        Log.d("PACK_PUBLISHER",pack_publisher+"");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(pack_name);

        loadingPack = findViewById(R.id.loadingPack);
        progress_bar_explore = findViewById(R.id.progress_bar_explore);

        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        getAllStickers();

    }

    private void getAllStickers() {

        StickerInterface apiService =
                ApiClient.getClient().create(StickerInterface.class);

        retrofit2.Call<Stickers> call = apiService.getCatStickers(pack_name);

        call.enqueue(new Callback<Stickers>() {
            @Override
            public void onResponse(retrofit2.Call<Stickers> call, Response<Stickers> response) {

                Stickers jsonResponse = response.body();
                stickerPackList = jsonResponse.getStickers();
                dataAdapter = new QueryAdapter(pack_publisher,context, stickerPackList,progress_bar_explore,loadingPack);
                recyclerView.setAdapter(dataAdapter);
                dataAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(retrofit2.Call<Stickers> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });


    }


}
