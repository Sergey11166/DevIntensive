package com.softdesign.devintensive.ui.view.behaviors;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import static com.softdesign.devintensive.utils.UIUtils.getAppBarSize;
import static com.softdesign.devintensive.utils.UIUtils.getMinHeight;
import static com.softdesign.devintensive.utils.UIUtils.getStatusBarHeight;

/**
 * @author Sergey Vorobyev
 */

@SuppressWarnings("unused")
public class ProfileScoresLinearLayoutBehavior<Header extends LinearLayout>
        extends CoordinatorLayout.Behavior<Header> {

    private float mMinScoresHeight;
    private float mMaxScoresHeight;
    private float mMinAppbarHeight;
    private float mMaxAppbarHeight;

    public ProfileScoresLinearLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, Header child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    /**
     * @param child Scores LinearLayout
     * @param dependency AppBarLayout
     */
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, Header child, View dependency) {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();

        AppBarLayout appBarLayout = (AppBarLayout) dependency;

        if (mMinScoresHeight == 0.0f) {
            initProperties(child, appBarLayout);
        }

        float currentAppBarHeight = appBarLayout.getBottom() - mMinAppbarHeight;
        float expandedPercentageFactor = currentAppBarHeight / mMaxAppbarHeight;
        lp.height = (int) (mMinScoresHeight +
                (mMaxScoresHeight - mMinScoresHeight) * expandedPercentageFactor);

        child.setTranslationY(appBarLayout.getBottom());
        child.setLayoutParams(lp);

        return super.onDependentViewChanged(parent, child, dependency);
    }

    private void initProperties(View child, AppBarLayout dependency) {
        mMaxScoresHeight = child.getHeight();
        mMinScoresHeight = getMinHeight(child);
        mMinAppbarHeight = getStatusBarHeight(child.getContext()) + getAppBarSize(child.getContext());
        mMaxAppbarHeight = dependency.getHeight() - mMinAppbarHeight;
    }
}
