package com.darewro.rider.view.customViews;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;

public class UnderlinedTextView extends androidx.appcompat.widget.AppCompatTextView {
    public UnderlinedTextView(Context context) {
        super(context);
        setPaintFlags(getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public UnderlinedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPaintFlags(getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public UnderlinedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPaintFlags(getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }


}
