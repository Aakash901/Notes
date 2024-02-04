package com.example.notes.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.notes.R;
import com.example.notes.model.UserData;
import com.example.notes.ui.fragment.HomeFragment;
import com.example.notes.utils.Toaster;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    // Declaring all the variable
    private GoogleSignInOptions mGoogleSignInOptions;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;

    //Declaring all the widgets
    private ProgressBar progressBar;
    private ImageView loginBtn;
    private RelativeLayout relativeLayout;

    private Toaster toaster = new Toaster();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        //Initialising all the widgets and variable
        loginBtn = findViewById(R.id.loginBtn);
        progressBar = findViewById(R.id.progressBar);
        relativeLayout = findViewById(R.id.relativeLayout);

        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);

        //Making status bar color as main color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.mainColor));


        SharedPreferences preferences = getSharedPreferences("userDetail", MODE_PRIVATE);
        String userId = preferences.getString("userId", null);

        // Checking if there is data exist then move to Home fragment page
        if (userId != null) {
            HomeFragment homeFragment = HomeFragment.newInstance(userId);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, homeFragment);
            fragmentTransaction.commit();
            relativeLayout.setVisibility(View.GONE);
        }


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                signIn();
            }

        });
    }


    private void signIn() {
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, RC_SIGN_IN);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                openHomeFragment(account);
            } catch (ApiException e) {
                int status = e.getStatusCode();
                // Show Toast according to exception
                if (status == 7) {
                    toaster.showToast(this, "Please check your network connection ");

                } else {
                    toaster.showToast(this, "Google sign-in failed: " + e.getStatusCode());

                }
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void openHomeFragment(GoogleSignInAccount account) {
        if (account != null) {
            String userId = account.getId();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String pic = String.valueOf(account.getPhotoUrl());
            relativeLayout.setVisibility(View.GONE);

            UserData userData = new UserData(name, email, pic,userId);

            //Store user information in shared preference
            SharedPreferences.Editor editor = getSharedPreferences("userDetail", MODE_PRIVATE).edit();
            userData.saveToSharedPreferences(editor);
            editor.apply();

            // open home fragment
            HomeFragment homeFragment = new HomeFragment(userId);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, homeFragment);
            fragmentTransaction.commit();
            progressBar.setVisibility(View.GONE);
        }

    }
}