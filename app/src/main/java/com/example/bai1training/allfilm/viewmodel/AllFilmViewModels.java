package com.example.bai1training.allfilm.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.bai1training.allfilm.repo.AllFilmRepo;
import com.example.bai1training.film.models.MovieAdver;
import com.example.bai1training.film.models.ResultRespone;

import java.util.List;

public class AllFilmViewModels extends AndroidViewModel {
    private MutableLiveData<ResultRespone> mNowPlayingMutableLiveData;

    private MutableLiveData<ResultRespone> mPopularMutableLiveData;
    private MutableLiveData<ResultRespone> mTopRateMutableLiveData;
    private MutableLiveData<ResultRespone> mUpcomingMutableLiveData;
    private MutableLiveData<List<MovieAdver>> movieAdverMutableLiveData;
    private MutableLiveData<ResultRespone> similarFilmLiveData;
    private MutableLiveData<ResultRespone> recommendFilmLiveData;

    private AllFilmRepo filmRepository;

    public AllFilmViewModels(Application application) {
        super(application);
        filmRepository = new AllFilmRepo(application);
    }

    public MutableLiveData<ResultRespone> getmNowPlayingMutableLiveData() {
        if (mNowPlayingMutableLiveData == null)
            return mNowPlayingMutableLiveData = filmRepository.getmNowPlayingLiveData();
        return mNowPlayingMutableLiveData;
    }

    public MutableLiveData<ResultRespone> getmPopularMutableLiveData() {
        if (mPopularMutableLiveData == null)
            return mPopularMutableLiveData = filmRepository.getmPopularLiveData();
        return mPopularMutableLiveData;
    }

    public MutableLiveData<ResultRespone> getmTopRateMutableLiveData() {
        if (mTopRateMutableLiveData == null)
            return mTopRateMutableLiveData = filmRepository.getmTopRateLiveData();
        return mTopRateMutableLiveData;
    }

    public MutableLiveData<ResultRespone> getmUpcomingMutableLiveData() {
        if (mUpcomingMutableLiveData == null)
            return mUpcomingMutableLiveData = filmRepository.getmUpcomingLiveData();
        return mUpcomingMutableLiveData;
    }

    public MutableLiveData<ResultRespone> getSimilarFilmLiveData() {
        if (similarFilmLiveData == null)
            return similarFilmLiveData = filmRepository.getSimilarFilmMutableLiveData();
        return similarFilmLiveData;
    }

    public MutableLiveData<ResultRespone> getRecommendFilmLiveData() {
        if (recommendFilmLiveData == null)
            return recommendFilmLiveData = filmRepository.getRecommendMutableLiveData();
        return recommendFilmLiveData;
    }

    public void fetchPopularMovies(String apiKey , int page) {
        filmRepository.fetchPopularMovies(apiKey,page);
    }

    public void fetchTopRateMovies(String apiKey ,int page) {
        filmRepository.fetchTopRateMovies(apiKey,page);
    }

    public void fetchNowPlayingMovies(String apiKey ,int page) {
        filmRepository.fetchNowPalyingMovies(apiKey,page);
    }

    public void fetchSimilarFilm(String id , String apiKey) {
        filmRepository.fetchSimilarFilm(id,apiKey);
    }
    public void fetchRecommendFilm(String id , String apiKey) {
        filmRepository.fetchRecommendFilm(id,apiKey);
    }

    public void fetchUpcomingMovies(String apiKey ,int page) { filmRepository.fetchUpcomingMovies(apiKey,page); }

}