package com.rhino.actv;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
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
public class AutoCompleteTextView extends AppCompatAutoCompleteTextView implements View.OnFocusChangeListener, TextWatcher {

    public final static String DEFAULT_INPUT_CACHE_FILE_NAME = "AutoCompleteTextView_input_cache";
    public final static int DEFAULT_RIGHT_DRAWABLE_COLOR = 0x4A000000;
    public final static int DEFAULT_INPUT_CACHE_MAX_COUNT = 10;
    public int mInputCacheMaxCount = DEFAULT_INPUT_CACHE_MAX_COUNT;
    public String mInputCacheKey;
    public SharedPreferences mSharedPreferences;
    public ArrayAdapter<String> mInputCacheAdapter;
    public List<String> mInputCacheList;
    public Drawable mRightDrawable;
    public OnClickListener mRightDrawableClickListener;
    public boolean mRightDrawableClearStyle;
    public OnFocusChangeListener mOnFocusChangeListener;
    public boolean mHasFocus;


    public AutoCompleteTextView(Context context) {
        super(context);
        init(context, null);
    }

    public AutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AutoCompleteTextView);
            mInputCacheKey = typedArray.getString(R.styleable.AutoCompleteTextView_actv_input_cache_key);
            mInputCacheMaxCount = typedArray.getInt(R.styleable.AutoCompleteTextView_actv_input_cache_max_count,
                    DEFAULT_INPUT_CACHE_MAX_COUNT);
            mRightDrawableClearStyle = typedArray.getBoolean(R.styleable.AutoCompleteTextView_actv_right_drawable_clear_style,
                    true);
            typedArray.recycle();
        }
        super.setOnFocusChangeListener(this);
        super.addTextChangedListener(this);
        this.mSharedPreferences = getContext().getSharedPreferences(DEFAULT_INPUT_CACHE_FILE_NAME, Context.MODE_PRIVATE);
        this.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1));
        this.mRightDrawable = getCompoundDrawables()[2];
        if (mRightDrawableClearStyle && this.mRightDrawable == null) {
            checkRightDrawableClearStyle();
        } else {
            this.setCompoundDrawables(getCompoundDrawables()[0],
                    getCompoundDrawables()[1], this.mRightDrawable, getCompoundDrawables()[3]);
        }

        if (!TextUtils.isEmpty(mInputCacheKey)) {
            mInputCacheList = getAllInputCache();
            notifyAllInputCache(mInputCacheList);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                Rect clearDrawableRect = getCompoundDrawables()[2].getBounds();
                int x = (int) ((getWidth() - event.getX()) * 0.8f);
                int y = (int) ((event.getY() - (getHeight() - clearDrawableRect.height()) / 2) * 0.8f);
                boolean clicked = clearDrawableRect.contains(x, y);
                if (clicked && mRightDrawableClickListener != null) {
                    mRightDrawableClickListener.onClick(this);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {
        super.setAdapter(adapter);
        this.mInputCacheAdapter = (ArrayAdapter<String>) adapter;
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        mOnFocusChangeListener = l;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.mHasFocus = hasFocus;
        if (mRightDrawableClearStyle) {
            if (hasFocus) {
                setRightDrawableVisible(getText().length() > 0);
            } else {
                setRightDrawableVisible(false);
            }
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.onFocusChange(v, hasFocus);
        }
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (mRightDrawableClearStyle && mHasFocus) {
            setRightDrawableVisible(text.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public Drawable getRightDrawable() {
        return this.mRightDrawable;
    }

    public void setRightDrawable(Drawable drawable) {
        this.mRightDrawable = drawable;
    }

    public void setRightDrawableVisible(boolean visible) {
        Drawable rightDrawable = visible ? mRightDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], rightDrawable, getCompoundDrawables()[3]);
    }

    public void setRightDrawableBounds(int left, int top, int right, int bottom) {
        Drawable rightDrawable = getCompoundDrawables()[2];
        if (rightDrawable != null) {
            rightDrawable.getBounds().set(left, top, right, bottom);
            setCompoundDrawables(getCompoundDrawables()[0],
                    getCompoundDrawables()[1], rightDrawable, getCompoundDrawables()[3]);
            postInvalidate();
        }
    }

    public void setRightDrawableClickListener(@Nullable OnClickListener l) {
        this.mRightDrawableClickListener = l;
    }

    public void setRightDrawableClearStyle(boolean enable) {
        this.mRightDrawableClearStyle = enable;
        this.checkRightDrawableClearStyle();
    }

    public void checkRightDrawableClearStyle() {
        if (mRightDrawableClearStyle && this.mRightDrawable == null) {
            this.mRightDrawable = getResources().getDrawable(R.drawable.ic_input_clear);
            this.mRightDrawable.setBounds(0, 0, (int) getTextSize(), (int) getTextSize());
            this.mRightDrawable.setColorFilter(DEFAULT_RIGHT_DRAWABLE_COLOR, PorterDuff.Mode.SRC_IN);
            this.mRightDrawableClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setText("");
                }
            };
        }
    }

    public void setInputCacheMaxCount(int count) {
        this.mInputCacheMaxCount = count;
    }

    public void setInputCacheKey(@NonNull String key) {
        mInputCacheKey = key;
        checkParamValid();
        mInputCacheList = getAllInputCache();
        notifyAllInputCache(mInputCacheList);
    }

    public void checkParamValid() {
        if (TextUtils.isEmpty(mInputCacheKey)) {
            throw new RuntimeException("mInputCacheKey is null");
        }
    }

    public void saveInputCache() {
        saveInputCache(getText().toString(), true);
    }

    public void saveInputCache(@NonNull String input) {
        saveInputCache(input, true);
    }

    public void saveInputCache(@NonNull String input, boolean notify) {
        checkParamValid();
        if (TextUtils.isEmpty(input) || mInputCacheList.contains(input)) {
            return;
        }
        if (mInputCacheList.size() >= mInputCacheMaxCount) {
            List<String> list = mInputCacheList.subList(0, mInputCacheMaxCount - 1);
            mInputCacheList = new ArrayList<>();
            mInputCacheList.addAll(list);
        }
        mInputCacheList.add(0, input);
        if (notify) {
            notifyAllInputCache(mInputCacheList);
        }
        String inputCacheStr = mInputCacheList.toString();
        mSharedPreferences.edit()
                .putString(mInputCacheKey, inputCacheStr.substring(1, inputCacheStr.length() - 1))
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

    public void notifyAllInputCache(@NonNull List<String> list) {
        mInputCacheAdapter.clear();
        mInputCacheAdapter.addAll(list);
    }

}
