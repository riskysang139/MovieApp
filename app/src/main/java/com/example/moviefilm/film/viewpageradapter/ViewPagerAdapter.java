package com.example.moviefilm.film.viewpageradapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.moviefilm.film.cart.view.CartFragment;
import com.example.moviefilm.film.home.view.HomeFragment;
import com.example.moviefilm.film.user.view.UserFragment;
import com.example.moviefilm.film.watchfilmlocal.view.WatchFilmLocalFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return CartFragment.getInstance();
            case 2:
                return WatchFilmLocalFragment.getInstance();
            case 3:
                return UserFragment.getInstance();
            case 0:
            default:
                return HomeFragment.getInstance();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
