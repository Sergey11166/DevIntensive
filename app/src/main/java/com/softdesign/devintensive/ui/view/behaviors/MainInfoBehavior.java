package com.softdesign.devintensive.ui.view.behaviors;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author Sergey Vorobyev
 */

@SuppressWarnings("unused")
public class MainInfoBehavior extends AppBarLayout.ScrollingViewBehavior {

    public MainInfoBehavior(Context context, AttributeSet attrs) {
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof LinearLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        final CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        if (dependency instanceof LinearLayout && !(dependency instanceof AppBarLayout)) {
            if (lp.getAnchorId() != -1 && (lp.getAnchorId() != dependency.getId())) {
                return false;
            }
        } else {
            return false;
        }
        lp.topMargin = dependency.getHeight();
        child.setLayoutParams(lp);
        return super.onDependentViewChanged(parent, child, dependency);
    }
}
