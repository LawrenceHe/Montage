package com.zhaodongdb.wireless;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tmall.wireless.vaf.virtualview.Helper.ImageLoader;
import com.tmall.wireless.vaf.virtualview.view.image.ImageBase;

public class ImageTarget implements Target {

    ImageBase mImageBase;

    ImageLoader.Listener mListener;

    public ImageTarget(ImageBase imageBase) {
        mImageBase = imageBase;
    }

    public ImageTarget(ImageLoader.Listener listener) {
        mListener = listener;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        mImageBase.setBitmap(bitmap, true);
        if (mListener != null) {
            mListener.onImageLoadSuccess(bitmap);
        }
        Log.d("TangramActivity", "onBitmapLoaded " + from);
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        if (mListener != null) {
            mListener.onImageLoadFailed();
        }
        Log.d("TangramActivity", "onBitmapFailed ");
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        Log.d("TangramActivity", "onPrepareLoad ");
    }
}
