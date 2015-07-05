package cn.bingoogolapple.weibo.ui.pupupwindow;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckedTextView;

import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.weibo.R;
import cn.bingoogolapple.weibo.model.HomeCategory;
import cn.bingoogolapple.weibo.util.ToastUtils;
import cn.bingoogolapple.weibo.util.UIUtils;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/7/5 上午9:42
 * 描述:
 */
public class HomeCategoryPopupWindow extends BasePopupWindow {
    private RecyclerView mCategoryRv;
    private CategoryAdapter mCategoryAdapter;
    private HomeCategoryPopupWindowDelegate mDelegate;
    List<HomeCategory> mHomeCategories;

    public HomeCategoryPopupWindow(Activity activity, View anchorView) {
        super(activity, R.layout.popwindow_home_category, anchorView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void findView() {
        mCategoryRv = getViewById(R.id.rv_home_category_data);
    }

    @Override
    protected void setListener() {
        getViewById(R.id.btn_home_category_edit).setOnClickListener(this);
        mCategoryAdapter = new CategoryAdapter(mActivity);
        mCategoryAdapter.setOnItemChildClickListener(new BGAOnItemChildClickListener() {
            @Override
            public void onItemChildClick(View view, int position) {
                if (mDelegate != null && view.getId() == R.id.ctv_item_home_category) {
                    HomeCategory homeCategory = mCategoryAdapter.getItem(position);
                    for (HomeCategory category : mHomeCategories) {
                        category.selected = false;
                    }
                    homeCategory.selected = true;
                    homeCategory.hasNewStatus = false;
                    mDelegate.onSelectCategory(homeCategory);
                }
                dismiss();
            }
        });
    }

    @Override
    protected void processLogic() {
        mCategoryRv.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mCategoryRv.setAdapter(mCategoryAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_home_category_edit) {
            ToastUtils.show("点击了编辑分组");
            dismiss();
        }
    }

    public void setCategorys(List<HomeCategory> homeCategories) {
        mHomeCategories = homeCategories;
        mCategoryAdapter.setDatas(mHomeCategories);
    }

    @Override
    public void show() {
        setWidth(UIUtils.getHalfScreenWidth(mActivity));
        setHeight(UIUtils.getHalfScreenHeight(mActivity));

        showAsDropDown(mAnchorView, (mAnchorView.getWidth() - getWidth()) / 2, -UIUtils.dp2px(mActivity, 12));
    }

    public void setDelegate(HomeCategoryPopupWindowDelegate delegate) {
        mDelegate = delegate;
    }

    private static class CategoryAdapter extends BGARecyclerViewAdapter<HomeCategory> {

        public CategoryAdapter(Context context) {
            super(context, R.layout.item_home_category);
        }

        @Override
        protected void setItemChildListener(BGAViewHolderHelper viewHolderHelper) {
            viewHolderHelper.setItemChildClickListener(R.id.ctv_item_home_category);
        }

        @Override
        protected void fillData(BGAViewHolderHelper viewHolderHelper, int position, HomeCategory homeCategory) {
            if (TextUtils.isEmpty(homeCategory.header)) {
                viewHolderHelper.setVisibility(R.id.ll_item_home_header, View.GONE);
            } else {
                viewHolderHelper.setVisibility(R.id.ll_item_home_header, View.VISIBLE);
                viewHolderHelper.setText(R.id.tv_item_home_header, homeCategory.header);
            }
            CheckedTextView categoryCtv = viewHolderHelper.getView(R.id.ctv_item_home_category);
            if (homeCategory.selected) {
                categoryCtv.setChecked(true);
            } else {
                categoryCtv.setChecked(false);
            }
            if (homeCategory.hasNewStatus) {
                categoryCtv.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.shape_orange_circle_small), null);
            } else {
                categoryCtv.setCompoundDrawables(null, null, null, null);
            }
            categoryCtv.setText(homeCategory.title);
        }
    }

    public interface HomeCategoryPopupWindowDelegate {
        void onSelectCategory(HomeCategory homeCategory);
    }
}