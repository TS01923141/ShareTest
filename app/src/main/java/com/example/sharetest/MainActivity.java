package com.example.sharetest;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.Manifest.permission.INTERNET;

public class MainActivity extends AppCompatActivity {
    //FaceBook
    Button btnShareLink, btnSharePhoto, btnShareVideo;
    //Line
    Button btnShareText;
    //All
    Button btnShareAll;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    private static final String TAG = "MainActivity";
    private static final int REQUEST_VIDEO_CODE = 1000;

    ApplicationInfo applicationinfo;
    private static final int RequestPermissionCode = 1;

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();

            if (ShareDialog.canShow(SharePhotoContent.class)){
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        checkPermissions();

//        printKeyHash();

        btnShareLink = findViewById(R.id.button_main_shareLink);
        btnSharePhoto = findViewById(R.id.button_main_sharePhoto);
        btnShareVideo = findViewById(R.id.button_main_shareVideo);
        btnShareText = findViewById(R.id.button_main_shareText);
        btnShareAll = findViewById(R.id.button_main_shareByInstalledApp);

        //init fb
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        btnShareLink.setOnClickListener((view) -> {
            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    Toast.makeText(MainActivity.this, "Share successful", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(MainActivity.this, "Share cancel", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setQuote("this is useful linl")
                    .setContentUrl(Uri.parse("http://youtube.com"))
                    .build();
            if (ShareDialog.canShow(ShareLinkContent.class)){
                shareDialog.show(linkContent);
            }
        });

        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(MainActivity.this, "Share successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Share cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnSharePhoto.setOnClickListener((view) -> {
            Picasso.with(getBaseContext())
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/7/7c/Facebook_New_Logo_%282015%29.svg/2000px-Facebook_New_Logo_%282015%29.svg.png")
                    .into(target);
        });

        btnShareVideo.setOnClickListener((view) ->{
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select video"), REQUEST_VIDEO_CODE);
        });

//        PackageManager pm = this.getPackageManager();
//        List<ApplicationInfo> appList =  pm.getInstalledApplications(0);
//        for(ApplicationInfo app: appList )
//        {
//            Log.i("info","app:" +  app );
////            if( app.packageName.equals(LINE_PACKAGE_NAME))
////            {
////
////                return true;
////            }
//        }

        btnShareText.setOnClickListener((view) ->{
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "Your Message Here");
            intent.setType("text/plain");
            intent.setPackage(getString(R.string.package_name_line));
            startActivity(intent);
        });

        btnShareAll.setOnClickListener((view) ->{
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareBody = "Your body here";
            String shareSub = "your Subject here";
            intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
//            intent.putExtra(Intent.EXTRA_TEXT, shareBody);
            //if want share to FaceBook , need a URL link either text , like below
            intent.putExtra(Intent.EXTRA_TEXT, "http://www.google.com");
            startActivity(Intent.createChooser(intent, "分享到"));
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_VIDEO_CODE){
                Uri selecteVideo = data.getData();

                ShareVideo video = new ShareVideo.Builder()
                        .setLocalUrl(selecteVideo)
                        .build();

                ShareVideoContent videoContent = new ShareVideoContent.Builder()
                        .setContentTitle("this is useful video")
                        .setContentDescription("Funny video from EDMT Dev download from YouTube")
                        .setVideo(video)
                        .build();

                if (shareDialog.canShow(ShareVideoContent.class)) {
                    shareDialog.show(videoContent);
                }
            }
        }
    }

    private void printKeyHash(){
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo("com.example.sharetest",
                    PackageManager.GET_SIGNATURES);
            for(Signature signature : packageInfo.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e(TAG, Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkAllPermission())
                requestPermission();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {
                        INTERNET,
                        //check more permissions if you want
//                     ........


                }, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {

                    boolean IntentPermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;

//                    .......


                    if (IntentPermission) {

                        Toast.makeText(MainActivity.this, "Permissions acquired", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "One or more permissions denied", Toast.LENGTH_LONG).show();

                    }
                }

                break;
            default:
                break;
        }
    }

    public boolean checkAllPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);
//        .....


        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED;
    }
}
