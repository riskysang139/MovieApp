package com.example.moviefilm.film.home.detailFilm.view;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.moviefilm.R;
import com.example.moviefilm.base.Converter;
import com.example.moviefilm.base.HorizontalItemDecoration;
import com.example.moviefilm.base.LoadingDialog;
import com.example.moviefilm.base.OnClickListener;
import com.example.moviefilm.base.customview.CircleAnimationUtil;
import com.example.moviefilm.databinding.ActivityDetailFilmBinding;
import com.example.moviefilm.film.home.adapter.FilmAdapter;
import com.example.moviefilm.film.home.allfilm.view.AllFilmActivity;
import com.example.moviefilm.film.home.detailFilm.adapter.CastAdapter;
import com.example.moviefilm.film.home.detailFilm.models.Cast;
import com.example.moviefilm.film.home.detailFilm.models.DetailFilm;
import com.example.moviefilm.film.home.detailFilm.viewmodel.DetailFilmViewModels;
import com.example.moviefilm.film.home.detailFilm.watchfilm.view.WatchFilmActivity;
import com.example.moviefilm.film.models.Results;
import com.example.moviefilm.film.view.MainActivity;
import com.example.moviefilm.roomdb.cartdb.Cart;
import com.example.moviefilm.roomdb.filmdb.Film;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DetailFilmActivity extends AppCompatActivity implements OnClickListener, View.OnClickListener {
    private static final String TAG = "TAG";
    ActivityDetailFilmBinding binding;
    public static final String KEY_FROM = "_from_screen";
    public static final String FROM_UP_COMING = "FROM_UP_COMING";
    public static final String FROM_TOP_RATE = "FROM_TOP_RATE";
    public static final String FROM_POPULAR = "FROM_POPULAR";
    public static final String FROM_SEARCH = "FROM_SEARCH";
    public static final String FROM_DETAIL = "FROM_DETAIL";
    public static final String FROM_SIMILAR = "FROM_SIMILAR";
    public static final String FROM_RECOMMEND = "FROM_RECOMMEND";
    public static final String FROM_VIDEO_HISTORY = "FROM_VIDEO_HISTORY";
    public static final String FROM_LOVED = "FROM_LOVED";
    public static final String FROM_CART = "FROM_CART";

    public static final String ID = "ID_VIDEO";

    private static String id = "";

    public static final String VIDEO_ID = "VIDEO_ID";
    private static final String VIDEO_LINK = "https://www.youtube.com/watch?v=";

    private RelativeLayout btnBack;
    private TextView txtTitle, txtAdult, txtGenres, txtTimeFilm, txtRelease;
    private TextView txtDetail;
    private RatingBar txtRated;
    private ImageView imgFilm;
    private DetailFilmViewModels detailFilmViewModels;
    private DetailFilm detailFilms;
    private List<Results> listReFilm, listSimFilm = new ArrayList<>();
    private FilmAdapter recFilmAdapter, simFilmAdapter;
    private LoadingDialog loadingDialog;
    private Button btnSimilar, btnRecommend;
    private List<Cast> castList = new ArrayList<>();
    private CastAdapter castAdapter;
    private Film filmDB;
    private Cart cartFilm;
    private FirebaseAuth firebaseAuth;
    private List<Cart> cartFilmList;
    private String videoResponseStr = "";
    private boolean seeMore = true;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_film);
        detailFilmViewModels = ViewModelProviders.of(this).get(DetailFilmViewModels.class);
        firebaseAuth = FirebaseAuth.getInstance();
        initView();
        getData();
        observerFilm();
        observerFilmCart();
        observerDetailFilm();
        observerRecommendFilm();
        observeSimilarFilm();
        observerCastFilm();
        observerListFilmCart();
        observerVideoTrailerFilm();
        onComeback();
        setUpReFilmAdapter();
        setUpSimFilmAdapter();
        setUpCastAdapter();
        actionMoreFilm();
        openCartFilm();
        refreshLayout();
    }

    private void getData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            id = bundle.getString(ID, "1");
    }

    private void initView() {
        btnBack = findViewById(R.id.btn_back);
        txtDetail = findViewById(R.id.detail_film);
        txtTitle = findViewById(R.id.title_film);
        txtAdult = findViewById(R.id.tv_adult);
        txtGenres = findViewById(R.id.tv_genres);
        txtRated = findViewById(R.id.tv_rated);
        txtTimeFilm = findViewById(R.id.time_film);
        txtRelease = findViewById(R.id.year_release);
        imgFilm = findViewById(R.id.video_view_click);
        loadingDialog = findViewById(R.id.progress_loading);
        btnRecommend = findViewById(R.id.btn_more_recommend);
        btnSimilar = findViewById(R.id.btn_more_similar);
        binding.detailFilm.setOnClickListener(this::onClick);
        binding.txtExpanded.setOnClickListener(this::onClick);
    }

    private void onComeback() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void observerDetailFilm() {
        detailFilmViewModels.fetchDetailFilm(id, MainActivity.API_KEY);
        detailFilmViewModels.getDetailFilmLiveData().observe(this, detailFilm -> {
            if (detailFilm == null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        detailFilmViewModels.fetchDetailFilm(id, MainActivity.API_KEY);
                    }
                },1000);
            } else {
                detailFilms = detailFilm;
                setUpViewDetail();
                insertFilm(detailFilm);
                insertFilmToCart(detailFilm);
            }
        });
    }

    public void observerVideoTrailerFilm() {
        detailFilmViewModels.fetchVideoTrailerFilm(id, MainActivity.API_KEY);
        detailFilmViewModels.getVideoFilmLiveData().observe(DetailFilmActivity.this, videoResponse -> {
            if (videoResponse != null && videoResponse.getResults().size() > 0) {
                watchFilm(videoResponse.getResults().get(0).getKey());
                downloadVideo(VIDEO_LINK + videoResponse.getResults().get(0).getKey());
            } else {
                Toast.makeText(getBaseContext(),"The movies is will be updated soon", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void observeSimilarFilm() {
        detailFilmViewModels.fetchSimilarFilm(id, MainActivity.API_KEY);
        detailFilmViewModels.getSimilarFilmLiveData().observe(this, resultResponse -> {
            if (resultResponse.getResults().size() > 0) {
                binding.sectionSimilar.setVisibility(View.VISIBLE);
                if (simFilmAdapter != null)
                    simFilmAdapter.setResultsList(resultResponse.getResults());
            } else
                binding.sectionSimilar.setVisibility(View.GONE);
        });
    }

    public void observerRecommendFilm() {
        detailFilmViewModels.fetchRecommendFilm(id, MainActivity.API_KEY);
        detailFilmViewModels.getRecommendFilmLiveData().observe(this, resultResponse -> {
            if (resultResponse.getResults().size() > 0) {
                binding.sectionRecommends.setVisibility(View.VISIBLE);
                if (recFilmAdapter != null)
                    recFilmAdapter.setResultsList(resultResponse.getResults());
            } else {
                binding.sectionRecommends.setVisibility(View.GONE);
            }
        });
    }


    public void observerFilm() {
        Disposable disposable = detailFilmViewModels.getMovieWithId(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(films -> {
                    Log.d(TAG, "accept: getMovie");
                    filmDB = films;
                    if (films != null) {
                        updateFilmLove(films);
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void observerFilmCart() {
        Disposable disposable = detailFilmViewModels.getMovieCart(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cart -> {
                    cartFilm = cart;
                });
        compositeDisposable.add(disposable);
    }

    public void observerListFilmCart() {
        Disposable disposable = detailFilmViewModels.getListMovieCart().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cart -> {
                    cartFilmList = cart;
                });
        compositeDisposable.add(disposable);
    }

    public void observerCastFilm() {
        detailFilmViewModels.fetchCastFilm(id, MainActivity.API_KEY);
        detailFilmViewModels.getCastResponseMutableLiveData().observe(this, resultResponse -> {
            if (resultResponse.getCast().size() > 0) {
                binding.sectionCast.setVisibility(View.VISIBLE);
                if (castAdapter != null)
                    castAdapter.setCastList(resultResponse.getCast());
            } else {
                binding.sectionCast.setVisibility(View.GONE);
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void setUpViewDetail() {
        if (detailFilms != null) {
            binding.layoutMain.setVisibility(View.VISIBLE);
            binding.rlDownload.setVisibility(View.VISIBLE);
            binding.rlLove.setVisibility(View.VISIBLE);
            binding.rlCart.setVisibility(View.VISIBLE);
            binding.progressLoading.setVisibility(View.GONE);
            txtTitle.setText(detailFilms.getTitle());
            txtDetail.setText(detailFilms.getOverview());
            Glide.with(this).load(MainActivity.HEADER_URL_IMAGE + detailFilms.getPosterPath()).into(imgFilm);
            txtRated.setRating(Float.parseFloat(detailFilms.getVoteAverage() / 2 + ""));
            if (detailFilms.getGenres().size() == 0 || detailFilms.getGenres().isEmpty())
                txtGenres.setText("•    No Data    •");
            else if (detailFilms.getGenres().size() == 1)
                txtGenres.setText("•    " + detailFilms.getGenres().get(0).getName() + "    •");
            else
                txtGenres.setText("•    " + detailFilms.getGenres().get(0).getName() + ", " + detailFilms.getGenres().get(1).getName() + "    •");
            txtTimeFilm.setText(detailFilms.getRuntime() + " mins");
            if (detailFilms.getAdult())
                txtAdult.setVisibility(View.VISIBLE);
            else
                txtAdult.setVisibility(View.GONE);
            txtRelease.setText(Converter.convertDate(detailFilms.getReleaseDate()));
            binding.txtPrice.setText("Add to cart : " + detailFilms.getVoteAverage() * 2 + " $");
            if (txtDetail.getLayout().getLineCount() <= 5) {
                binding.txtExpanded.setVisibility(View.GONE);
            } else {
                binding.txtExpanded.setVisibility(View.VISIBLE);
                txtDetail.setMaxLines(5);
            }
        } else {
            binding.layoutMain.setVisibility(View.GONE);
            binding.rlDownload.setVisibility(View.GONE);
            binding.rlLove.setVisibility(View.GONE);
            binding.rlCart.setVisibility(View.GONE);
            binding.progressLoading.setVisibility(View.VISIBLE);
        }
    }

    private void setUpReFilmAdapter() {
        recFilmAdapter = new FilmAdapter(listReFilm, this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rcvReconmmend.addItemDecoration(new HorizontalItemDecoration(Converter.dpToPx(this, 15)));
        binding.rcvReconmmend.setItemAnimator(new DefaultItemAnimator());
        binding.rcvReconmmend.setLayoutManager(layoutManager);
        binding.rcvReconmmend.setAdapter(recFilmAdapter);
    }

    private void setUpSimFilmAdapter() {
        simFilmAdapter = new FilmAdapter(listSimFilm, this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rcvSimilarFilms.addItemDecoration(new HorizontalItemDecoration(Converter.dpToPx(this, 15)));
        binding.rcvSimilarFilms.setItemAnimator(new DefaultItemAnimator());
        binding.rcvSimilarFilms.setLayoutManager(layoutManager);
        binding.rcvSimilarFilms.setAdapter(simFilmAdapter);
    }

    private void setUpCastAdapter() {
        castAdapter = new CastAdapter(castList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rcvCast.addItemDecoration(new HorizontalItemDecoration(Converter.dpToPx(this, 15)));
        binding.rcvCast.setLayoutManager(layoutManager);
        binding.rcvCast.setAdapter(castAdapter);
    }

    @Override
    public void onClickNowDetailFilm(Results resultFilm, int position) {
        Intent intent = new Intent(this, DetailFilmActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(DetailFilmActivity.ID, resultFilm.getId() + "");
        bundle.putString(DetailFilmActivity.KEY_FROM, DetailFilmActivity.FROM_DETAIL);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void actionMoreFilm() {
        btnSimilar.setOnClickListener(view -> {
            Intent intent = new Intent(DetailFilmActivity.this, AllFilmActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(DetailFilmActivity.ID, id + "");
            bundle.putString(DetailFilmActivity.KEY_FROM, DetailFilmActivity.FROM_SIMILAR);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        btnRecommend.setOnClickListener(view -> {
            Intent intent = new Intent(DetailFilmActivity.this, AllFilmActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(DetailFilmActivity.ID, id + "");
            bundle.putString(DetailFilmActivity.KEY_FROM, DetailFilmActivity.FROM_RECOMMEND);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    private void watchFilm(String videoId) {
        if (detailFilms != null) {
            binding.videoViewClick.setOnClickListener(view -> {
                Intent intent = new Intent(DetailFilmActivity.this, WatchFilmActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(VIDEO_ID, videoId);
                bundle.putString(ID, detailFilms.getId() + "");
                intent.putExtras(bundle);
                startActivity(intent);
            });
        }
    }

    private void openCartFilm() {
        binding.rlCart.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            Intent intent = new Intent(DetailFilmActivity.this, MainActivity.class);
            bundle.putString(KEY_FROM, FROM_DETAIL);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    private void insertFilm(@NonNull DetailFilm detailFilm) {
        if (filmDB == null) {
            Film film = new Film(detailFilm.getId(), detailFilm.getTitle(),
                    MainActivity.HEADER_URL_IMAGE + detailFilm.getPosterPath(),
                    Float.parseFloat(detailFilm.getVoteAverage() / 2 + ""), Converter.convertStringToDate(detailFilms.getReleaseDate()), 0, 0, 0);
            detailFilmViewModels.insertFilm(film);
        }
    }

    @SuppressLint("SetTextI18n")
    private void insertFilmToCart(DetailFilm detailFilm) {
        if (cartFilmList != null)
            binding.txtNumCart.setText(cartFilmList.size() + "");
        if (cartFilm == null) {
            binding.payment.setVisibility(View.VISIBLE);
            binding.payment.setOnClickListener(view -> new CircleAnimationUtil().attachActivity(DetailFilmActivity.this).setTargetView(binding.payment).setMoveDuration(1000).setDestView(binding.rlCart).setAnimationListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    detailFilmViewModels.insertFilmCart(new Cart(detailFilm.getId(), detailFilm.getTitle(),
                            MainActivity.HEADER_URL_IMAGE + detailFilm.getPosterPath(),
                            Float.parseFloat(detailFilm.getVoteAverage() / 2 + ""), Converter.convertStringToDate(detailFilms.getReleaseDate())));
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (cartFilmList != null)
                        binding.txtNumCart.setText(cartFilmList.size() + "");
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            }).startAnimation());
        } else
            binding.payment.setVisibility(View.GONE);
    }

    private void updateFilmLove(Film films) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (films.getFilmLove() == 1) {
            binding.imgHeart.setImageResource(R.drawable.heart_red);
            binding.rlLove.setOnClickListener(view -> {
                if (firebaseUser == null) {
                    Toast.makeText(getBaseContext(), "Please login to unloved this movie", Toast.LENGTH_LONG).show();
                } else {
                    Film film = films;
                    film.setFilmLove(0);
                    detailFilmViewModels.updateFilm(film);
                    binding.imgHeart.setImageResource(R.drawable.heart);
                }
            });
        } else {
            binding.rlLove.setOnClickListener(view -> {
                if (firebaseUser == null) {
                    Toast.makeText(getBaseContext(), "Please login to love this movie", Toast.LENGTH_LONG).show();
                } else {
                    Film film = films;
                    film.setFilmLove(1);
                    detailFilmViewModels.updateFilm(film);
                    binding.imgHeart.setImageResource(R.drawable.heart_red);
                }
            });
        }

    }

    private void refreshLayout() {
        Handler handler = new Handler();
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        detailFilmViewModels.fetchDetailFilm(id, MainActivity.API_KEY);
                        detailFilmViewModels.fetchRecommendFilm(id, MainActivity.API_KEY);
                        detailFilmViewModels.fetchCastFilm(id, MainActivity.API_KEY);
                        detailFilmViewModels.fetchVideoTrailerFilm(id, MainActivity.API_KEY);
                        detailFilmViewModels.fetchCastFilm(id, MainActivity.API_KEY);
                        binding.swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    public void downloadVideo(String link) {

        binding.rlDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser == null) {
                    Toast.makeText(getBaseContext(), "Please login to download this movie", Toast.LENGTH_LONG).show();
                } else {
                    if (detailFilms != null) {
                        if (link.equals(VIDEO_LINK))
                            Toast.makeText(getBaseContext(), "Video does not exist", Toast.LENGTH_LONG).show();
                        else {
                            Toast.makeText(getBaseContext(), "Downloading", Toast.LENGTH_SHORT).show();
                            new YouTubeExtractor(getBaseContext()) {
                                @Override
                                public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                                    if (ytFiles != null) {
                                        int itag = 22;
                                        String downloadUrl = ytFiles.get(itag).getUrl();
                                        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
                                        request.setTitle("Movie Funny");
                                        request.setDescription(detailFilms.getTitle());
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        request.setVisibleInDownloadsUi(false);
                                        request.allowScanningByMediaScanner();
                                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Movie_Funny" +
                                                "/" + detailFilms.getTitle() + ".mp4");
                                        downloadmanager.enqueue(request);
                                    }
                                }
                            }.extract(link);
                        }
                    } else
                        Toast.makeText(getBaseContext(), "Please wait a minute", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detail_film:
            case R.id.txt_expanded:
                if (detailFilms != null) {
                    if ((detailFilms.getOverview() != null || detailFilms.getOverview().length() != 0) && txtDetail.getLineCount() >= 5) {
                        binding.txtExpanded.setVisibility(View.VISIBLE);
                        if (seeMore) {
                            if (txtDetail.getLineCount() >= 5) {
                                binding.titleExpand.setText("Collapse");
                                binding.imgExpand.setImageResource(R.drawable.ic_arrown_up);
                                txtDetail.setMaxLines(1000);
                                seeMore = false;
                            }
                        } else {
                            binding.titleExpand.setText("See more");
                            binding.imgExpand.setImageResource(R.drawable.ic_arrown_down);
                            txtDetail.setMaxLines(5);
                            seeMore = true;
                        }
                        break;
                    }
                } else {
                    binding.txtExpanded.setVisibility(View.GONE);
                }
        }
    }
}