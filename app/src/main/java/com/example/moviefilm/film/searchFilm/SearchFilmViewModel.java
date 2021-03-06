package com.example.moviefilm.film.searchFilm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.moviefilm.film.models.ResultResponse;

public class SearchFilmViewModel extends AndroidViewModel {
    private MutableLiveData<ResultResponse> resultResponeLiveData ;
    private SearchFilmRepo searchFilmRepo;

    public SearchFilmViewModel(@NonNull Application application) {
        super(application);
        searchFilmRepo=new SearchFilmRepo(application);
    }

    public MutableLiveData<ResultResponse> getResultResponeLiveData() {
        if(resultResponeLiveData ==null)
            return resultResponeLiveData=searchFilmRepo.getSearchFilmMutableLiveData();
        return resultResponeLiveData;
    }

    public void fetchSearchResponse(String apiKey, String query){
        searchFilmRepo.fetchFilmRepo(apiKey,query);
    }

}
