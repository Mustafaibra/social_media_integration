package com.example.socialapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Picture;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    TextView fbname,fbemail;
    Button logoutbtn;
    CircleImageView profile;
    SharedPreferences sharedPreferences;
    String Pfb_id, Pfb_name, Pfb_profileUrl, Pfb_email;
    public static final String SHARED_PREFS = "sahredprefs";
    public static final String FbprofileUrl = "PfbprofileUrl", Fbname = "Pfb_name", Fbemail = "Pfb_email", Fbid = "Pfb_id";

    public static final String FB_LOGIN = "fb_login";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fb_profile);
        fbname=findViewById(R.id.fbname);
        fbemail=findViewById(R.id.fbemail);
        logoutbtn=findViewById(R.id.logout);
        profile=findViewById(R.id.fb_profile_pic);

        sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        Pfb_id=sharedPreferences.getString(Fbid,"");
        Pfb_name=sharedPreferences.getString(Fbname,"");
        Pfb_email=sharedPreferences.getString(Fbemail,"");
        Pfb_profileUrl=sharedPreferences.getString(FbprofileUrl,"");
        fbname.setText(Pfb_name);
        fbemail.setText(Pfb_email);
        Picasso.get().load(Pfb_profileUrl).placeholder(R.drawable.profile).into(profile);

        logoutbtn.setOnClickListener(view -> {
            AlertDialog.Builder builder=new AlertDialog.Builder(UserProfile.this);
            builder.setTitle("Logout?").setMessage("Are you Sure?").setPositiveButton(
                    "yes",
                    ((dialogInterface, i) -> {
                            SharedPreferences setting=getSharedPreferences(FB_LOGIN,0);
                            SharedPreferences.Editor editor=setting.edit();
                            editor.remove("fb_logged");
                            editor.clear();
                            editor.commit();

                        SharedPreferences.Editor editor1=sharedPreferences.edit();

                        editor.clear();
                        editor.commit();

                        Toast.makeText(UserProfile.this,"Loggedout Successfully",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(UserProfile.this,MainActivity.class);
                        startActivity(intent);
                        finish();


                    })
            ).setNegativeButton("no",null);
            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        });


    }
    AccessTokenTracker accessTokenTracker =  new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                profile.setImageResource(0);
                fbname.setText(" ");
                fbemail.setText(" ");
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

}
