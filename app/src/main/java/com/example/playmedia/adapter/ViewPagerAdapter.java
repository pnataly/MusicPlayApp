package com.example.playmedia.adapter;

import android.content.ContentResolver;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.example.playmedia.AllSongFragment;
import com.example.playmedia.FavoritesFragment;


public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ContentResolver contentResolver;
    int tabCount;


    public ViewPagerAdapter(FragmentManager fm, ContentResolver contentResolver, int tabCount)
    {
        super(fm);
        this.contentResolver = contentResolver;
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return AllSongFragment.getInstance(position, contentResolver);

            case 1:
                return FavoritesFragment.getInstance(position);

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

}
