package com.example.fb0122.oneday.weidget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fb0122.oneday.R;

/**
 * Created by fengbo on 16/8/18.
 */
public class MyEditText extends EditText {

    private final static String TAG = "MyEditText";

    private Context context;

    private TextView textView;

    public MyEditText(Context context, AttributeSet attrs ) {
        super(context, attrs);
    }

    public void setStatus(boolean editAble){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);
        if (editAble){
            Log.e(TAG,textView.getText().toString());
            textView.setVisibility(GONE);
            setVisibility(VISIBLE);
            setTextColor(getResources().getColor(R.color.black));
            this.setHint(textView.getText().toString());
            requestFocus();
            setCursorVisible(false);
            imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
            imm.showSoftInput(this,0);
        }else{
            textView.setVisibility(VISIBLE);
            setVisibility(GONE);
            textView.setText(getText().toString());
            textView.setTextColor(getResources().getColor(R.color.black));
            setFocusable(editAble);
            setEnabled(editAble);
            setFocusableInTouchMode(editAble);
            imm.hideSoftInputFromWindow(getWindowToken(),0);
        }
    }

    public void setContext(Context context){
        this.context = context;
    }

    public Context getMyContext(){
        return context;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }
}
