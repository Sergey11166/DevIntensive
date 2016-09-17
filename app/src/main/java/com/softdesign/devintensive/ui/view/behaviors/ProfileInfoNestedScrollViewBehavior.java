package com.softdesign.devintensive.ui.view.behaviors;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author Sergey Vorobyev
 */

@SuppressWarnings("unused")
public class ProfileInfoNestedScrollViewBehavior extends AppBarLayout.ScrollingViewBehavior {

    public ProfileInfoNestedScrollViewBehavior(Context context, AttributeSet attrs) {
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof LinearLayout && !(dependency instanceof AppBarLayout);
    }

    /**
     * @param child Profile info NestedScrollView
     * @param dependency Scores LinearLayout
     */
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        int translation = (int) (dependency.getTranslationY() + dependency.getHeight());
        ViewCompat.offsetTopAndBottom(child, translation);
        return super.onDependentViewChanged(parent, child, dependency);
    }
}
