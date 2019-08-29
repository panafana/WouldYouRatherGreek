package panafana.example.panaf.wouldyourather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.TextViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

import panafana.example.panaf.wouldyourather.models.Comment;
import panafana.example.panaf.wouldyourather.models.Question;
import panafana.example.panaf.wouldyourather.models.Stats;
import panafana.example.panaf.wouldyourather.utils.Manager;

import static java.lang.Math.abs;
import static java.lang.Math.round;

//import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    SharedPreferences SP;
    SharedPreferences SP2;
    Context ctx = this;
    private String jsonResult;
    private String jsonStatsResult;
    private String jsonCommentResult;
    private String url = "http://83.212.84.230/getquestions.php";
    private String getStatsUrl ="http://83.212.84.230/getstats.php";
    private String commentsUrl ="http://83.212.84.230/getcomments.php";
    int globalI =0,newquestionscount=0;
    int showstats,buttonPressed=0,questionsTillAd=20;
    float x=0,y=0,xu=0,yu=0;
    String currentQstUp,currentQstDown;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private DrawerLayout mDrawerLayout;
    RoundCornerProgressBar progress1;
    float sanity=50.0f ;
    private AdView mAdView;
    int MY_PERMISSIONS_REQUEST_READ_STORAGE=0;
    private InterstitialAd mInterstitialAd;
    boolean isDefault = true;
    boolean isFunny = false;
    boolean isDisturbing = false;
    boolean isGrose = false;
    boolean isCouples = false;
    boolean isNSFW = false;
    int defaultQuestionsCount=0;
    int funnyQuestionsCount=0;
    int disturbingQuestionsCount=0;
    int groseQuestionsCount=0;
    int couplesQuestionsCount=0;
    int max;
    boolean first_open = true;
    ArrayList<Question> defaultQuestions = new ArrayList<>();
    ArrayList<Question> couplesQuestions = new ArrayList<>();
    ArrayList<Question> groseQuestions = new ArrayList<>();
    ArrayList<Question> disturbingQuestions = new ArrayList<>();
    ArrayList<Question> funnyQuestions = new ArrayList<>();
    ArrayList<Question> runningQuestions = new ArrayList<>();
    LabeledSwitch general_switch;
    LabeledSwitch funny_switch;
    LabeledSwitch couples_switch;
    LabeledSwitch difficult_switch;
    LabeledSwitch grose_switch;

    ArrayList<ArrayList<Comment>> allcomments = new ArrayList<>();
    Manager manager;
    Context context;
    ArrayList<Question> allquestions;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //MobileAds.initialize(this,"ca-app-pub-2471480338929808~1664063554");
        context = this;
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2471480338929808/6440025315");

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.LEVEL, "main");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LEVEL_START, bundle);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        System.out.println("ad banner " + mAdView.isLoading());

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        mDrawerLayout = findViewById(R.id.drawer_layout);

//        accessWebService();
//        GetStats g = new GetStats();
//        g.execute(new String[]{getStatsUrl});
        SP2 = getSharedPreferences("gameState", MODE_PRIVATE);

        showstats = 1;
        SharedPreferences SP4 = getSharedPreferences("stats", MODE_PRIVATE);
        sanity = SP4.getFloat("sanity", 50.00f);

        manager = new Manager();
        manager.getQuestions(this,false);

        isDefault = SP2.getBoolean("isDefault",true);
        isFunny = SP2.getBoolean("isFunny",false);
        isDisturbing = SP2.getBoolean("isDisturbing",false);
        isGrose = SP2.getBoolean("isGrose",false);
        isCouples = SP2.getBoolean("isCouples",false);





        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        int id = menuItem.getItemId();
                        //System.out.println("menu id "+id);


                        //noinspection SimplifiableIfStatement
                        if (id == R.id.reset) {
                            resetGameState();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mDrawerLayout.closeDrawers();
                                }
                            }, 200);
                        } else if (id == R.id.submitQuestion) {
                            Intent i = new Intent(MainActivity.this, SubmitQuestion.class);
                            startActivity(i);
                        } else if (id == R.id.changeTextColor) {
                            TextView upperText = findViewById(R.id.textViewUp);
                            TextView lowerText = findViewById(R.id.textViewDown);
                            System.out.println("color value " + upperText.getCurrentTextColor());
                            if (upperText.getCurrentTextColor() == -14080463) {
                                upperText.setTextColor(Color.WHITE);
                                lowerText.setTextColor(Color.WHITE);
                            } else {
                                upperText.setTextColor(getResources().getColor(R.color.text_color));
                                lowerText.setTextColor(getResources().getColor(R.color.text_color));
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mDrawerLayout.closeDrawers();
                                }
                            }, 200);
                        } else if (id == R.id.logout) {
                            SharedPreferences SP = getSharedPreferences("user", MODE_PRIVATE);
                            SharedPreferences.Editor SPE = SP.edit();
                            SPE.clear();
                            SPE.apply();
                            Intent i = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }else if(id==R.id.general){
                            Log.e("tapped","general");
                            general_switch.performClick();
                        }else if(id == R.id.funny){
                            Log.e("tapped","funny");
                            funny_switch.performClick();
                        }else if(id == R.id.difficult){
                            Log.e("tapped","difficult");
                            difficult_switch.performClick();
                        }else if(id == R.id.couples){
                            Log.e("tapped","couples");
                            couples_switch.performClick();
                        }else if(id == R.id.grose){
                            Log.e("tapped","grose");
                            grose_switch.performClick();
                        }
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        return true;

                    }


                });



        general_switch =(LabeledSwitch) navigationView.getMenu().findItem(R.id.general).getActionView();
        general_switch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                SP2.edit().putBoolean("isDefault",isOn).apply();
                if(isOn){
                    Log.e("general switch","on");
                    isDefault=true;
                    runningQuestions.clear();
                    if(isDefault){
                        runningQuestions.addAll(defaultQuestions);
                    }
                    if(isCouples){
                        runningQuestions.addAll(couplesQuestions);
                    }
                    if(isDisturbing){
                        runningQuestions.addAll(disturbingQuestions);
                    }
                    if(isFunny){
                        runningQuestions.addAll(funnyQuestions);
                    }
                    if(isGrose){
                        runningQuestions.addAll(groseQuestions);
                    }
                }else{
                    Log.e("general switch","off");
                    isDefault = false;
                    if(!isDefault&&!isFunny&&!isDisturbing&&!isGrose&&!isCouples){
                        general_switch.performClick();
                    }
                    runningQuestions.clear();
                    if(isDefault){
                        runningQuestions.addAll(defaultQuestions);
                    }
                    if(isCouples){
                        runningQuestions.addAll(couplesQuestions);
                    }
                    if(isDisturbing){
                        runningQuestions.addAll(disturbingQuestions);
                    }
                    if(isFunny){
                        runningQuestions.addAll(funnyQuestions);
                    }
                    if(isGrose){
                        runningQuestions.addAll(groseQuestions);
                    }

                }

            }
        });
        funny_switch =(LabeledSwitch) navigationView.getMenu().findItem(R.id.funny).getActionView();
        funny_switch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                SP2.edit().putBoolean("isFunny",isOn).apply();
                if(isOn){
                    Log.e("funny_switch","on");
                    isFunny = true;
                    runningQuestions.clear();
                    if(isDefault){
                        runningQuestions.addAll(defaultQuestions);
                    }
                    if(isCouples){
                        runningQuestions.addAll(couplesQuestions);
                    }
                    if(isDisturbing){
                        runningQuestions.addAll(disturbingQuestions);
                    }
                    if(isFunny){
                        runningQuestions.addAll(funnyQuestions);
                    }
                    if(isGrose){
                        runningQuestions.addAll(groseQuestions);
                    }
                }else{
                    Log.e("funny_switch","off");
                    isFunny = false;
                    if(!isDefault&&!isFunny&&!isDisturbing&&!isGrose&&!isCouples){
                        general_switch.performClick();
                    }
                    runningQuestions.clear();
                    if(isDefault){
                        runningQuestions.addAll(defaultQuestions);
                    }
                    if(isCouples){
                        runningQuestions.addAll(couplesQuestions);
                    }
                    if(isDisturbing){
                        runningQuestions.addAll(disturbingQuestions);
                    }
                    if(isFunny){
                        runningQuestions.addAll(funnyQuestions);
                    }
                    if(isGrose){
                        runningQuestions.addAll(groseQuestions);
                    }

                }

            }
        });

        couples_switch =(LabeledSwitch) navigationView.getMenu().findItem(R.id.couples).getActionView();
        couples_switch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                SP2.edit().putBoolean("isCouples",isOn).apply();
                if(isOn){
                    Log.e("couples_switch","on");
                    isCouples = true;
                    runningQuestions.clear();
                    if(isDefault){
                        runningQuestions.addAll(defaultQuestions);
                    }
                    if(isCouples){
                        runningQuestions.addAll(couplesQuestions);
                    }
                    if(isDisturbing){
                        runningQuestions.addAll(disturbingQuestions);
                    }
                    if(isFunny){
                        runningQuestions.addAll(funnyQuestions);
                    }
                    if(isGrose){
                        runningQuestions.addAll(groseQuestions);
                    }
                }else{
                    Log.e("couples_switch","off");
                    isCouples = false;
                    if(!isDefault&&!isFunny&&!isDisturbing&&!isGrose&&!isCouples){
                        general_switch.performClick();
                    }
                    runningQuestions.clear();
                    if(isDefault){
                        runningQuestions.addAll(defaultQuestions);
                    }
                    if(isCouples){
                        runningQuestions.addAll(couplesQuestions);
                    }
                    if(isDisturbing){
                        runningQuestions.addAll(disturbingQuestions);
                    }
                    if(isFunny){
                        runningQuestions.addAll(funnyQuestions);
                    }
                    if(isGrose){
                        runningQuestions.addAll(groseQuestions);
                    }

                }

            }
        });

        difficult_switch =(LabeledSwitch) navigationView.getMenu().findItem(R.id.difficult).getActionView();
        difficult_switch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                SP2.edit().putBoolean("isDisturbing",isOn).apply();
                if(isOn){
                    Log.e("difficult_switch","on");
                    isDisturbing = true;
                    runningQuestions.clear();
                    if(isDefault){
                        runningQuestions.addAll(defaultQuestions);
                    }
                    if(isCouples){
                        runningQuestions.addAll(couplesQuestions);
                    }
                    if(isDisturbing){
                        runningQuestions.addAll(disturbingQuestions);
                    }
                    if(isFunny){
                        runningQuestions.addAll(funnyQuestions);
                    }
                    if(isGrose){
                        runningQuestions.addAll(groseQuestions);
                    }
                }else{
                    Log.e("difficult_switch","off");
                    isDisturbing = false;
                    if(!isDefault&&!isFunny&&!isDisturbing&&!isGrose&&!isCouples){
                        general_switch.performClick();
                    }

                    runningQuestions.clear();
                    if(isDefault){
                        runningQuestions.addAll(defaultQuestions);
                    }
                    if(isCouples){
                        runningQuestions.addAll(couplesQuestions);
                    }
                    if(isDisturbing){
                        runningQuestions.addAll(disturbingQuestions);
                    }
                    if(isFunny){
                        runningQuestions.addAll(funnyQuestions);
                    }
                    if(isGrose){
                        runningQuestions.addAll(groseQuestions);
                    }
                }

            }
        });

        grose_switch =(LabeledSwitch) navigationView.getMenu().findItem(R.id.grose).getActionView();
        grose_switch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {

                SP2.edit().putBoolean("isGrose",isOn).apply();
                if(isOn){
                    Log.e("grose_switch","on");
                    isGrose = true;
                    runningQuestions.clear();
                    if(isDefault){
                        runningQuestions.addAll(defaultQuestions);
                    }
                    if(isCouples){
                        runningQuestions.addAll(couplesQuestions);
                    }
                    if(isDisturbing){
                        runningQuestions.addAll(disturbingQuestions);
                    }
                    if(isFunny){
                        runningQuestions.addAll(funnyQuestions);
                    }
                    if(isGrose){
                        runningQuestions.addAll(groseQuestions);
                    }
                }else{
                    isGrose = false;
                    if(!isDefault&&!isFunny&&!isDisturbing&&!isGrose&&!isCouples){
                        general_switch.performClick();
                    }
                    Log.e("grose_switch","off");
                    runningQuestions.clear();
                    if(isDefault){
                        runningQuestions.addAll(defaultQuestions);
                    }
                    if(isCouples){
                        runningQuestions.addAll(couplesQuestions);
                    }
                    if(isDisturbing){
                        runningQuestions.addAll(disturbingQuestions);
                    }
                    if(isFunny){
                        runningQuestions.addAll(funnyQuestions);
                    }
                    if(isGrose){
                        runningQuestions.addAll(groseQuestions);
                    }
                }

            }
        });




        View header = navigationView.getHeaderView(0);
        TextView nav_header = header.findViewById(R.id.nav_header_textView);
        SharedPreferences SP3 = getSharedPreferences("user", MODE_PRIVATE);
        String username = SP3.getString("username", "empty");
        nav_header.setText(username);

        progress1 = (RoundCornerProgressBar) header.findViewById(R.id.progress_bar);
        progress1.setProgressColor(Color.parseColor("#ed3b27"));
        progress1.setProgressBackgroundColor(Color.parseColor("#808080"));
        progress1.setMax(100);
        progress1.setProgress(50);



//        String[] uniqueCategoriesArray = getResources().getStringArray(R.array.categories);
//        Menu menu = navigationView.getMenu();
//        Menu submenuCategories = menu.addSubMenu(Menu.NONE,Menu.NONE,0,"Κατηγορίες ερωτήσεων");
//
//        for(int i=0;i<uniqueCategoriesArray.length;i++){
//            submenuCategories.add(Menu.NONE,i,Menu.NONE,(uniqueCategoriesArray[i])).setCheckable(true).setIcon(R.drawable.ic_check_box_outline_blank_black_24dp).setChecked(false).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem menuItem) {
//                    System.out.println(menuItem.toString());
//                    if(menuItem.isChecked()){
//                        menuItem.setIcon(R.drawable.ic_check_box_outline_blank_black_24dp);
//                        switch (menuItem.toString()){
//                            case "Γενικές":isDefault=false;
//                                resetGameState();
//                                break;
//                            case "Αστείες":isFunny=false;
//                                resetGameState();
//                                break;
//                            case "Ανησυχητικές":isDisturbing=false;
//                                resetGameState();
//                                break;
//                            case "Αηδιαστικές": isGrose=false;
//                                resetGameState();
//                                break;
//                            case "Για ζευγάρια": isCouples=false;
//                                resetGameState();
//                                break;
//                        }
//                        //menuItem.setChecked(false);
//                        if(!isDefault&&!isFunny&&!isDisturbing&&!isGrose&&!isCouples){
//                            isDefault=true;
//                            max=defaultQuestionsCount;
//                            submenuCategories.getItem(0).setChecked(true).setIcon(R.drawable.ic_check_box_black_24dp);
//                        }
//                        System.out.println("max "+max);
//                        System.out.println("default "+isDefault);
//                        System.out.println("funny "+isFunny);
//                        System.out.println("disturbing "+isDisturbing);
//                        System.out.println("grose "+isGrose);
//                        System.out.println("couples "+isCouples);
//
//                    }else{
//
//                        menuItem.setIcon(R.drawable.ic_check_box_black_24dp);
//                        switch (menuItem.toString()){
//                            case "Γενικές":isDefault=true;
//                                resetGameState();
//                                break;
//                            case "Αστείες":isFunny=true;
//                                resetGameState();
//                                break;
//                            case "Ανησυχητικές":isDisturbing=true;
//                                resetGameState();
//                                break;
//                            case "Αηδιαστικές": isGrose=true;
//                                resetGameState();
//                                break;
//                            case "Για ζευγάρια": isCouples=true;
//                                resetGameState();
//                                break;
//                        }
//                        System.out.println("max "+max);
//                        System.out.println("default "+isDefault);
//                        System.out.println("funny "+isFunny);
//                        System.out.println("disturbing "+isDisturbing);
//                        System.out.println("grose "+isGrose);
//                        System.out.println("couples "+isCouples);
//                        //menuItem.setChecked(true);
//                    }
//                    return false;
//                }
//            });
//        }
//        submenuCategories.getItem(0).setChecked(true).setIcon(R.drawable.ic_check_box_black_24dp);
//


        //permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Αδεια χρήσης χώρου αποθήκευσης");
                alertBuilder.setMessage("Η άδεια αυτή χρησιμοποιείται μόνο στην λειτουργία του πλήκτρου share");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_READ_STORAGE
                                );
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_STORAGE);
            }
        }

    }


    void resetGameState (){
        globalI = 0;
        runningQuestions.clear();
        if(isDefault){
            runningQuestions.addAll(defaultQuestions);
        }
        if(isCouples){
            runningQuestions.addAll(couplesQuestions);
        }
        if(isDisturbing){
            runningQuestions.addAll(disturbingQuestions);
        }
        if(isFunny){
            runningQuestions.addAll(funnyQuestions);
        }
        if(isGrose){
            runningQuestions.addAll(groseQuestions);
        }

    }


    int nextQuestion(){
        int random=0 ;
        if(runningQuestions.size()>1){
            try{
                random  = new Random().nextInt(runningQuestions.size()-1) ;

            }catch (IllegalArgumentException e){
                random=0;
            }
            runningQuestions.remove(random);
            return random;

        }else{
            Toast.makeText(ctx, "Τέλος ερωτήσεων", Toast.LENGTH_LONG).show();
            resetGameState();
            try{
                random  = new Random().nextInt(runningQuestions.size()-1) ;

            }catch (IllegalArgumentException e){
                random=0;
            }
            runningQuestions.remove(random);
            return random;

        }
    }


    @SuppressLint("ClickableViewAccessibility")
    public void playGame(){
        SP = getSharedPreferences("questions", MODE_PRIVATE);
        SharedPreferences SP3 = getSharedPreferences("gender",MODE_PRIVATE);
        final String genderS = SP3.getString("gender","other");


        final SharedPreferences SP = getSharedPreferences("questions", MODE_PRIVATE);
        final String temp = SP.getString("allquestions",null);
        Gson gson33 = new Gson();
        Type type33 = new TypeToken<ArrayList<Question>>() {
        }.getType();
        allquestions = gson33.fromJson(temp, type33);



        Gson gson6 = new Gson();
        String json6 = SP.getString("allcategories", null);
        Type type6 = new TypeToken<ArrayList<String>>() {}.getType();
        final ArrayList<String>  allcategories = gson6.fromJson(json6, type6);


        final ImageView upperImage = findViewById(R.id.upperImage);
        final ImageView lowerImage = findViewById(R.id.lowerImage);
        final ImageView upperImage2 = findViewById(R.id.upperImage2);
        final ImageView lowerImage2 = findViewById(R.id.lowerImage2);
        final TextView upperText = findViewById(R.id.textViewUp);
        final TextView lowerText = findViewById(R.id.textViewDown);
        final ImageView shareImage = findViewById(R.id.share_stats);
        final ImageView shareImage2 = findViewById(R.id.share_stats2);
        final ImageView commentImage = findViewById(R.id.comment_stats);
        final ImageView commentImage2 = findViewById(R.id.comment_stats2);
        final ImageView or = findViewById(R.id.or);

        //making text scalable
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(upperText,5,30,2, TypedValue.COMPLEX_UNIT_SP);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(lowerText,5,30,2, TypedValue.COMPLEX_UNIT_SP);





        defaultQuestionsCount=0;
        funnyQuestionsCount=0;
        disturbingQuestionsCount=0;
        groseQuestionsCount=0;
        couplesQuestionsCount=0;


        for(int i=0;i<allquestions.size();i++){
            if(allquestions.get(i).getCategory().equals("default")){
                defaultQuestions.add(allquestions.get(i));
            }else if(allquestions.get(i).getCategory().equals("funny")){
                funnyQuestions.add(allquestions.get(i));
            }else if(allquestions.get(i).getCategory().equals("couples")){
                couplesQuestions.add(allquestions.get(i));
            }else if(allquestions.get(i).getCategory().equals("grose")){
                groseQuestions.add(allquestions.get(i));
            }else if(allquestions.get(i).getCategory().equals("disturbing")){
                disturbingQuestions.add(allquestions.get(i));
            }
        }



        final int answerUpColor = getResources().getColor(R.color.answer_up_color);
        final int answerDownColor = getResources().getColor(R.color.answer_down_color);


        if(newquestionscount>0) Toast.makeText(getApplicationContext(), newquestionscount+" νέες ερωτήσεις", Toast.LENGTH_LONG).show();

        SharedPreferences SP4 = getSharedPreferences("stats",MODE_PRIVATE);
        final float coeff = 100f*(1f/allquestions.size());
        max = 0;
        if(isDefault){
            runningQuestions.addAll(defaultQuestions);
        }
        if(isCouples){
            runningQuestions.addAll(couplesQuestions);
        }
        if(isDisturbing){
            runningQuestions.addAll(disturbingQuestions);
        }
        if(isFunny){
            runningQuestions.addAll(funnyQuestions);
        }
        if(isGrose){
            runningQuestions.addAll(groseQuestions);
        }


        globalI=nextQuestion();

        String[] qst =  runningQuestions.get(globalI).getQuestion().split("@",2);
        System.out.println(qst[0]);
        System.out.println(qst[1]);
        upperText.setText(qst[0]);
        lowerText.setText(qst[1]);
        currentQstUp=qst[0];
        currentQstDown=qst[1];
        //upperText.setShadowLayer(2, 2, 4, Color.BLACK);
        //lowerText.setShadowLayer(10, 2, 4, Color.BLACK);
        //globalI=nextQuestion(questions);


        or.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getPointerCount()>1){
                    or.setVisibility(View.VISIBLE);
                }
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //do something when pressed down
                    //Log.d("or", "or clicked");
                    or.setVisibility(View.INVISIBLE);



                    return true;
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    //do something when let go
                    or.setVisibility(View.VISIBLE);
                    or.performClick();

                    or.setColorFilter(null);
                    return true;
                }
                return false;
            }
        });

        or.setOnClickListener(view -> {

            Log.d("or","clicked");
            //manager.updateStats(this,"5d5bba8d6be1113f5413f601",0);
//            manager.getAllStats(this);
//            //manager.getStats(this,"5d5bba8d6be1113f5413f600");
//
//            int size = allquestions.size();
//            for(int i = 0; i<size;i++) {
//                System.out.println("stats "+allquestions.get(i).getStats().getMale0()+" "
//                        +allquestions.get(i).getStats().getFemale0()+" "
//                        +allquestions.get(i).getStats().getOther0()+" "
//                        +allquestions.get(i).getStats().getMale1()+" "
//                        +allquestions.get(i).getStats().getFemale1()+" "
//                        +allquestions.get(i).getStats().getOther1());
//            }

            Log.e("isDefault", String.valueOf(isDefault));
            Log.e("isFunny", String.valueOf(isFunny));
            Log.e("isDisturbing", String.valueOf(isDisturbing));
            Log.e("isGrose", String.valueOf(isGrose));
            Log.e("isCouples", String.valueOf(isCouples));

        });

        shareImage.setOnTouchListener((view, motionEvent) -> {
            //do something when pressed down
            //Log.d("or", "or clicked");
            if(motionEvent.getPointerCount()>1){
                shareImage.setVisibility(View.VISIBLE);
            }
            float x2,y2;
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                //do something when pressed down
                //Log.d("or", "or clicked");

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    // UI code goes here

                    //do something when pressed down
                    //Log.d("or", "or clicked");
                    shareImage.setVisibility(View.INVISIBLE);
                    shareImage2.setVisibility(View.VISIBLE);
                });
                //upperImage.setColorFilter(ctx.getResources().getColor(R.color.tint_blue),PorterDuff.Mode.MULTIPLY);
                xu=motionEvent.getX();
                yu=motionEvent.getY();
                return true;
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                //do something when let go
                x2=motionEvent.getX();
                y2=motionEvent.getY();
                //Log.d("absx ",Float.toString(abs(x2-xu)));
                //Log.d("absy ",Float.toString(abs(y2-yu)));

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        // UI code goes here
                        shareImage2.setVisibility(View.INVISIBLE);
                        shareImage.setVisibility(View.VISIBLE);




                    }
                });
                //do something when let go
                if((abs(x2-xu))>200||(abs(y2-yu))>200){
                    //upperImage.setColorFilter(null);
                    return true;
                }else {
                    shareImage.performClick();
                }
            }

            return false;
        });

        shareImage.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Bitmap bm = screenShot(getWindow().getDecorView().findViewById(android.R.id.content));
            File file = saveBitmap(bm, "mantis_image.png");
            Log.i("chase", "filepath: "+file.getAbsolutePath());
            Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out my app.");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "share via"));
        });

        commentImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getPointerCount()>1){
                    commentImage.setVisibility(View.VISIBLE);
                }
                //do something when pressed down
                //Log.d("or", "or clicked");
                float x2,y2;
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    //do something when pressed down
                    //Log.d("or", "or clicked");

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        // UI code goes here
                        commentImage.setVisibility(View.INVISIBLE);
                        commentImage2.setVisibility(View.VISIBLE);
                    });

                    xu=motionEvent.getX();
                    yu=motionEvent.getY();
                    return true;

                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    //do something when let go
                    x2=motionEvent.getX();
                    y2=motionEvent.getY();
                    //Log.d("absx ",Float.toString(abs(x2-xu)));
                    //Log.d("absy ",Float.toString(abs(y2-yu)));

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            // UI code goes here
                            commentImage2.setVisibility(View.INVISIBLE);
                            commentImage.setVisibility(View.VISIBLE);


                        }
                    });
                    //do something when let go
                    if((abs(x2-xu))>200||(abs(y2-yu))>200){

                        return true;
                    }else{
                        commentImage.performClick();
                    }
                }
                return false;
            }
        });

        commentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                for(int i = 0;i<bigTable.get(0).size();i++){
//                    GetComments gc = new GetComments();
//                    gc.execute(commentsUrl,Integer.toString(i));
//                }
                SharedPreferences SP35 = getSharedPreferences("questions", MODE_PRIVATE);
                Gson gson1 = new Gson();
                String json1 = gson1.toJson(allquestions);
                SP35.edit().putString("runningQuestions",json1).apply();

                Manager manager = new Manager();
                manager.getComments(context,runningQuestions.get(globalI).getId());
                Log.d("or","clicked");

                Intent i = new Intent(MainActivity.this,Comments.class);
                i.putExtra("id",runningQuestions.get(globalI).getId());
                i.putExtra("globalI",globalI);
                startActivity(i);
            }
        });

        upperImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent2) {
                float x2,y2;
                if(motionEvent2.getPointerCount()>1){
                    upperImage.setVisibility(View.VISIBLE);
                }
                if(motionEvent2.getAction() == MotionEvent.ACTION_DOWN ){
                    //do something when pressed down
                    //Log.d("or", "or clicked");


                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        // UI code goes here
                        upperImage.setVisibility(View.INVISIBLE);
                        upperImage2.setVisibility(View.VISIBLE);
                    });
                    //upperImage.setColorFilter(ctx.getResources().getColor(R.color.tint_blue),PorterDuff.Mode.MULTIPLY);
                    xu = motionEvent2.getX();
                    yu = motionEvent2.getY();
                    return true;
                }
                else if(motionEvent2.getAction() == MotionEvent.ACTION_UP ){
                    x2=motionEvent2.getX();
                    y2=motionEvent2.getY();
                    //Log.d("absx ",Float.toString(abs(x2-xu)));
                    //Log.d("absy ",Float.toString(abs(y2-yu)));

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            // UI code goes here
                            upperImage2.setVisibility(View.INVISIBLE);
                            upperImage.setVisibility(View.VISIBLE);
                        }
                    });
                    //do something when let go
                    if((abs(x2-xu))>200||(abs(y2-yu))>200){
                        //upperImage.setColorFilter(null);
                        return true;
                    }else{
                        upperImage.performClick();

                        //upperImage.setColorFilter(null);
                    }
                    return true;
                }

                return false;
            }
        });

        lowerImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float x2=0,y2=0;
                if(motionEvent.getPointerCount()>1){
                    lowerImage.setVisibility(View.VISIBLE);
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    //do something when pressed down
                    //Log.d("or", "or clicked");
                    //lowerImage.setImageResource(R.drawable.red_pressed);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            // UI code goes here
                            lowerImage.setVisibility(View.INVISIBLE);
                            lowerImage2.setVisibility(View.VISIBLE);
                        }
                    });

                    //lowerImage.setColorFilter(ctx.getResources().getColor(R.color.tint_red),PorterDuff.Mode.MULTIPLY);
                    x=motionEvent.getX();
                    y=motionEvent.getY();
                    return true;
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP ){
                    x2=motionEvent.getX();
                    y2=motionEvent.getY();
                    //Log.d("absx ",Float.toString(abs(x2-x)));
                    //Log.d("absy ",Float.toString(abs(y2-y)));
                    //lowerImage.setImageResource(R.drawable.red_unpressed);

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            // UI code goes here
                            lowerImage.setVisibility(View.VISIBLE);
                            lowerImage2.setVisibility(View.INVISIBLE);
                        }
                    });
                    //do something when let go
                    if((abs(x2-x))>200||(abs(y2-y))>200){
                        //lowerImage.setColorFilter(null);
                        return true;
                    }else{
                        lowerImage.performClick();

                        //lowerImage.setColorFilter(null);
                    }
                    return true;
                }
                return false;
            }
        });

        upperImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                buttonPressed=0;
                if(showstats==0) {
                    globalI=nextQuestion();

                    questionsTillAd--;
                    if(questionsTillAd<0){
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }
                        questionsTillAd=20;
                    }

                    Manager manager = new Manager();
                    if(genderS.equals("male")){
                        manager.updateStats(getApplicationContext(),runningQuestions.get(globalI).getId(),0);
                    }else if(genderS.equals("female")){
                        manager.updateStats(getApplicationContext(),runningQuestions.get(globalI).getId(),1);
                    }else{
                        manager.updateStats(getApplicationContext(),runningQuestions.get(globalI).getId(),2);
                    }

                    //System.out.println(other0);

                    String[] qst = runningQuestions.get(globalI).getQuestion().split("@", 2);
                    upperText.setText(qst[0]);
                    lowerText.setText(qst[1]);

                    currentQstUp=qst[0];
                    currentQstDown=qst[1];
                    System.out.println(qst[0]);
                    System.out.println(qst[1]);

                    manager.getStats(getApplicationContext(),runningQuestions.get(globalI).getId());

                    showstats=1;
                }else{
                    int male0i ,female0i ,other0i ,male1i ,female1i ,other1i;
                    Stats stats = runningQuestions.get(globalI).getStats();
                    male0i = stats.getMale0();
                    female0i = stats.getFemale0();
                    other0i = stats.getOther0();
                    male1i = stats.getMale1();
                    female1i = stats.getFemale1();
                    other1i = stats.getOther1();

                    float lowerstatsmale = (float) (male1i)/(float) (female1i+male1i+other1i);
                    float lowerstatfesmale = (float) (female1i)/(float) (female1i+male1i+other1i);
                    float lowerstatsother = (float) (other1i)/(float) (female1i+male1i+other1i);
                    float upperstatsmale = (float) (male0i)/(float) (female0i+male0i+other0i);
                    float upperstatfesmale = (float) (female0i)/(float) (female0i+male0i+other0i);
                    float upperstatsother = (float) (other0i)/(float) (female0i+male0i+other0i);
                    float lowerstats =(float)(male1i+female1i+other1i)/(float)(male0i+female0i+other0i+male1i+female1i+other1i);
                    float upperstats =(float)(male0i+female0i+other0i)/(float)(male0i+female0i+other0i+male1i+female1i+other1i);

                    int lowerstatsshow =  round(lowerstats*100);
                    int upperstatsshow =  round(upperstats*100);
                    int lowerstatsmaleshow =  round(lowerstatsmale*100);
                    int lowerstatsfemaleshow = round(lowerstatfesmale*100);
                    int lowerstatsothershow = round(lowerstatsother*100);
                    int upperstatsmaleshow = round(upperstatsmale*100);
                    int upperstatsfemaleshow = round(upperstatfesmale*100);
                    int upperstatsothershow = round(upperstatsother*100);

                    String lowerString1 = "Άνδρες "+Integer.toString(lowerstatsmaleshow)+"%"+"\nΓυναίκες "+Integer.toString(lowerstatsfemaleshow)+"%"+"\nΆλλο "+Integer.toString(lowerstatsothershow)+"%";
                    String lowerString2 = Integer.toString(lowerstatsshow)+"%"+"\n";
                    SpannableString ss1=  new SpannableString(lowerString2+lowerString1);
                    ss1.setSpan(new RelativeSizeSpan(0.6f), lowerString2.length(),lowerString1.length()+lowerString2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
                    SpannableString ss1qst = new SpannableString((currentQstDown+"\n"));
                    ss1qst.setSpan(new RelativeSizeSpan(0.8f),0,currentQstDown.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    System.out.println("male0 "+male0i);
                    System.out.println("male1 "+male1i);
                    System.out.println("female0 "+female0i);
                    System.out.println("female1 "+female1i);
                    System.out.println("other0 "+other0i);
                    System.out.println("other1 "+other1i);
                    String upperString1 ="Άνδρες "+Integer.toString(upperstatsmaleshow)+"%"+"\nΓυναίκες "+Integer.toString(upperstatsfemaleshow)+"%"+"\nΆλλο "+Integer.toString(upperstatsothershow)+"%";
                    String upperString2 =Integer.toString(upperstatsshow)+"%"+"\n";
                    SpannableString ss2=  new SpannableString(upperString2+upperString1);
                    ss2.setSpan(new RelativeSizeSpan(0.6f), upperString2.length(),upperString1.length()+upperString2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
                    SpannableString ss2qst = new SpannableString((currentQstUp+"\n"));
                    ss2qst.setSpan(new RelativeSizeSpan(0.8f),0,currentQstUp.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if(buttonPressed==0){
                        ss2qst.setSpan(new ForegroundColorSpan(answerUpColor),0,currentQstUp.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if(upperstatsshow>=lowerstatsshow){
                            sanity = sanity + coeff;
                            progress1.setProgress(sanity);
                            SP4.edit().putFloat("sanity",sanity).apply();
                            System.out.println("sanity "+sanity+" coeff "+coeff);
                            progress1.setProgress(sanity);
                        }else {
                            sanity = sanity - coeff;
                            progress1.setProgress(sanity);
                            SP4.edit().putFloat("sanity",sanity).apply();
                            System.out.println("sanity "+sanity+" coeff "+coeff);
                            progress1.setProgress(sanity);
                        }
                    }else{
                        ss1qst.setSpan(new ForegroundColorSpan(answerDownColor),0,currentQstDown.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if(upperstatsshow<=lowerstatsshow){
                            sanity = sanity + coeff;
                            progress1.setProgress(( sanity));
                            SP4.edit().putFloat("sanity", sanity).apply();
                            System.out.println("sanity "+sanity+" coeff "+coeff);
                            progress1.setProgress(sanity);
                        }else {
                            sanity = sanity - coeff;
                            progress1.setProgress(sanity);
                            SP4.edit().putFloat("sanity",sanity).apply();
                            System.out.println("sanity "+sanity+" coeff "+coeff);
                            progress1.setProgress(sanity);
                        }
                    }


                    lowerText.setText( TextUtils.concat(ss1qst, ss1));
                    //lowerText.setText(Integer.toString(lowerstatsshow)+"%"+"\n"+"Άνδρες "+Integer.toString(lowerstatsmaleshow)+"% ("+Integer.toString(male1i)+") \nΓυναίκες "+Integer.toString(lowerstatsfemaleshow)+"% ("+Integer.toString(female1i)+") \nΆλλο "+Integer.toString(lowerstatsothershow)+"% ("+Integer.toString(other1i)+")");
                    //upperText.setText( Integer.toString(upperstatsshow)+"%"+"\n"+"Άνδρες "+Integer.toString(upperstatsmaleshow)+"% ("+Integer.toString(male0i)+") \nΓυναίκες "+Integer.toString(upperstatsfemaleshow)+"% ("+Integer.toString(female0i)+") \nΆλλο "+Integer.toString(upperstatsothershow)+"% ("+Integer.toString(other0i)+")");
                    upperText.setText(TextUtils.concat(ss2qst, ss2));

                    showstats=0;
                }
            }
        });

        lowerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
                buttonPressed=1;
                if(showstats==0) {
                    globalI=nextQuestion();

                    questionsTillAd--;
                    if(questionsTillAd<0){
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }
                        questionsTillAd=20;
                    }



                    System.out.println("globalI stats "+globalI);
                    Manager manager = new Manager();
                    if(genderS.equals("male")){
                        manager.updateStats(getApplicationContext(),runningQuestions.get(globalI).getId(),3);
                    }else if(genderS.equals("female")){
                        manager.updateStats(getApplicationContext(),runningQuestions.get(globalI).getId(),4);
                    }else{
                        manager.updateStats(getApplicationContext(),runningQuestions.get(globalI).getId(),5);
                    }

                    String[] qst = runningQuestions.get(globalI).getQuestion().split("@", 2);
                    lowerText.setText(qst[1]);
                    upperText.setText(qst[0]);
                    currentQstUp=qst[0];
                    currentQstDown=qst[1];
                    System.out.println(qst[0]);
                    System.out.println(qst[1]);

                    showstats=1;
                }else{

                    int male0i ,female0i ,other0i ,male1i ,female1i ,other1i;
                    Stats stats = runningQuestions.get(globalI).getStats();
                    male0i = stats.getMale0();
                    female0i = stats.getFemale0();
                    other0i = stats.getOther0();
                    male1i = stats.getMale1();
                    female1i = stats.getFemale1();
                    other1i = stats.getOther1();

                    float lowerstatsmale = (float) (male1i)/(float) (female1i+male1i+other1i);
                    float lowerstatfesmale = (float) (female1i)/(float) (female1i+male1i+other1i);
                    float lowerstatsother = (float) (other1i)/(float) (female1i+male1i+other1i);
                    float upperstatsmale = (float) (male0i)/(float) (female0i+male0i+other0i);
                    float upperstatfesmale = (float) (female0i)/(float) (female0i+male0i+other0i);
                    float upperstatsother = (float) (other0i)/(float) (female0i+male0i+other0i);
                    float lowerstats =(float)(male1i+female1i+other1i)/(float)(male0i+female0i+other0i+male1i+female1i+other1i);
                    float upperstats =(float)(male0i+female0i+other0i)/(float)(male0i+female0i+other0i+male1i+female1i+other1i);
                    //System.out.println("lowerstats "+lowerstats);
                    int lowerstatsshow = round(lowerstats*100);
                    int upperstatsshow = round(upperstats*100);
                    int lowerstatsmaleshow = round(lowerstatsmale*100);
                    int lowerstatsfemaleshow = round(lowerstatfesmale*100);
                    int lowerstatsothershow = round(lowerstatsother*100);
                    int upperstatsmaleshow = round(upperstatsmale*100);
                    int upperstatsfemaleshow = round(upperstatfesmale*100);
                    int upperstatsothershow = round(upperstatsother*100);
                    String lowerString1 = "Άνδρες "+Integer.toString(lowerstatsmaleshow)+"%"+"\nΓυναίκες "+Integer.toString(lowerstatsfemaleshow)+"%"+"\nΆλλο "+Integer.toString(lowerstatsothershow)+"%";
                    String lowerString2 = Integer.toString(lowerstatsshow)+"%"+"\n";
                    SpannableString ss1=  new SpannableString(lowerString2+lowerString1);
                    ss1.setSpan(new RelativeSizeSpan(0.6f), lowerString2.length(),lowerString1.length()+lowerString2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
                    SpannableString ss1qst = new SpannableString((currentQstDown+"\n"));
                    ss1qst.setSpan(new RelativeSizeSpan(0.8f),0,currentQstDown.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    System.out.println("male0 "+male0i);
                    System.out.println("male1 "+male1i);
                    System.out.println("female0 "+female0i);
                    System.out.println("female1 "+female1i);
                    System.out.println("other0 "+other0i);
                    System.out.println("other1 "+other1i);

                    String upperString1 ="Άνδρες "+Integer.toString(upperstatsmaleshow)+"%"+"\nΓυναίκες "+Integer.toString(upperstatsfemaleshow)+"%"+"\nΆλλο "+Integer.toString(upperstatsothershow)+"%";
                    String upperString2 =Integer.toString(upperstatsshow)+"%"+"\n";
                    SpannableString ss2=  new SpannableString(upperString2+upperString1);
                    ss2.setSpan(new RelativeSizeSpan(0.6f), upperString2.length(),upperString1.length()+upperString2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
                    SpannableString ss2qst = new SpannableString((currentQstUp+"\n"));
                    ss2qst.setSpan(new RelativeSizeSpan(0.8f),0,currentQstUp.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if(buttonPressed==0){
                        ss2qst.setSpan(new ForegroundColorSpan(answerUpColor),0,currentQstUp.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if(upperstatsshow>=lowerstatsshow){
                            sanity = sanity + coeff;
                            progress1.setProgress(sanity);
                            SP4.edit().putFloat("sanity",sanity).apply();
                            System.out.println("sanity "+sanity);
                            progress1.setProgress(sanity);
                        }else {
                            sanity = sanity - coeff;
                            progress1.setProgress(sanity);
                            SP4.edit().putFloat("sanity",sanity).apply();
                            System.out.println("sanity "+sanity);
                            progress1.setProgress(sanity);
                        }
                    }else{
                        ss1qst.setSpan(new ForegroundColorSpan(answerDownColor),0,currentQstDown.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if(upperstatsshow<=lowerstatsshow){
                            sanity = sanity + coeff;
                            progress1.setProgress(sanity);
                            SP4.edit().putFloat("sanity",sanity).apply();
                            System.out.println("sanity "+sanity);
                            progress1.setProgress(sanity);
                        }else {
                            sanity = sanity - coeff;
                            progress1.setProgress(sanity);
                            SP4.edit().putFloat("sanity",sanity).apply();
                            System.out.println("sanity "+sanity);
                            progress1.setProgress(sanity);
                        }
                    }

                    lowerText.setText( TextUtils.concat(ss1qst, ss1));
                    //lowerText.setText(Integer.toString(lowerstatsshow)+"%"+"\n"+"Άνδρες "+Integer.toString(lowerstatsmaleshow)+"% ("+Integer.toString(male1i)+") \nΓυναίκες "+Integer.toString(lowerstatsfemaleshow)+"% ("+Integer.toString(female1i)+") \nΆλλο "+Integer.toString(lowerstatsothershow)+"% ("+Integer.toString(other1i)+")");
                    //upperText.setText( Integer.toString(upperstatsshow)+"%"+"\n"+"Άνδρες "+Integer.toString(upperstatsmaleshow)+"% ("+Integer.toString(male0i)+") \nΓυναίκες "+Integer.toString(upperstatsfemaleshow)+"% ("+Integer.toString(female0i)+") \nΆλλο "+Integer.toString(upperstatsothershow)+"% ("+Integer.toString(other0i)+")");
                    upperText.setText(TextUtils.concat(ss2qst, ss2));

                    //System.out.println("other0 "+other0i);
                    //System.out.println("other1 "+other1i);

                    showstats=0;

                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
                //TODO: make NavigationView compatible with android M
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                int id = item.getItemId();


                if(id== android.R.id.home){
                    mDrawerLayout.openDrawer(GravityCompat.START);


                if(first_open) {
                    if (isDefault) {
                        isDefault = false;
                        general_switch.setOn(false);
                        general_switch.performClick();
                    }
                    if (isCouples) {
                        isCouples = false;
                        couples_switch.setOn(false);
                        couples_switch.performClick();
                    }
                    if (isDisturbing) {
                        isDisturbing = false;
                        difficult_switch.setOn(false);
                        difficult_switch.performClick();
                    }
                    if (isFunny) {
                        isFunny = false;
                        funny_switch.setOn(false);
                        funny_switch.performClick();
                    }
                    if (isGrose) {
                        isGrose = false;
                        grose_switch.setOn(false);
                        grose_switch.performClick();
                    }

                    invalidateOptionsMenu();
                    first_open = false;
                }
                    return true;

                }



        return super.onOptionsItemSelected(item);
    }
    //close Drawer on Back Button press
    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences SP = getSharedPreferences("gameState",0);
        isDefault = SP.getBoolean("isDefault",true);
        isFunny = SP.getBoolean("isFunny",false);
        isDisturbing = SP.getBoolean("isDisturbing",false);
        isGrose = SP.getBoolean("isGrose",false);
        isCouples = SP.getBoolean("isCouples",false);

    }


    private Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private static File saveBitmap(Bitmap bm, String fileName){
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File dir = new File(path);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dir, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }




}
