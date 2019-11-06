package com.zhaodongdb.wireless.fragment;

import com.qmuiteam.qmui.arch.QMUIFragment;
import com.zhaodongdb.wireless.fragment.home.MainFragment;

/**
 * Created by cgspine on 2018/1/7.
 */

public abstract class BaseFragment extends QMUIFragment {


    public BaseFragment() {
    }

//    @Override
//    protected int backViewInitOffset(Context context, int dragDirection, int moveEdge) {
//        if (moveEdge == SwipeBackLayout.EDGE_TOP || moveEdge == SwipeBackLayout.EDGE_BOTTOM) {
//            return 0;
//        }
//        return QMUIDisplayHelper.dp2px(context, 100);
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        QDUpgradeManager.getInstance(getContext()).runUpgradeTipTaskIfExist(getActivity());
//
//    }

    @Override
    public Object onLastFragmentFinish() {
        return new MainFragment();

    }

//    protected void goToWebExplorer(@NonNull String url, @Nullable String title) {
//        Intent intent = QDMainActivity.createWebExplorerIntent(getContext(), url, title);
//        startActivity(intent);
//    }

//    protected void injectDocToTopBar(QMUITopBar topBar) {
//        final QDItemDescription description = QDDataManager.getInstance().getDescription(this.getClass());
//        if (description != null) {
//            topBar.addRightTextButton("DOC", QMUIViewHelper.generateViewId())
//                    .setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            goToWebExplorer(description.getDocUrl(), description.getName());
//                        }
//                    });
//        }
//    }
//
//    protected void injectDocToTopBar(QMUITopBarLayout topBar) {
//        final QDItemDescription description = QDDataManager.getInstance().getDescription(this.getClass());
//        if (description != null) {
//            topBar.addRightTextButton("DOC", QMUIViewHelper.generateViewId())
//                    .setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            goToWebExplorer(description.getDocUrl(), description.getName());
//                        }
//                    });
//        }
//    }
}
