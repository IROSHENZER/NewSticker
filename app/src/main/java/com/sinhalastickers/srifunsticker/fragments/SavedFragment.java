package com.sinhalastickers.srifunsticker.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sinhalastickers.srifunsticker.BuildConfig;
import com.sinhalastickers.srifunsticker.R;
import com.sinhalastickers.srifunsticker.adapters.StickerPackListAdapter;
import com.sinhalastickers.srifunsticker.adapters.StickerPackListItemViewHolder;
import com.sinhalastickers.srifunsticker.base.SavedPackDetailActivity;
import com.sinhalastickers.srifunsticker.base.StickerPack;
import com.sinhalastickers.srifunsticker.helpers.Admob;
import com.sinhalastickers.srifunsticker.helpers.StickerBook;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SavedFragment extends Fragment {


    public SavedFragment() {
        // Required empty public constructor
    }

    public static final String EXTRA_STICKER_PACK_LIST_DATA = "sticker_pack_list";
    private static final int STICKER_PREVIEW_DISPLAY_LIMIT = 5;
    private static final String TAG = "StickerPackList";
    private LinearLayoutManager packLayoutManager;
    private static RecyclerView packRecyclerView;
    private static StickerPackListAdapter allStickerPacksListAdapter;
   // WhiteListCheckAsyncTask whiteListCheckAsyncTask;
    ArrayList<StickerPack> stickerPackList;
    public static Context context;
    public static String newName, newCreator;
    public View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_saved, container, false);

        packRecyclerView = view.findViewById(R.id.sticker_pack_list);
        stickerPackList = StickerBook.getAllStickerPacks();//getIntent().getParcelableArrayListExtra( EXTRA_STICKER_PACK_LIST_DATA);

         String aa = stickerPackList.size()+"";
        Log.d("stickerbook",aa);
        showStickerPackList(stickerPackList);

        Admob.createLoadBanner(getContext(), view);

        return view;
    }

    public void showStickerPackList(ArrayList<StickerPack> stickerPackList) {
        allStickerPacksListAdapter = new StickerPackListAdapter(stickerPackList, onAddButtonClickedListener);
        packRecyclerView.setAdapter(allStickerPacksListAdapter);
        packLayoutManager = new LinearLayoutManager(context);
        packLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                packRecyclerView.getContext(),
                packLayoutManager.getOrientation()
        );
        packRecyclerView.addItemDecoration(dividerItemDecoration);
        packRecyclerView.setLayoutManager(packLayoutManager);
        packRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(this::recalculateColumnCount);
    }

    private StickerPackListAdapter.OnAddButtonClickedListener onAddButtonClickedListener = new StickerPackListAdapter.OnAddButtonClickedListener() {
        @Override
        public void onAddButtonClicked(StickerPack pack) {
            if(pack.getStickers().size()>=3) {
                Intent intent = new Intent();
                intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK");
                intent.putExtra(SavedPackDetailActivity.EXTRA_STICKER_PACK_ID, pack.getIdentifier());
                intent.putExtra(SavedPackDetailActivity.EXTRA_STICKER_PACK_AUTHORITY, BuildConfig.CONTENT_PROVIDER_AUTHORITY);
                intent.putExtra(SavedPackDetailActivity.EXTRA_STICKER_PACK_NAME, pack.getName());
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create();
                alertDialog.setTitle("Invalid Action");
                alertDialog.setMessage("In order to be applied to WhatsApp, the sticker pack must have at least 3 stickers. Please add more stickers first.");
                alertDialog.show();
            }
        }
    };

    private void recalculateColumnCount() {
        final int previewSize = getResources().getDimensionPixelSize(R.dimen.sticker_pack_list_item_preview_image_size);
        int firstVisibleItemPosition = packLayoutManager.findFirstVisibleItemPosition();
        StickerPackListItemViewHolder viewHolder = (StickerPackListItemViewHolder) packRecyclerView.findViewHolderForAdapterPosition(firstVisibleItemPosition);
        if (viewHolder != null) {
            final int max = Math.max(viewHolder.imageRowView.getMeasuredWidth() / previewSize, 1);
            int numColumns = Math.min(STICKER_PREVIEW_DISPLAY_LIMIT, max);
            allStickerPacksListAdapter.setMaxNumberOfStickersInARow(numColumns);
        }
    }

}
