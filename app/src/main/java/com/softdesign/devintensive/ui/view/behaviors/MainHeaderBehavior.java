package com.softdesign.devintensive.ui.view.behaviors;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import static com.softdesign.devintensive.utils.UIHelper.getAppBarSize;
import static com.softdesign.devintensive.utils.UIHelper.getMinHeight;
import static com.softdesign.devintensive.utils.UIHelper.getStatusBarHeight;

/**
 * @author Sergey Vorobyev
 */

@SuppressWarnings("unused")
public class MainHeaderBehavior<Header extends LinearLayout>
        extends CoordinatorLayout.Behavior<Header> {

    private float mMinHeaderHeight;
    private float mMaxHeaderHeight;
    private float mMinAppbarHeight;
    private float mMaxAppbarHeight;

    public MainHeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, Header child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, Header child, View dependency) {
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

        if (mMinHeaderHeight == 0.0f) {
            initProperties(child, appBarLayout);
        }

        final float appBarHeight = appBarLayout.getBottom() - mMinAppbarHeight;
        final float expandedPercentageFactor = appBarHeight / mMaxAppbarHeight;
        lp.height = (int) (mMinHeaderHeight + (mMaxHeaderHeight - mMinHeaderHeight) * expandedPercentageFactor);

        child.setTranslationY(appBarLayout.getBottom());
        child.setLayoutParams(lp);

        return super.onDependentViewChanged(parent, child, dependency);
    }

    private void initProperties(Header child, AppBarLayout dependency) {
        mMaxHeaderHeight = child.getHeight();
        if (mMinHeaderHeight == 0.0f) mMinHeaderHeight = getMinHeight(child);
        mMinAppbarHeight = getStatusBarHeight(child.getContext()) + getAppBarSize(child.getContext());
        mMaxAppbarHeight = dependency.getHeight() - mMinAppbarHeight;
    }
}
