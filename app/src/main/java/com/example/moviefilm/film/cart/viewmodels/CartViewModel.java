package com.example.moviefilm.film.cart.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.moviefilm.film.cart.repo.CartRepository;
import com.example.moviefilm.film.home.detailFilm.repo.DetailFilmRepo;
import com.example.moviefilm.roomdb.Film;

import java.util.List;

import io.reactivex.Flowable;

public class CartViewModel extends AndroidViewModel{
    private CartRepository cartRepository;
    public CartViewModel(@NonNull Application application) {
        super(application);
        cartRepository = new CartRepository(application);
    }
    //Get all Movie
    public Flowable<List<Film>> getAllMovies(){
        return cartRepository.getAllFilm();
    }

    //Get all Movie
    public Flowable<List<Film>> getFilmCart(int isWantBuy){
        return cartRepository.getFilmCart(isWantBuy);
    }

    //Update Movie
    public void updateFilm(Film film){
        cartRepository.updateCartMovie(film);
    }
}
