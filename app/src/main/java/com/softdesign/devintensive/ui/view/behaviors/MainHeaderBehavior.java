package com.softdesign.devintensive.ui.view.behaviors;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.softdesign.devintensive.R;

import static com.softdesign.devintensive.utils.UIHelper.getAppBarSize;
import static com.softdesign.devintensive.utils.UIHelper.getMinHeight;
import static com.softdesign.devintensive.utils.UIHelper.getStatusBarHeight;

/**
 * @author Sergey Vorobyev
 */

public class MainHeaderBehavior extends AppBarLayout.ScrollingViewBehavior {

    private float mMinHeaderHeight;
    private float mMaxHeaderHeight;
    private float mMinAppbarHeight;
    private float mMaxAppbarHeight;

    public MainHeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MainHeaderBehavior);
        mMinHeaderHeight = a.getDimensionPixelSize(R.styleable.MainHeaderBehavior_min_height, 0);
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

        if (mMinHeaderHeight == 0) {
            initProperties(child, appBarLayout);
        }

        final float curAppBarHeight = appBarLayout.getBottom() - mMinAppbarHeight;
        final float expandedPercentageFactor = curAppBarHeight / mMaxAppbarHeight;
        lp.height = (int) (mMinHeaderHeight + (mMaxHeaderHeight - mMinHeaderHeight) * expandedPercentageFactor);

        child.setLayoutParams(lp);

        return super.onDependentViewChanged(parent, child, dependency);
    }

    private void initProperties(View child, AppBarLayout dependency) {
        mMaxHeaderHeight = child.getHeight();
        if (mMinHeaderHeight == 0.0f) mMinHeaderHeight = getMinHeight(child);
        mMinAppbarHeight = getStatusBarHeight() + getAppBarSize();
        mMaxAppbarHeight = dependency.getHeight() - mMinAppbarHeight;
    }
}
