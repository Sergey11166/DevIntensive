package com.softdesign.devintensive.data.network;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.softdesign.devintensive.utils.App;
import com.squareup.picasso.Picasso;

/**
 * @author Sergey Vorobyev.
 */

public class PicassoCache {

    private Picasso mPicassoInstance;

    public PicassoCache() {
        OkHttp3Downloader downloader = new OkHttp3Downloader(App.get(), Integer.MAX_VALUE);
        Picasso.Builder builder = new Picasso.Builder(App.get());
        builder.downloader(downloader);

        mPicassoInstance = builder.build();
        Picasso.setSingletonInstance(mPicassoInstance);
    }

    public Picasso getPicassoInstance() {
        return mPicassoInstance;
    }
}
