package com.ennew.ui.act;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ennew.R;
import com.ennew.annation.ViewInject;
import com.ennew.ui.base.BaseActivity;
import com.ennew.utils.ImageLoader;

import java.util.ArrayList;

/**
 * Created by lilong on 16/1/5.
 */
public class PhotoSeeActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.viewpager)
    ViewPager pager = null;

    ArrayList<View> viewContainter = new ArrayList<View>();

    public String TAG = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_see);
        setHeadMid(null,View.VISIBLE,"预览");
        setHeadLeft(this,View.VISIBLE);
        setHeadRight(this,View.INVISIBLE);
        
        ArrayList<String> listphoto =getIntent().getBundleExtra(ACTIVITY_BUNDLE).getStringArrayList("selectlist");
        for (int i = 0; i <listphoto.size() ; i++) {
            ImageView imageView = new ImageView(this);
            ImageLoader.getInstance().display(listphoto.get(i), imageView,
                    getWindow().getWindowManager().getDefaultDisplay().getWidth(), 0);
            viewContainter.add(imageView);
        }
        pager.setAdapter(new PagerAdapter() {

            //viewpager中的组件数量
            @Override
            public int getCount() {
                return viewContainter.size();
            }

            //滑动切换的时候销毁当前的组件
            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                ((ViewPager) container).removeView(viewContainter.get(position));
            }

            //每次滑动的时候生成的组件
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ((ViewPager) container).addView(viewContainter.get(position));
                return viewContainter.get(position);
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return  "";
            }
        });

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
                Log.d(TAG, "--------changed:" + arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                Log.d(TAG, "-------scrolled arg0:" + arg0);
                Log.d(TAG, "-------scrolled arg1:" + arg1);
                Log.d(TAG, "-------scrolled arg2:" + arg2);
            }

            @Override
            public void onPageSelected(int arg0) {
                Log.d(TAG, "------selected:" + arg0);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.comm_left:
            case R.id.comm_right:
                viewContainter.clear();
                finish();
        }

    }
}
