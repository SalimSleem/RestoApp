package com.example.RestoApp.screens.authentication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.RestoApp.screens.main.MainActivity;
import com.example.RestoApp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthenticationActivity extends AppCompatActivity
        implements LoginFragment.LoginFragmentListener, RegisterFragment.RegisterFragmentListener {

    @BindView(R.id.container)
    FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        addLoginFragment();
    }

    private void addLoginFragment() {
        LoginFragment fragment = LoginFragment.newInstance();

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    public void onLoginSuccess() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginFailure() {

    }

    @Override
    public void onRequestRegister() {
        RegisterFragment fragment = RegisterFragment.newInstance();

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment, RegisterFragment.TAG)
                .addToBackStack(RegisterFragment.TAG)
                .commit();
    }

    @Override
    public void onRegisterSuccess() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void onRegisterFailure() {

    }
}
