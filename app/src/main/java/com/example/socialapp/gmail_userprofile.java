package com.example.socialapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class gmail_userprofile extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    CircleImageView profileImg;
    Button signOut;
    TextView gmailName,gmailEmail;
    String g_name,g_profileUrl,g_email;
    GoogleApiClient googleApiClient;
    SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS = "sahredprefs";
    public static final String EmailprofileUrl = "g_profileUrl", Emailname = "g_name", Emailemail = "g_email";

    public static final String GMAIL_LOGIN = "gmail_login";
    public static final String GMAIL_SAVE = "gmail_save";
    ProgressDialog progressDialog;

protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.gmail_profile);
    profileImg=findViewById(R.id.gmail_profile_pic);
    gmailEmail=findViewById(R.id.gmailemail);
    gmailName=findViewById(R.id.gmailname);
    signOut=findViewById(R.id.gmail_logout);
    progressDialog=new ProgressDialog(gmail_userprofile.this);
    progressDialog.setTitle("loading....");
    progressDialog.show();
    SharedPreferences setting=getSharedPreferences(GMAIL_LOGIN,0);
    if(setting.getString("gmail_saved","").toString().equals("gmail_saved")){
        sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        gmailName.setText(sharedPreferences.getString(Emailname,""));
        gmailEmail.setText(sharedPreferences.getString(Emailemail,""));
        Picasso.get().load(sharedPreferences.getString(EmailprofileUrl,"")).placeholder(R.drawable.profile).into(profileImg);
        progressDialog.dismiss();
    }
    GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
    googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(
            Auth.GOOGLE_SIGN_IN_API,googleSignInOptions
    ).build();
    GoogleSignInAccount googleSignInAccount= GoogleSignIn.getLastSignedInAccount(this);
    if (googleSignInAccount !=null){
        g_name=googleSignInAccount.getDisplayName();
        g_email=googleSignInAccount.getEmail();
        if(googleSignInAccount.getPhotoUrl() != null){
            g_profileUrl=googleSignInAccount.getPhotoUrl().toString();

        }else{g_profileUrl=null;}
        Picasso.get().load(g_profileUrl).placeholder(R.drawable.profile).into(profileImg);
        gmailName.setText(g_name);
        gmailEmail.setText(g_email);
        progressDialog.dismiss();
        sendemailData();
    }
    signOut.setOnClickListener(view -> {

        AlertDialog.Builder builder=new AlertDialog.Builder(gmail_userprofile.this);
        builder.setTitle("Logout?").setMessage("Aru u sure?").setPositiveButton(
                "yes",
                ((dialogInterface, i) -> {
                    Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                                        if (status.isSuccess()){
                                            SharedPreferences setting=getSharedPreferences(GMAIL_LOGIN,0);
                                            SharedPreferences.Editor editor_save=setting.edit();
                                            editor_save.remove("gmail_saved");
                                            editor_save.clear();
                                            editor_save.commit();

                                            SharedPreferences settings = getSharedPreferences(GMAIL_LOGIN, 0);
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.remove("gmail_logged");
                                            editor.clear();
                                            editor.commit();

                                            SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                            editor1.clear();
                                            editor1.commit();

                                            Toast.makeText(gmail_userprofile.this, "Gmail Logged out successfully!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(gmail_userprofile.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else{
                                            Toast.makeText(gmail_userprofile.this,"Gmail Logout failed!",Toast.LENGTH_SHORT).show();
                                        }
                        }
                    });
                })
        ).setNegativeButton("no",null);
        AlertDialog alertDialog= builder.create();
        alertDialog.show();

    });


}



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void sendemailData(){
        SharedPreferences settings = getSharedPreferences(GMAIL_SAVE, 0);
        SharedPreferences.Editor editor_save = settings.edit();
        editor_save.putString("gmail_saved", "gmail_saved");
        editor_save.commit();

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(Emailname,g_name);
        editor.putString(Emailemail,g_email);
        editor.putString(EmailprofileUrl,g_profileUrl);
        editor.apply();
    }
}
