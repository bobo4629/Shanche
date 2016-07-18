package bobo.shanche.myAdapter;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import bobo.shanche.TabFragment1;
import bobo.shanche.TabFragment2;

/**
 * Created by bobo1 on 2016/7/7.
 */
public class PagerAdapter extends FragmentPagerAdapter{


    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                TabFragment1 tab1 = new TabFragment1();
                return tab1;
            case 1:
                TabFragment2 tab2 = new TabFragment2();
                return tab2;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    //需要用此方法 同时添加title 若用tablayout.add(tablayout.newtab("title"))添加的话会多出空标题
    //除非使用newtab("",0)
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "收藏";
            case 1:
                return "记录";
        }
        return null;
    }
}
