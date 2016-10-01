package com.softdesign.devintensive.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @author Sergey Vorobyev.
 */

public class RepositoriesListView extends ListView {

    public RepositoriesListView(Context context) {
        super(context);
    }

    public RepositoriesListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RepositoriesListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
