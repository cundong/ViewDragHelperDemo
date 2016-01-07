package com.cundong.viewdraghelper.view;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.cundong.viewdraghelper.util.UiUtils;

public class ImmersiveView extends View {

    private Context context;

    public ImmersiveView(Context context) {
        super(context);
        this.context = context;
    }

    public ImmersiveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initImmersive();
    }

    private void initImmersive() {
        if (Build.VERSION.SDK_INT < 19) {

            setVisibility(View.GONE);
            return;
        }
        if (!(context instanceof Activity)) {

            setVisibility(View.GONE);
            return;
        }

        int statusBarHeight = UiUtils.getStatusHeight();

        if (0 >= statusBarHeight) {
            setVisibility(View.GONE);
        } else {
            setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = statusBarHeight;
            setLayoutParams(params);
        }
    }
}