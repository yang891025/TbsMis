package com.tbs.tbsmis.Live.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.tbs.tbsmis.Live.fragment.showLiveAllColumn;
import com.tbs.tbsmis.Live.fragment.showLiveCommon;

/**
 *  版本号：1.0
 *  类描述：
 *  备注消息：
 *  修改时间：2017/2/7 下午4:41
 **/
public class LiveAllCloumnAdapter extends FragmentStatePagerAdapter
{

   // private List<LiveOtherColumn> mliveOtherColumns;
    private  String[] mTiltle;
    private FragmentManager mFragmentManager;

    public LiveAllCloumnAdapter(FragmentManager fm, String[] title)
    {
        super(fm);
        this.mFragmentManager=fm;
        //this.mliveOtherColumns=mliveOtherColumns;
        this.mTiltle=title;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTiltle[position];
    }

    @Override
    public int getCount() {
         return mTiltle.length;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0)
        {
            return showLiveCommon.newInstance();
        }else if(position==1)
        {
             return showLiveAllColumn.newInstance();
        }
        return null;
//        else if(position==mTiltle.length-1)
//        {
//            return LiveSportsColumnFragment.getInstance();
//        }
//        else
//        {
////            其他一级栏目分类
//             return LiveOtherColumnFragment.getInstance(mliveOtherColumns.get(position-2),position-2);
//        }
    }
}
