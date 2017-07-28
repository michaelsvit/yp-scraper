package com.michaelsvit.kolnoa;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Michael on 7/18/2016.
 */
public class FixedAspectRatioImageView extends ImageView{
    public FixedAspectRatioImageView(Context context) {
        super(context);
    }

    public FixedAspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedAspectRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = (int) (widthSize * 1.4);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
    }
}
