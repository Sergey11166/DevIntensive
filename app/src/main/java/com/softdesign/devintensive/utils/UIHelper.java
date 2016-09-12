package com.softdesign.devintensive.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * @author Sergey Vorobyev
 */

public class UIHelper {

    public static int getMinHeight(View v) {
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int widthMeasureSpec = View.MeasureSpec
                .makeMeasureSpec(getScreenWidth(), View.MeasureSpec.AT_MOST);

        v.measure(widthMeasureSpec, heightMeasureSpec);
        return v.getMeasuredHeight();
    }

    private static int getScreenWidth() {
        WindowManager manager = (WindowManager) App.get().getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int deviceWidth;
        Point point = new Point();
        display.getSize(point);
        deviceWidth = point.x;
        return deviceWidth;
    }

    public static float getAppBarSize() {
        final TypedArray styledAttributes = App.get().getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        float mActionBarSize = styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return mActionBarSize;
    }

    public static int getStatusBarHeight() {
        int result = 0;
        int resId = App.get().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = App.get().getResources().getDimensionPixelSize(resId);
        }
        return result;
    }
}
