package com.zhaodongdb.wireless;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.zhaodongdb.common.utils.DeviceUtil;


public class FloatDebugView extends FrameLayout {

    private OnOpenListener onOpenListener;
    private ImageView imageView;

    public FloatDebugView(final Context context, int resId) {
        super(context);
        addImageView(resId);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addImageView(int resId) {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView = new ImageView(getContext());
        imageView.setImageResource(resId);
        int width = DeviceUtil.getPixelFromDip(40);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, width);
        params.leftMargin = DeviceUtil.getWindowWidth() - DeviceUtil.getPixelFromDip(50);
        params.topMargin = DeviceUtil.getWindowHeight() - DeviceUtil.getPixelFromDip(240);
        imageView.setLayoutParams(params);
        addView(imageView);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            private float x, y;
            private float org_x, org_y;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) imageView.getLayoutParams();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // 捕获手指触摸按下动作
                        // 获取相对View的坐标，即以此View左上角为原点
                        x = event.getX();
                        y = event.getY();
                        org_x = event.getRawX();
                        org_y = event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE: // 捕获手指触摸移动动作
                        params.leftMargin = (int) (event.getRawX() - x);
                        params.leftMargin = params.leftMargin < 0 ? 0 : params.leftMargin;
                        int maxLeftMargin = DeviceUtil.getWindowWidth() - imageView.getWidth();
                        params.leftMargin = params.leftMargin > maxLeftMargin ? maxLeftMargin : params.leftMargin;

                        params.topMargin = (int) (event.getRawY() - DeviceUtil.getStatusBarHeight(getContext()) - y);
                        params.topMargin = params.topMargin < 0 ? 0 : params.topMargin;
                        int maxTopMargin = DeviceUtil.getWindowHeight() - imageView.getHeight() - DeviceUtil.getStatusBarHeight(getContext());
                        params.topMargin = params.topMargin > maxTopMargin ? maxTopMargin : params.topMargin;
                        imageView.requestLayout();
                        break;

                    case MotionEvent.ACTION_UP: // 捕获手指触摸离开动作
                        int distance = DeviceUtil.getPixelFromDip(5);
                        if (Math.abs(event.getRawX() - org_x) < distance && Math.abs(event.getRawY() - org_y) < distance) {
                            if (onOpenListener != null) {
                                onOpenListener.onOpen();
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    public void setImageBounds(int width, int height) {
        FrameLayout.LayoutParams params = (LayoutParams) imageView.getLayoutParams();
        params.height = height;
        params.width = width;
        imageView.setLayoutParams(params);
    }

    public void setImageMargin(int top, int left) {
        FrameLayout.LayoutParams params = (LayoutParams) imageView.getLayoutParams();
        params.leftMargin = left;
        params.topMargin = top;
        imageView.setLayoutParams(params);
    }

    public void setOnOpenListener(OnOpenListener onOpenListener) {
        this.onOpenListener = onOpenListener;
    }

    public interface OnOpenListener{
        void onOpen();
    }

}
