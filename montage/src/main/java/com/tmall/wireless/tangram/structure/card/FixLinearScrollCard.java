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

package com.tmall.wireless.tangram.structure.card;

import androidx.annotation.Nullable;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.ScrollFixLayoutHelper;

import org.json.JSONObject;

/**
 * Created by longerian on 16/12/11.
 */

public class FixLinearScrollCard extends LinearScrollCard {

    private FixCard.FixStyle mFixStyle;

    @Override
    public void parseStyle(@Nullable JSONObject data) {
        super.parseStyle(data);
        mFixStyle = new FixCard.FixStyle();
        if (data != null) {
            mFixStyle.parseWith(data);
        }
    }

    @Nullable
    @Override
    public LayoutHelper convertLayoutHelper(LayoutHelper oldHelper) {
        ScrollFixLayoutHelper scrollFixHelper;
        if (oldHelper instanceof ScrollFixLayoutHelper) {
            scrollFixHelper = (ScrollFixLayoutHelper) oldHelper;
        } else {
            scrollFixHelper = new ScrollFixLayoutHelper(0, 0);
        }

        if (mFixStyle != null)
            scrollFixHelper.setAspectRatio(mFixStyle.aspectRatio);

            FixCard.FixStyle fixStyle = mFixStyle;
            scrollFixHelper.setAlignType(fixStyle.alignType);
            scrollFixHelper.setShowType(fixStyle.showType);
            scrollFixHelper.setSketchMeasure(fixStyle.sketchMeasure);
            scrollFixHelper.setX(fixStyle.x);
            scrollFixHelper.setY(fixStyle.y);

        return scrollFixHelper;
    }
}
