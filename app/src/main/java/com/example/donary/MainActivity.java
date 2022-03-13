package com.example.donary;



import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Individual Facebook login
    private Button btn_other_sign_in_method, btn_login;
    private LoginButton btnfbLogin;
    private CallbackManager callbackManager;
    private TextView txtRegister, txt_forgot_password;
    private EditText txt_email, txt_password;
    private ProgressDialog progressDialog;
    //Firebase login
    private FirebaseAuth mAuth;

    //For Firebase Auth Login UI
    private static final int MY_REQUEST_CODE = 7117; //Any number you want
    List<AuthUI.IdpConfig> providers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_email = (EditText) findViewById(R.id.txtLoginEmail);
        txt_password = (EditText) findViewById(R.id.txtLoginPassword);
        txt_forgot_password = (TextView) findViewById(R.id.txtForgotPassword);
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        //To check if user is already logged in
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
            finish();
            startActivity(new Intent(MainActivity.this, Homepage.class));
        }

        //Login
        btn_login = (Button) findViewById(R.id.btnLogin);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(txt_email.getText().toString(), txt_password.getText().toString());
            }
        });

        //Navigate to register page
        txtRegister = (TextView) findViewById(R.id.txtRegister);
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterActivity();
            }
        });

        txt_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ForgotPassword.class));
            }
        });

        //Just an alternative, removing it soon
        //btn_other_sign_in_method = (Button) findViewById(R.id.btn_other_signin_method);
        mAuth = FirebaseAuth.getInstance();

        //Attempt for single Facebook login button
        //btnfbLogin = (LoginButton) findViewById(R.id.btn_fb_login);
        callbackManager = CallbackManager.Factory.create();
        /*btnfbLogin.setReadPermissions(Arrays.asList("email", "public_profile"));
        btnfbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Logging in...please wait");
                progressDialog.setCancelable(false);
                progressDialog.show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });*/

        //Building Firebase Auth UI
        /*btn_other_sign_in_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Init provider
                providers = Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(), //Email builder
                        new AuthUI.IdpConfig.FacebookBuilder().build(), //Facebook builder
                        new AuthUI.IdpConfig.GoogleBuilder().build()//Google builder
                );

                showSignInOptions();
            }
        });*/

    }

    /*private void showSignInOptions(){
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.FirebaseLoginTheme).setIsSmartLockEnabled(false)
                .build(),MY_REQUEST_CODE
        );
    }*/

    /*This is for Firebase Auth UI, comment for now for individual Facebook login button
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MY_REQUEST_CODE){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK){

               openHomepageActivity();
            }
            else{
                Toast.makeText(this, ""+response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    private void openHomepageActivity() {
        Intent intent = new Intent(this, Homepage.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Function used to call another to get Facebook information for app
    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken==null){
                Toast.makeText(MainActivity.this, "User logged out.", Toast.LENGTH_SHORT).show();
            }else{
                //openHomepageActivity();
            }

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        //Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            openHomepageActivity();
        }

    }

    private void handleFacebookAccessToken (AccessToken token){
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            openHomepageActivity();
                        }else{
                            Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //will be used in logging in (not implemented yet)
    private void checkEmailVerification(){
        FirebaseUser firebaseUser = mAuth.getInstance().getCurrentUser();
        Boolean emailFlag = firebaseUser.isEmailVerified();

        if(emailFlag){
            finish();
            startActivity(new Intent(MainActivity.this, Homepage.class));
        }else{
            Toast.makeText(this, "Please verify your account via your email.", Toast.LENGTH_SHORT).show();
            //Because in validate(), user is signed in, but if email is not verified, then sign them out
            mAuth.signOut();
        }
    }

    private void openRegisterActivity(){
        startActivity(new Intent(MainActivity.this, Register.class));
    }

    private void validate(String userName, String userPassword){

        if(userName.isEmpty() || userPassword.isEmpty()){
            Toast.makeText(MainActivity.this, "Please enter your details", Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.setMessage("Logging in...please wait");
            progressDialog.show();
            progressDialog.setCancelable(false);

            mAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        //Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        checkEmailVerification();
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
