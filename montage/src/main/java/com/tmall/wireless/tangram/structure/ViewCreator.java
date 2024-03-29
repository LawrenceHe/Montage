/*
 * MIT License
 *
 * Copyright (c) 2018 Alibaba Group
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tmall.wireless.tangram.structure;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.tmall.wireless.tangram.TangramBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by villadora on 15/9/10.
 */
public class ViewCreator<V extends View> {

    private static final String TAG = "ViewCreator";

    private Class<V> mClz;

    private V view;

    public ViewCreator(@NonNull final Class<V> clz) {
        this.mClz = clz;
    }

    public V create(@NonNull Context context, ViewGroup parent) {
        try {
            Constructor<V> constructor = mClz.getConstructor(Context.class);
            view = constructor.newInstance(context);
            return view;
        } catch (InstantiationException e) {
            handleException(e);
        } catch (IllegalAccessException e) {
            handleException(e);
        } catch (InvocationTargetException e) {
            handleException(e);
        } catch (NoSuchMethodException e) {
            handleException(e);
        }

        throw new RuntimeException("Failed to create View of class: " + mClz.getName());
    }

    private void handleException(Exception e) {
        if (TangramBuilder.isPrintLog())
            Log.e(TAG, "Exception when create instance: " + mClz.getCanonicalName() + "  message: " + e.getMessage(), e);
        throw new RuntimeException(e);
    }
}
