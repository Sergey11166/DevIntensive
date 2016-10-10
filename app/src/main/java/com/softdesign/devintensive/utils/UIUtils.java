package com.softdesign.devintensive.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.softdesign.devintensive.R;

import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * @author Sergey Vorobyev
 */

@SuppressWarnings("unused")
public class UIUtils {

    public static int getMinHeight(View v) {
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int widthMeasureSpec = View.MeasureSpec
                .makeMeasureSpec(getScreenWidth(v), View.MeasureSpec.AT_MOST);

        v.measure(widthMeasureSpec, heightMeasureSpec);
        return v.getMeasuredHeight();
    }

    private static int getScreenWidth(View view) {
        WindowManager manager = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
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

    public static void showSoftKeyboard(View view) {
        view.postDelayed(() -> {
            InputMethodManager keyboard = (InputMethodManager)
                    view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(view, 0);
        }, 200);
    }

    public static void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showToast(Handler handler, String message) {
        if (handler != null) {
            handler.post(() -> showToast(message));
        } else {
            showToast(message);
        }
    }

    public static void showToast(String message) {
        Toast.makeText(App.get(), message, Toast.LENGTH_LONG).show();
    }

    public static void handleResponseError(String logTag, Throwable error) {
        handleResponseError(null, logTag, error);
    }

    public static void handleResponseError(Handler handler,
                                           String logTag, Throwable error) {
        if (error instanceof HttpException) {
            HttpException exception = (HttpException) error;
            if (exception.code() == 403) {
                Log.d(logTag, "handleError(): wrong username or password");
                showToast(handler, App.get().getString(R.string.error_wrong_username_or_password));
            } else {
                Log.d(logTag, "handleError(): response code is " + exception.code());
                showToast(handler, App.get().getString(R.string.error_unknown_error)
                        + " " + exception.code());
            }
        } else if (error instanceof SocketTimeoutException) {
            Log.e(logTag, "onResponse(): check internet connection", error);
            showToast(handler, App.get().getString(R.string.error_failed_to_connect_to_server));
        } else if (error instanceof Exception) {
            Log.e(logTag, "onResponse(): unknown error", error);
            showToast(handler, App.get().getString(R.string.error_unknown_error));
        }
    }
}
