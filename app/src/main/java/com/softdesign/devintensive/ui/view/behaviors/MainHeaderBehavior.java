package com.softdesign.devintensive.ui.view.behaviors;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.softdesign.devintensive.R;

import static com.softdesign.devintensive.utils.UIHandler.getAppBarSize;
import static com.softdesign.devintensive.utils.UIHandler.getMinHeight;
import static com.softdesign.devintensive.utils.UIHandler.getStatusBarHeight;

/**
 * @author Sergey Vorobyev
 */

public class MainHeaderBehavior extends AppBarLayout.ScrollingViewBehavior {

    private Context mContext;
    private float mMinLayoutHeight;
    private float mMaxLayoutHeight;
    private float mMinAppbarHeight;
    private float mMaxAppbarHeight;

    public MainHeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MainHeaderBehavior);
        mMinLayoutHeight = a.getDimensionPixelSize(R.styleable.MainHeaderBehavior_min_height, 0);
        a.recycle();
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        final CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();

        AppBarLayout appBarLayout;

        if (dependency instanceof AppBarLayout) {
            appBarLayout = (AppBarLayout) dependency;
            if (lp.getAnchorId() != -1 && lp.getAnchorId() != appBarLayout.getId()) {
                return false;
            }
        } else {
            return false;
        }

        if (mMaxLayoutHeight == 0) {
            initProperties(child, appBarLayout);
        }

        final float curAppBarHeight = appBarLayout.getBottom() - mMinAppbarHeight;
        final float expandedPercentageFactor = curAppBarHeight / mMaxAppbarHeight;
        lp.height = (int) (mMinLayoutHeight + (mMaxLayoutHeight - mMinLayoutHeight) * expandedPercentageFactor);

        child.setLayoutParams(lp);

        return super.onDependentViewChanged(parent, child, dependency);
    }

    private void initProperties(View child, AppBarLayout dependency) {
        mMaxLayoutHeight = child.getHeight();
        if (mMinLayoutHeight == 0.0f) mMinLayoutHeight = getMinHeight(child);
        mMinAppbarHeight = getStatusBarHeight(mContext) + getAppBarSize(mContext);
        mMaxAppbarHeight = dependency.getHeight() - mMinAppbarHeight;
    }
}
