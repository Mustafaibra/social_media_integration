package com.example.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    Button fbBtn,gmailBtn;
    CallbackManager callbackManager;
    String name,id,profilePic,email;
    SharedPreferences sharedPreferences;
    GoogleApiClient googleApiClient;
    ProgressDialog progressDialog;
    public static final int SignIn_value = 1;
    public static final String FB_LOGIN = "fb_login";
    String  loading="Loading date.......";

    public static final String GMAIL_LOGIN = "gmail_login";
    public static final String SHARED_PREFS = "sahredprefs";
    public static final String FbprofileUrl = "PfbprofileUrl", Fbname = "Pfb_name", Fbemail = "Pfb_email", Fbid = "Pfb_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    SharedPreferences fb_setting=getSharedPreferences(FB_LOGIN,0);
    SharedPreferences gmail_setting=getSharedPreferences(GMAIL_LOGIN,0);
    if (fb_setting.getString("fb_logged","").toString().equals("fb_logged")){
        startActivity(new Intent(MainActivity.this, UserProfile.class));
        this.finish();
    }else if(gmail_setting.getString("gmail_logged", "").toString().equals("gmail_logged")){

        startActivity(new Intent(MainActivity.this,gmail_userprofile.class));
        this.finish();

    }

    fbBtn=findViewById(R.id.fbbtn);
    callbackManager=CallbackManager.Factory.create();
    fbBtn.setOnClickListener(view -> {
        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                fbBtn.setVisibility(View.VISIBLE);
                gmailBtn.setVisibility(View.VISIBLE);
                progressDialog=new ProgressDialog(MainActivity.this);
                progressDialog.setTitle(loading);
                progressDialog.show();

                GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("Demo", object.toString());

                        try {
                            name = object.getString("name");
                            id = object.getString("id");
                            if(object.getJSONObject("picture").getJSONObject("data").getString("url") != null) {
                                profilePic = object.getJSONObject("picture").getJSONObject("data").getString("url");
                            }else{
                                profilePic = "null";
                            }

                            if(object.has("email")) {
                                email = object.getString("email");
                            }else{
                                email = " ";
                            }

                            SharedPreferences settings = getSharedPreferences(FB_LOGIN, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("fb_logged", "fb_logged");
                            editor.commit();
                            SendDate();
                            progressDialog.dismiss();

                            Toast.makeText(MainActivity.this, "Login successfully.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, UserProfile.class));
                            finish();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                Bundle bundle = new Bundle();
                bundle.putString("fields","picture.type(large),gender, name, id, birthday, friends, email");
                graphRequest.setParameters(bundle);
                graphRequest.executeAsync();



            }

            @Override
            public void onCancel() {
                fbBtn.setVisibility(View.VISIBLE);
                gmailBtn.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Login cancelled.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                fbBtn.setVisibility(View.VISIBLE);
                gmailBtn.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Login error.", Toast.LENGTH_SHORT).show();

            }
        });
    });
    gmailBtn=findViewById(R.id.gmailbtn);
        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).build();


        gmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SignIn_value);
            }
        });
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void SendDate() {
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(Fbname,name);
        editor.putString(Fbemail,email);
        editor.putString(FbprofileUrl,profilePic);
        editor.putString(Fbid,id);
        editor.apply();
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SignIn_value){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSigninResult(task);
        }

    }
    private void handleSigninResult(Task<GoogleSignInAccount> result){
        try{
            GoogleSignInAccount account = result.getResult(ApiException.class);
            fbBtn.setVisibility(View.INVISIBLE);
            gmailBtn.setVisibility(View.INVISIBLE);
            SharedPreferences settings = getSharedPreferences(GMAIL_LOGIN, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("gmail_logged", "gmail_logged");
            editor.commit();

            Toast.makeText(MainActivity.this, "Login successfully.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, gmail_userprofile.class));
            finish();

        } catch (ApiException e) {
            fbBtn.setVisibility(View.VISIBLE);
            gmailBtn.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Login failed.", Toast.LENGTH_SHORT).show();
            Log.v("Error", "signInResult:failed code = " + e.getStatusCode());
        }
    }
}
