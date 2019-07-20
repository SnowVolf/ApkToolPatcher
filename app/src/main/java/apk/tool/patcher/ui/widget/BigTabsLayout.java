package apk.tool.patcher.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import apk.tool.patcher.R;

/**
 * Реализация аккуратно слизана с SOVA Lite с некоторыми доработками
 */
public class BigTabsLayout extends HorizontalScrollView implements ViewPager.OnAdapterChangeListener, ViewPager.OnPageChangeListener {
    private static final float UNCHECKED_ALPHA = 0.25f;
    private LinearLayout innerLayout = new LinearLayout(getContext());
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private OnCurrentTabClickedListener tabClickedListener;
    private static final String TAG = "BigTabsLayout";

    public BigTabsLayout(Context context) {
        super(context);
        addView(innerLayout);
    }

    public BigTabsLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        addView(innerLayout);
    }

    public BigTabsLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        addView(innerLayout);
    }

    public boolean isHorizontalScrollBarEnabled() {
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public void onPageScrollStateChanged(int i) {
    }

    public void onPageSelected(int i) {
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public BigTabsLayout setTabClickedListener(OnCurrentTabClickedListener onCurrentTabClickedListener) {
        Log.d(TAG, "setTabClickedListener() called with: onCurrentTabClickedListener = [" + onCurrentTabClickedListener + "]");
        tabClickedListener = onCurrentTabClickedListener;
        return this;
    }

    public void setupWithPager(ViewPager viewPager) {
        Log.d(TAG, "setupWithPager() called with: viewPager = [" + viewPager + "]");
        ViewPager viewPager2 = mPager;
        if (viewPager2 != null) {
            viewPager2.removeOnAdapterChangeListener(this);
            mPager.removeOnPageChangeListener(this);
        }
        mPager = viewPager;
        viewPager.addOnAdapterChangeListener(this);
        viewPager.addOnPageChangeListener(this);
        onAdapterChanged(viewPager, null, viewPager.getAdapter());
    }

    private void setupViews() {
        Log.d(TAG, "setupViews() called");
        LayoutInflater from = LayoutInflater.from(getContext());
        innerLayout.removeAllViews();
        for (int i = 0; i < mPagerAdapter.getCount(); ++i) {
            TextView textView = (TextView) from.inflate(R.layout.big_tab, innerLayout, false);
            textView.setText(mPagerAdapter.getPageTitle(i));
            innerLayout.addView(textView);
            if (i > 0) {
                textView.setAlpha(UNCHECKED_ALPHA);
            }
            final int finalI = i;
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    setupViews(finalI);
                }
            });
        }
        updateAlpha(mPager.getCurrentItem(), 0.0f);
        requestLayout();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                BigTabsLayout.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                BigTabsLayout bigTabsLayout = BigTabsLayout.this;
                bigTabsLayout.onPageScrolled(bigTabsLayout.mPager.getCurrentItem(), 0.0f, 0);
            }
        });
    }

    public void setupViews(int i) {
        Log.d(TAG, "setupViews() called with: i = [" + i + "]");
        if (i == mPager.getCurrentItem()) {
            OnCurrentTabClickedListener onCurrentTabClickedListener = tabClickedListener;
            if (onCurrentTabClickedListener != null) {
                onCurrentTabClickedListener.onCurrentTabClicked();
                return;
            }
            return;
        }
        mPager.setCurrentItem(i);
    }

    public void invalidateTabs() {
        Log.d(TAG, "invalidateTabs() called");
        setupViews();
    }

    @Override
    protected void onMeasure(int i, int i2) {
        Log.d(TAG, "onMeasure() called with: i = [" + i + "], i2 = [" + i2 + "]");
        super.onMeasure(i, i2);
        PagerAdapter pagerAdapter = mPagerAdapter;
        if (pagerAdapter != null && pagerAdapter.getCount() != 0) {
            innerLayout.getChildAt(mPagerAdapter.getCount() - 1)
                    .setPaddingRelative(getResources().getDimensionPixelSize(R.dimen.big_tab_padding_start),
                            0, MeasureSpec.getSize(i), 0);
        }
    }

    private void updateAlpha(int i, float f) {
        Log.d(TAG, "updateAlpha() called with: i = [" + i + "], f = [" + f + "]");
        View childAt = innerLayout.getChildAt(i);
        int i2 = i + 1;
        View childAt2 = innerLayout.getChildAt(i2);
        childAt.setAlpha(((1.0f - f) * 0.75f) + UNCHECKED_ALPHA);
        if (childAt2 != null) {
            childAt2.setAlpha((f * 0.75f) + UNCHECKED_ALPHA);
        }
        int i3 = 0;
        while (i3 < innerLayout.getChildCount()) {
            if (!(i3 == i || i3 == i2)) {
                innerLayout.getChildAt(i3).setAlpha(UNCHECKED_ALPHA);
            }
            i3++;
        }
    }

    public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter pagerAdapter, @Nullable PagerAdapter pagerAdapter2) {
        Log.d(TAG, "onAdapterChanged() called with: viewPager = [" + viewPager + "], pagerAdapter = [" + pagerAdapter + "], pagerAdapter2 = [" + pagerAdapter2 + "]");
        if (pagerAdapter2 != null) {
            mPagerAdapter = pagerAdapter2;
            setupViews();
        }
    }

    public void onPageScrolled(int i, float f, int i2) {
        Log.d(TAG, "onPageScrolled() called with: i = [" + i + "], f = [" + f + "], i2 = [" + i2 + "]");
        updateAlpha(i, f);
        i2 = innerLayout.getChildAt(i).getLeft();
        View childAt = innerLayout.getChildAt(i + 1);
        scrollTo((int) (((float) i2) + (((float) ((childAt != null ? childAt.getLeft() : 0) - i2)) * f)), 0);
    }

    public interface OnCurrentTabClickedListener {
        void onCurrentTabClicked();
    }
}