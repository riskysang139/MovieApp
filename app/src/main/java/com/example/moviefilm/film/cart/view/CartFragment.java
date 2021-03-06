package com.example.moviefilm.film.cart.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.moviefilm.R;
import com.example.moviefilm.base.Converter;
import com.example.moviefilm.databinding.FragmentCartBinding;
import com.example.moviefilm.film.cart.adapter.CartAdapter;
import com.example.moviefilm.film.cart.model.FilmBill;
import com.example.moviefilm.film.cart.viewmodels.CartViewModel;
import com.example.moviefilm.film.detailFilm.view.DetailFilmActivity;
import com.example.moviefilm.film.login.view.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CartFragment extends Fragment implements CartAdapter.OnCartClickListener {
    FragmentCartBinding binding;
    private CartAdapter cartAdapter;
    private List<FilmBill.CartFB> filmList;
    private CartViewModel cartViewModel;
    private String keyFrom = "";
    public static float totalPrice = 0;
    private FirebaseAuth firebaseAuth;
    private List<FilmBill.CartFB> filmListBuy;
    private float myMoneyWallet = 0;
    private int position = 1;

    public static CartFragment getInstance() {
        Bundle args = new Bundle();
        CartFragment fragment = new CartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false);
        cartViewModel = ViewModelProviders.of(this).get(CartViewModel.class);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            cartViewModel.fetchMyWallet();
            observeFilmWallet();
            cartViewModel.fetchFilmCart();
            totalPrice = 0;
            keyFrom = getArguments().getString(DetailFilmActivity.KEY_FROM, "");
            binding.layoutLogin.setVisibility(View.VISIBLE);
            binding.layoutNotLogin.setVisibility(View.GONE);
        } else {
            binding.layoutLogin.setVisibility(View.GONE);
            binding.layoutNotLogin.setVisibility(View.VISIBLE);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        filmListBuy = new ArrayList<>();
        filmList = new ArrayList<>();
        binding.totalPayment.setSelected(true);
        binding.btnBack.setVisibility(View.GONE);
        binding.rlPayment.setVisibility(View.VISIBLE);
        binding.btnPayment.setOnClickListener(view1 -> insertBill());
        initAdapter();
        observerCartFilm();
        initLogin();
    }

    private void initLogin() {
        binding.btnOk.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(LoginActivity.KEY_FROM, DetailFilmActivity.FROM_CART);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    private void observerCartFilm() {
        cartViewModel.getCartListMutableLiveData().observe(getActivity(), cartList -> {
            filmList = cartList;
            if (cartList.size() > 0) {
                binding.txtNoData.setVisibility(View.GONE);
                binding.rlPayment.setVisibility(View.VISIBLE);
                if (cartAdapter != null) {
                    cartAdapter.setFilmListDB(cartList);
                    slPaymentAllFilm(cartList);
                }
            } else {
                binding.txtNoData.setVisibility(View.VISIBLE);
                binding.rlPayment.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible && FirebaseAuth.getInstance().getCurrentUser() == null) {
            binding.layoutNotLogin.setVisibility(View.VISIBLE);
            binding.layoutLogin.setVisibility(View.GONE);
        } else if (menuVisible && FirebaseAuth.getInstance().getCurrentUser() != null) {
            cartViewModel.fetchMyWallet();
            cartViewModel.fetchFilmCart();
            binding.cbSlAll.setChecked(false);
            totalPrice = 0;
            CartAdapter.numberChoice = 0;
            binding.totalPayment.setText("Total Payment : " + Converter.df.format(totalPrice) + " $");
            binding.btnPayment.setText("Payment (" + 0 + ")");
        }
    }

    @SuppressLint("SetTextI18n")
    private void initAdapter() {
        cartAdapter = new CartAdapter(new ArrayList<>(), getContext(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcvCart.setLayoutManager(layoutManager);
        binding.rcvCart.setAdapter(cartAdapter);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClickCart(int position, boolean isChoose, int numberChoice, FilmBill.CartFB cart) {
        if (isChoose) {
            if (position == 0) {
                this.position = position;
            }
            totalPrice += (filmList.get(position).getFilmRate() * 4);
            filmListBuy.add(cart);
        } else {
            binding.cbSlAll.setChecked(false);
            totalPrice -= (filmList.get(position).getFilmRate() * 4);
            if (totalPrice <= 0)
                totalPrice = 0;
            filmListBuy.remove(cart);
        }
        binding.totalPayment.setText("Total Payment : " + Converter.df.format(totalPrice) + " $");
        binding.btnPayment.setText("Payment (" + numberChoice + ")");
    }

    @Override
    public void onClickDetail(String id) {
        Intent intent = new Intent(getActivity(), DetailFilmActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(DetailFilmActivity.ID, id);
        bundle.putString(DetailFilmActivity.KEY_FROM, DetailFilmActivity.FROM_CART);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onCLickDelete(FilmBill.CartFB film, int position) {
        cartViewModel.deleteFilmCart(position, film);
        filmList.remove(position);
        cartAdapter.notifyItemRemoved(position);
        cartViewModel.fetchFilmCart();
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void slPaymentAllFilm(List<FilmBill.CartFB> filmList) {
        binding.cbSlAll.setOnClickListener(view -> {
            totalPrice = 0;
            if (binding.cbSlAll.isChecked()) {
                if (cartAdapter != null) {
                    for (FilmBill.CartFB film : filmList)
                        film.setChecked(true);
                    cartAdapter.setFilmListDB(filmList);
                    cartAdapter.notifyDataSetChanged();
                    for (int i = 0; i < filmList.size(); i++)
                        totalPrice += (filmList.get(i).getFilmRate() * 4);
                    CartAdapter.numberChoice = filmList.size();
                    binding.btnPayment.setText("Payment (" + filmList.size() + ")");
                }
            } else {
                for (FilmBill.CartFB film : filmList)
                    film.setChecked(false);
                cartAdapter.setFilmListDB(filmList);
                cartAdapter.notifyDataSetChanged();
                totalPrice = 0;
                CartAdapter.numberChoice = 0;
                binding.btnPayment.setText("Payment (" + 0 + ")");
            }
            binding.totalPayment.setText("Total Payment : " + Converter.df.format(totalPrice) + " $");
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void insertBill() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Notification")
                    .setMessage("Please login to payment your bill")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(LoginActivity.KEY_FROM, DetailFilmActivity.FROM_CART);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", null)
                    .setIcon(R.drawable.logo_movie_app)
                    .show();
        } else {
            if (CartAdapter.numberChoice == 0)
                Toast.makeText(getContext(), "Please Choose Film You Want To Buy !", Toast.LENGTH_LONG).show();
            else {
                new AlertDialog.Builder(getContext())
                        .setTitle("Buy Film !!!")
                        .setMessage("Do you want to buy it !")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                paymentFilm();
                            }
                        })
                        .setNegativeButton("No", null)
                        .setIcon(R.drawable.logo_movie_app)
                        .show();
            }
        }
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void paymentFilm() {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        long unixTime = System.currentTimeMillis();
        if (binding.cbSlAll.isChecked()) {
            if (myMoneyWallet > 0 && myMoneyWallet > totalPrice) {
                cartViewModel.insertFilmBuy(filmList, totalPrice + "", timeStamp, unixTime + "");
                cartViewModel.deleteAllFilmCart();
                cartViewModel.updateMyWallet((myMoneyWallet - totalPrice) + "");
                cartViewModel.fetchFilmCart();
                cartViewModel.fetchMyWallet();
                binding.cbSlAll.setChecked(false);
                filmList.clear();
                filmListBuy.clear();
                CartAdapter.numberChoice = 0;
                totalPrice = 0;
                binding.txtNoData.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Buy Film SuccessFully !", Toast.LENGTH_LONG).show();
                cartAdapter.notifyDataSetChanged();
                binding.totalPayment.setText("Total Payment : " + Converter.df.format(totalPrice) + " $");
                binding.btnPayment.setText("Payment (" + 0 + ")");
            } else {
                Toast.makeText(getContext(), "Please top up to make the payment !", Toast.LENGTH_LONG).show();
            }
        } else {
            if (filmListBuy.size() == 0)
                Toast.makeText(getContext(), "Please Choose Film You Want To Buy !", Toast.LENGTH_LONG).show();
            else {
                if (myMoneyWallet > 0 && myMoneyWallet > totalPrice) {
                    cartViewModel.insertFilmBuy(filmListBuy, totalPrice + "", timeStamp, unixTime + "");
                    for (FilmBill.CartFB cart : filmListBuy)
                        cartViewModel.deleteFilmCart(1, cart);

                    cartViewModel.updateMyWallet((myMoneyWallet - totalPrice) + "");
                    cartViewModel.fetchFilmCart();
                    if (position == 0) {
                        filmList.remove(0);
                        cartAdapter.notifyItemRemoved(0);
                    }
                    filmListBuy.clear();
                    cartAdapter.notifyDataSetChanged();
                    totalPrice = 0;
                    CartAdapter.numberChoice = filmList.size();
                    Toast.makeText(getContext(), "Buy Film SuccessFully !", Toast.LENGTH_LONG).show();
                    binding.totalPayment.setText("Total Payment : " + Converter.df.format(totalPrice) + " $");
                    binding.btnPayment.setText("Payment (" + 0 + ")");
                } else {
                    Toast.makeText(getContext(), "Please top up to make the payment !", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void observeFilmWallet() {
        cartViewModel.getWalletResponseLiveData().observe(getActivity(), filmWallet -> {
            if (filmWallet != null) {
                try {
                    myMoneyWallet = Float.parseFloat(filmWallet.getTotalMoney());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
