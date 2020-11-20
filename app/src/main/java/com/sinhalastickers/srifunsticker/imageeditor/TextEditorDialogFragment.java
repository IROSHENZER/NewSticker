package com.sinhalastickers.srifunsticker.imageeditor;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sinhalastickers.srifunsticker.R;
import com.sinhalastickers.srifunsticker.helpers.AppArrays;
import com.sinhalastickers.srifunsticker.helpers.SharedPref;


/**
 * Created by Burhanuddin Rashid on 1/16/2018.
 */

public class TextEditorDialogFragment extends DialogFragment {

    public static final String TAG = TextEditorDialogFragment.class.getSimpleName();
    public static final String EXTRA_INPUT_TEXT = "extra_input_text";
    public static final String EXTRA_COLOR_CODE = "extra_color_code";
    public static final String EXTRA_FONT_CODE = "extra_font_code";
    private EditText mAddTextEditText;
    private TextView mAddTextDoneTextView;
    private InputMethodManager mInputMethodManager;
    private int mColorCode;
    private TextEditor mTextEditor;
    private Boolean toggleButonColor = false;
    private Boolean toggleButonFont = false;
    Typeface mEmojiTypeFace;
    static Typeface checek_typeface;
    String text_font;
    SharedPref sharedPref;

    public interface TextEditor {
        void onDone(String inputText, int colorCode, Typeface typeface);
    }


    //Show dialog with provide text and text color
    public static TextEditorDialogFragment show(@NonNull AppCompatActivity appCompatActivity,
                                                 @NonNull String inputText,
                                                 @ColorInt int colorCode,
                                                 Typeface mEmojiTypeFace) {
        Bundle args = new Bundle();
        args.putString(EXTRA_INPUT_TEXT, inputText);
        args.putInt(EXTRA_COLOR_CODE, colorCode);
        args.putString(EXTRA_FONT_CODE, String.valueOf(mEmojiTypeFace));
        TextEditorDialogFragment fragment = new TextEditorDialogFragment();
        fragment.setArguments(args);
        fragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return fragment;
    }

    //Show dialog with default text input as empty and text color white
    public static TextEditorDialogFragment show(@NonNull AppCompatActivity appCompatActivity) {
        return show(appCompatActivity,
                "WA Stickers", ContextCompat.getColor(appCompatActivity, R.color.white), checek_typeface);
    }

    @Override
    public void onStart() {
        super.onStart();
        sharedPref = new SharedPref(getActivity());
        Dialog dialog = getDialog();

        checek_typeface = Typeface.createFromAsset(getActivity().getAssets(), "All_Fonts/Fonts00001.otf");

        //Make dialog full screen with transparent background
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_text_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAddTextEditText = view.findViewById(R.id.add_text_edit_text);
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mAddTextDoneTextView = view.findViewById(R.id.add_text_done_tv);

        Button btnColor;
        Button btnFont;
        btnColor = view.findViewById(R.id.btnColor);
        btnFont = view.findViewById(R.id.btnFont);

        //Setup the color picker for text color
        RecyclerView addTextColorPickerRecyclerView = view.findViewById(R.id.add_text_color_picker_recycler_view);
        addTextColorPickerRecyclerView.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        addTextColorPickerRecyclerView.setLayoutManager(layoutManager);
        addTextColorPickerRecyclerView.setHasFixedSize(true);

        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addTextColorPickerRecyclerView.setVisibility(View.GONE);
                toggleButonFont = false;

                ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(getActivity());
                //This listener will change the text color when clicked on any color from picker
                colorPickerAdapter.setOnColorPickerClickListener(new ColorPickerAdapter.OnColorPickerClickListener() {
                    @Override
                    public void onColorPickerClickListener(int colorCode) {
                        mColorCode = colorCode;
                        mAddTextEditText.setTextColor(colorCode);
                    }
                });
                addTextColorPickerRecyclerView.setAdapter(colorPickerAdapter);

                if (toggleButonColor){
                    addTextColorPickerRecyclerView.setVisibility(View.GONE);
                    toggleButonColor = false;
                }else{
                    addTextColorPickerRecyclerView.setVisibility(View.VISIBLE);
                    toggleButonColor = true;
                }

            }
        });

        btnFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addTextColorPickerRecyclerView.setVisibility(View.GONE);
                toggleButonColor = false;

                TextAdapter fontAdapter = new TextAdapter(getActivity(), AppArrays.getFontArray(getActivity()));
                addTextColorPickerRecyclerView.setAdapter(fontAdapter);

                fontAdapter.setOnClickLIstner(new OnClickFont() {
                    @Override
                    public void onFontClick(View v, String fontPath, int pos) {
                        text_font = fontPath;
                        mEmojiTypeFace = Typeface.createFromAsset(getActivity().getAssets(), text_font);
                        mAddTextEditText.setTypeface(mEmojiTypeFace);
                        sharedPref.setUser("font",text_font);
                    }
                });

                if (toggleButonFont){
                    addTextColorPickerRecyclerView.setVisibility(View.GONE);
                    toggleButonFont = false;
                }else{
                    addTextColorPickerRecyclerView.setVisibility(View.VISIBLE);
                    toggleButonFont = true;
                }
            }
        });

        mAddTextEditText.setText(getArguments().getString(EXTRA_INPUT_TEXT));
        mColorCode = getArguments().getInt(EXTRA_COLOR_CODE);
        mAddTextEditText.setTextColor(mColorCode);
        mAddTextEditText.setTypeface(mEmojiTypeFace);
        mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        //Make a callback on activity when user is done with text editing
        mAddTextDoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                dismiss();

                String inputText = mAddTextEditText.getText().toString();
                if (!TextUtils.isEmpty(inputText) && mTextEditor != null) {
                    mTextEditor.onDone(inputText, mColorCode,mEmojiTypeFace);
                }
            }
        });





    }


    //Callback to listener if user is done with text editing
    public void setOnTextEditorListener(TextEditor textEditor) {
        mTextEditor = textEditor;
    }
}
