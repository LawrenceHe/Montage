package com.zhaodongdb.wireless.home;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.squareup.picasso.Picasso;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.TangramEngine;
import com.tmall.wireless.tangram.util.IInnerImageSetter;
import com.zhaodongdb.wireless.R;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author cginechen
 * @date 2016-10-19
 */

public class MainFragment extends BaseFragment {
    private final static String TAG = MainFragment.class.getSimpleName();

    @BindView(R.id.pager)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    QMUITabSegment mTabSegment;
    private Map<Pager, HomeController> mPages;
    private PagerAdapter mPagerAdapter = new PagerAdapter() {

        private int mChildCount = 0;

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return mPages.size();
        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            HomeController page = mPages.get(Pager.getPagerFromPosition(position));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(page, params);
            return page;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            if (mChildCount == 0) {
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }

        @Override
        public void notifyDataSetChanged() {
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }
    };

    @Override
    protected View onCreateView() {
        FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, layout);
        initTabs();
        initPagers();
        return layout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        for (Map.Entry<Pager, HomeController> entry : mPages.entrySet()) {
            TangramEngine engine = entry.getValue().getEngine();
            if (engine != null) {
                engine.destroy();
            }
        }
    }

    private void initTabs() {
        int normalColor = QMUIResHelper.getAttrColor(getActivity(), R.attr.qmui_config_color_gray_6);
        int selectColor = QMUIResHelper.getAttrColor(getActivity(), R.attr.qmui_config_color_blue);
        mTabSegment.setDefaultNormalColor(normalColor);
        mTabSegment.setDefaultSelectedColor(selectColor);
        mTabSegment.setDefaultTabIconPosition(QMUITabSegment.ICON_POSITION_TOP);

        // 如果你的 icon 显示大小和实际大小不吻合:
        // 1. 设置icon 的 bounds
        // 2. Tab 使用拥有5个参数的构造器
        // 3. 最后一个参数（setIntrinsicSize）设置为false
        int iconShowSize = QMUIDisplayHelper.dp2px(getContext(), 30);
        Drawable normalDrawable, selectDrawable;
        normalDrawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_home_tabbar);
        normalDrawable.setBounds(0, 0, iconShowSize, iconShowSize);
        selectDrawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_home_tabbar_selected);
        selectDrawable.setBounds(0, 0, iconShowSize, iconShowSize);
        QMUITabSegment.Tab home = new QMUITabSegment.Tab(
                normalDrawable,
                selectDrawable,
                getResources().getString(R.string.tab_home), false, false
        );
        normalDrawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_fortune_tabbar);
        normalDrawable.setBounds(0, 0, iconShowSize, iconShowSize);
        selectDrawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_fortune_tabbar_selected);
        selectDrawable.setBounds(0, 0, iconShowSize, iconShowSize);
        QMUITabSegment.Tab fortune = new QMUITabSegment.Tab(
                normalDrawable,
                selectDrawable,
                getResources().getString(R.string.tab_fortune), false, false
        );
        normalDrawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_shopping_tabbar);
        normalDrawable.setBounds(0, 0, iconShowSize, iconShowSize);
        selectDrawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_shopping_tabbar_selected);
        selectDrawable.setBounds(0, 0, iconShowSize, iconShowSize);
        QMUITabSegment.Tab shopping = new QMUITabSegment.Tab(
                normalDrawable,
                selectDrawable,
                getResources().getString(R.string.tab_shopping), false, false
        );
        normalDrawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_loan_tabbar);
        normalDrawable.setBounds(0, 0, iconShowSize, iconShowSize);
        selectDrawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_loan_tabbar_selected);
        selectDrawable.setBounds(0, 0, iconShowSize, iconShowSize);
        QMUITabSegment.Tab loan = new QMUITabSegment.Tab(
                normalDrawable,
                selectDrawable,
                getResources().getString(R.string.tab_loan), false, false
        );
        normalDrawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_mine_tabbar);
        normalDrawable.setBounds(0, 0, iconShowSize, iconShowSize);
        selectDrawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_mine_tabbar_selected);
        selectDrawable.setBounds(0, 0, iconShowSize, iconShowSize);
        QMUITabSegment.Tab mine = new QMUITabSegment.Tab(
                normalDrawable,
                selectDrawable,
                getResources().getString(R.string.tab_mine), false, false
        );

        mTabSegment.addTab(home)
                .addTab(fortune)
                .addTab(shopping)
                .addTab(loan)
                .addTab(mine);
    }

    private void initPagers() {

        final Context appContext = getActivity();

        //Step 1: init tangram
        TangramBuilder.init(appContext, new IInnerImageSetter() {
            @Override
            public <IMAGE extends ImageView> void doLoadImageUrl(@NonNull IMAGE view,
                                                                 @Nullable String url) {
                Picasso.with(appContext).load(url).into(view);
            }
        }, ImageView.class);
        //Step 2: register build=in cells and cards
        TangramBuilder.InnerBuilder montageBuilder = TangramBuilder.newInnerBuilder(appContext);

        HomeController.HomeControlListener listener = new HomeController.HomeControlListener() {
            @Override
            public void startFragment(BaseFragment fragment) {
                MainFragment.this.startFragment(fragment);
            }
        };

        mPages = new HashMap<>();

        HomeController homeController = new MainHomeController(getActivity(), "home", montageBuilder.build());
        homeController.setHomeControlListener(listener);
        mPages.put(Pager.HOME, homeController);

        HomeController fortuneController = new MainFortuneController(getActivity(), "fortune", montageBuilder.build());
        fortuneController.setHomeControlListener(listener);
        mPages.put(Pager.FORTUNE, fortuneController);

        HomeController shoppingController = new MainShoppingController(getActivity(), "mall", montageBuilder.build());
        shoppingController.setHomeControlListener(listener);
        mPages.put(Pager.SHOPPING, shoppingController);

        HomeController loanController = new MainLoanController(getActivity(), "loan", montageBuilder.build());
        loanController.setHomeControlListener(listener);
        mPages.put(Pager.LOAN, loanController);

        HomeController mineController = new MainMineController(getActivity(), "mine", montageBuilder.build());
        mineController.setHomeControlListener(listener);
        mPages.put(Pager.MINE, mineController);

        mViewPager.setAdapter(mPagerAdapter);
        mTabSegment.setupWithViewPager(mViewPager, false);
    }

    enum Pager {
        HOME, FORTUNE, SHOPPING, LOAN, MINE;

        public static Pager getPagerFromPosition(int position) {
            return Pager.values()[position];
        }
    }

    @Override
    protected boolean canDragBack() {
        return false;
    }

    @Override
    public Object onLastFragmentFinish() {
        return null;
    }
}