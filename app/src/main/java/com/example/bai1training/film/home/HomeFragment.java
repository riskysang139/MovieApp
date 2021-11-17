package com.example.bai1training.film.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bai1training.R;
import com.example.bai1training.allfilm.AllFilmActivity;
import com.example.bai1training.base.HorizontalItemDecoration;
import com.example.bai1training.base.OnClickListener;
import com.example.bai1training.base.OnClickListener2;
import com.example.bai1training.databinding.FragmentHomeBinding;
import com.example.bai1training.detailFilm.DetailFilmActivity;
import com.example.bai1training.film.MainActivity;
import com.example.bai1training.film.adapter.FilmAdapter;
import com.example.bai1training.film.adapter.FilmAdapter2;
import com.example.bai1training.film.adapter.MovieAdverAdapter;
import com.example.bai1training.film.models.MovieAdver;
import com.example.bai1training.film.models.Results;
import com.example.bai1training.film.viewmodels.FilmViewModels;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OnClickListener, OnClickListener2 {
    private FragmentHomeBinding binding;
    private FilmViewModels filmViewModels;
    private List<Results> popularMoviesList, topRateMovieList, nowPlayingMovieList, upComingMovieList;
    private List<MovieAdver> movieAdverList;
    private RecyclerView rcvPopular, rcvTopRate, rcvNowPlaying, rcvUpComing;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FilmAdapter filmAdapter;
    private MovieAdverAdapter movieAdverAdapter;
    private FilmAdapter2 filmAdapter2;
    private ViewPager2 viewPager2;
    private Handler handler = new Handler();
    private Button btnPopular, btnTopRated, btnUpComing;
    private static final String TAG = "Tag";

    public static HomeFragment getInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = binding.getRoot();
        filmViewModels = ViewModelProviders.of(this).get(FilmViewModels.class);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        observerPopularFilm();
        observerTopRateFilm();
        observerNowPlayingFilm();
        observerUpComingFilm();
        obserVerMovieAdver();
        onRefresh();
        onClickAllFilm();
    }


    private void initView(View view) {
        viewPager2 = view.findViewById(R.id.view_adver);
        rcvPopular = view.findViewById(R.id.rcv_popular);
        rcvTopRate = view.findViewById(R.id.rcv_top_rate);
        rcvNowPlaying = view.findViewById(R.id.rcv_now_playing);
        rcvUpComing = view.findViewById(R.id.rcv_up_coming);
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        popularMoviesList = new ArrayList<>();
        topRateMovieList = new ArrayList<>();
        upComingMovieList = new ArrayList<>();
        nowPlayingMovieList = new ArrayList<>();
        movieAdverList = new ArrayList<>();
        btnPopular = view.findViewById(R.id.btn_more_popular);
        btnTopRated = view.findViewById(R.id.btn_top_rated);
        btnUpComing = view.findViewById(R.id.btn_up_coming);
    }

    private void observerPopularFilm() {
        filmViewModels.fetchPopularMovies(MainActivity.API_KEY, 1);
        filmViewModels.getmPopularMutableLiveData().observe(getActivity(), resultRespone -> {
                    if (resultRespone != null) {
                        popularMoviesList = resultRespone.getResults();
                        initRecyclerPopular();
                        Log.e(TAG, "result respone : " + popularMoviesList.toString());
                    } else
                        Log.e(TAG, "call api failure");
                }
        );
    }

    private void observerTopRateFilm() {
        filmViewModels.fetchTopRateMovies(MainActivity.API_KEY, 1);
        filmViewModels.getmTopRateMutableLiveData().observe(getActivity(), resultRespone -> {
                    if (resultRespone != null) {
                        topRateMovieList = resultRespone.getResults();
                        initRecyclerTopRate();
                        Log.e(TAG, "result respone : " + topRateMovieList.toString());
                    } else
                        Log.e(TAG, "call api failure");
                }
        );
    }

    private void observerNowPlayingFilm() {
        filmViewModels.fetchNowPlayingMovies(MainActivity.API_KEY, 1);
        filmViewModels.getmNowPlayingMutableLiveData().observe(getActivity(), resultRespone -> {
                    if (resultRespone != null) {
                        nowPlayingMovieList = resultRespone.getResults();
                        initRecyclerNowPlaying();
                        Log.e(TAG, "result respone : " + nowPlayingMovieList.toString());
                    } else
                        Log.e(TAG, "call api failure");
                }
        );
    }

    private void observerUpComingFilm() {
        filmViewModels.fetchUpcomingMovies(MainActivity.API_KEY, 3);
        filmViewModels.getmUpcomingMutableLiveData().observe(getActivity(), resultRespone -> {
                    if (resultRespone != null) {
                        upComingMovieList = resultRespone.getResults();
                        initRecyclerUpComing();
                        Log.e(TAG, "result respone : " + upComingMovieList.toString());
                    } else
                        Log.e(TAG, "call api failure");
                }
        );
    }

    private void obserVerMovieAdver() {
        filmViewModels.fetchMovieAdver();
        filmViewModels.getMovieAdverMutableLiveData().observe(getActivity(), new Observer<List<MovieAdver>>() {
            @Override
            public void onChanged(List<MovieAdver> movieAdvers) {
                if (movieAdvers != null) {
                    movieAdverList = movieAdvers;
                    initViewAdver();
                }
            }
        });
    }

    private void initRecyclerPopular() {
        filmAdapter = new FilmAdapter(popularMoviesList, getActivity(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvPopular.addItemDecoration(new HorizontalItemDecoration(com.viettel.vtecommerce.utils.Converter.dpToPx(requireContext(), 15)));
        rcvPopular.setItemAnimator(new DefaultItemAnimator());
        rcvPopular.setLayoutManager(layoutManager);
        rcvPopular.setAdapter(filmAdapter);
    }

    private void initRecyclerTopRate() {
        filmAdapter2 = new FilmAdapter2(topRateMovieList, getActivity(), this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, RecyclerView.HORIZONTAL, false);
        rcvTopRate.setLayoutManager(gridLayoutManager);
        rcvTopRate.setAdapter(filmAdapter2);
    }

    private void initRecyclerNowPlaying() {
        filmAdapter = new FilmAdapter(nowPlayingMovieList, getActivity(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvNowPlaying.setLayoutManager(layoutManager);
        rcvNowPlaying.setAdapter(filmAdapter);
    }

    private void initRecyclerUpComing() {
        filmAdapter = new FilmAdapter(upComingMovieList, getActivity(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvUpComing.setLayoutManager(layoutManager);
        rcvUpComing.addItemDecoration(new HorizontalItemDecoration(com.viettel.vtecommerce.utils.Converter.dpToPx(requireContext(), 15)));
        rcvUpComing.setItemAnimator(new DefaultItemAnimator());
        rcvUpComing.setAdapter(filmAdapter);
    }

    private void initViewAdver() {
        movieAdverAdapter = new MovieAdverAdapter(movieAdverList, viewPager2);
        viewPager2.setAdapter(movieAdverAdapter);
        onAutoScrollAD();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    private void onAutoScrollAD() {
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(20));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.8f + r * 0.2f);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 3000);
            }
        });
    }

    public void onRefresh() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        filmViewModels.fetchPopularMovies(MainActivity.API_KEY, 1);
                        filmViewModels.fetchTopRateMovies(MainActivity.API_KEY, 1);
                        filmViewModels.fetchNowPlayingMovies(MainActivity.API_KEY, 1);
                        filmViewModels.fetchUpcomingMovies(MainActivity.API_KEY, 1);
                        filmViewModels.fetchMovieAdver();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

    }

    @Override
    public void onClickNowDetailFilm(Results resultFilm, int position) {
        Intent intent = new Intent(getActivity(), DetailFilmActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(DetailFilmActivity.ID, resultFilm.getId() + "");
        bundle.putString(DetailFilmActivity.KEY_FROM, DetailFilmActivity.FROM_POPULAR);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }

    @Override
    public void onClickNowDetailFilm2(Results resultFilm, int position) {
        Intent intent = new Intent(getActivity(), DetailFilmActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(DetailFilmActivity.ID, resultFilm.getId() + "");
        bundle.putString(DetailFilmActivity.KEY_FROM, DetailFilmActivity.FROM_POPULAR);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }

    private void onClickAllFilm() {
        btnUpComing.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AllFilmActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(DetailFilmActivity.KEY_FROM, DetailFilmActivity.FROM_UP_COMING);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
        });
        btnPopular.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AllFilmActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(DetailFilmActivity.KEY_FROM, DetailFilmActivity.FROM_POPULAR);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
        });
        btnTopRated.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AllFilmActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(DetailFilmActivity.KEY_FROM, DetailFilmActivity.FROM_TOP_RATE);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
        });

    }
}
