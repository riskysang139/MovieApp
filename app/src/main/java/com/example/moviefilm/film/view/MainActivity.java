package com.example.moviefilm.film.view;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.example.moviefilm.R;
import com.example.moviefilm.base.customview.SearchActionBarView;
import com.example.moviefilm.databinding.ActivityMainBinding;
import com.example.moviefilm.film.viewpageradapter.ViewPagerAdapter;
import com.example.moviefilm.film.detailFilm.view.DetailFilmActivity;
import com.example.moviefilm.film.searchFilm.SearchFilmViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    BottomNavigationView bottomNavigationView;
    ViewPager2 viewPager;
    ViewPagerAdapter adapter;
    public static final String API_KEY = "42ccbde96fdbec040787f337977a26da";
    public static final String HEADER_URL_IMAGE = "https://image.tmdb.org/t/p/w500";
    SearchActionBarView searchActionBarView;
    public static String keySearch = "";
    SearchFilmViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewModel = ViewModelProviders.of(this).get(SearchFilmViewModel.class);
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        initView();
        setUpViewpager();
        initData();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                });
    }

    private void initView() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.viewPager);

//        searchActionBarView = findViewById(R.id.sb_search_all);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String id = bundle.getString(DetailFilmActivity.KEY_FROM, "");
            if (id.equals(DetailFilmActivity.FROM_DETAIL) || id.equals(DetailFilmActivity.FROM_CART)) {
                viewPager.setCurrentItem(1);
                Bundle bundleFragment = new Bundle();
                bundleFragment.putString(DetailFilmActivity.KEY_FROM, DetailFilmActivity.FROM_DETAIL);
            } else
                Toast.makeText(getBaseContext(), "Error !!!", Toast.LENGTH_LONG).show();
        }
    }

    private void setUpViewpager() {
        adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_cart).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_film_offline).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_user).setChecked(true);
                        break;
                }
            }
        });
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.navigation_cart:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.navigation_film_offline:
                    viewPager.setCurrentItem(2);
                    break;
                case R.id.navigation_user:
                    viewPager.setCurrentItem(3);
                    break;
            }
            return true;
        });
        viewPager.setOffscreenPageLimit(4);
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


}