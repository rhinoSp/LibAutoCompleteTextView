package com.rhino.actv;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ListAdapter;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author LuoLin
 * @since Create on 2018/7/27.
 */
public class AutoCompleteTextView extends AppCompatAutoCompleteTextView {

    public final static String INPUT_CACHE_FILE_NAME = "AutoCompleteTextView_input_cache";
    public String mInputCacheKey;
    public SharedPreferences mSharedPreferences;
    public ArrayAdapter<String> mInputCacheAdapter;
    public List<String> mInputCacheList;

    public AutoCompleteTextView(Context context) {
        super(context);
        init();
    }

    public AutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        this.mSharedPreferences = getContext().getSharedPreferences(INPUT_CACHE_FILE_NAME, Context.MODE_PRIVATE);
        this.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {
        super.setAdapter(adapter);
        this.mInputCacheAdapter = (ArrayAdapter<String>) adapter;
    }

    public void checkParamValid() {
        if (TextUtils.isEmpty(mInputCacheKey)) {
            throw new RuntimeException("mInputCacheKey is null");
        }
    }

    public void setInputCacheKey(@NonNull String key) {
        mInputCacheKey = key;
        checkParamValid();
        mInputCacheList = getAllInputCache();
        notifyAll(mInputCacheList);
    }

    public void saveInputCache(@NonNull String input) {
        saveInputCache(input, false);
    }

    public void saveInputCache(@NonNull String input, boolean notify) {
        checkParamValid();
        if (TextUtils.isEmpty(input)) {
            return;
        }
        mInputCacheList.add(0, input);
        if (notify) {
            notifyAll(mInputCacheList);
        }
        String inputCacheStr = mInputCacheList.toString();
        mSharedPreferences.edit()
                .putString(mInputCacheKey, inputCacheStr.substring(1, inputCacheStr.length()-1))
                .apply();
    }

    @NonNull
    public List<String> getAllInputCache() {
        checkParamValid();
        String inputCache = mSharedPreferences.getString(mInputCacheKey, "");
        String[] inputCacheArray = inputCache.split(", ");
        List<String> list = new ArrayList<>();
        for (String input : inputCacheArray) {
            if (TextUtils.isEmpty(input)) {
                continue;
            }
            list.add(input);
        }
        return list;
    }

    public void notifyAll(@NonNull List<String> list) {
        mInputCacheAdapter.clear();
        mInputCacheAdapter.addAll(list);
    }

}
