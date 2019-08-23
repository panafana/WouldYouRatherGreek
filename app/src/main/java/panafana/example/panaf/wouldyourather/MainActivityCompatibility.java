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
import android.view.Menu;
import android.view.MenuInflater;
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
import androidx.core.widget.TextViewCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

import panafana.example.panaf.wouldyourather.models.Question;
import panafana.example.panaf.wouldyourather.models.Stats;
import panafana.example.panaf.wouldyourather.utils.Manager;

import static java.lang.Math.abs;
import static java.lang.Math.round;

//import com.google.android.material.navigation.NavigationView;

public class MainActivityCompatibility extends AppCompatActivity {
    SharedPreferences SP;
    SharedPreferences SP2;
    Context ctx = this;
    private String jsonResult;
    private String jsonStatsResult;
    private String jsonCommentResult;
    private String url = "http://83.212.84.230/getquestions.php";
    private String getStatsUrl ="http://83.212.84.230/getstats.php";
    private String commentsUrl ="http://83.212.84.230/getcomments.php";
    int globalI =0,newquestionscount=0,questionsTillAd=20;
    int showstats,buttonPressed=0;
    float x=0,y=0,xu=0,yu=0;
    String currentQstUp,currentQstDown;
    int MY_PERMISSIONS_REQUEST_READ_STORAGE=0;
    private AdView mAdView;
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
    Manager manager;
    MenuItem submenuCategories;
    Context context;
    private FirebaseAnalytics mFirebaseAnalytics;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_compatibility);
        context = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //MobileAds.initialize(this,"ca-app-pub-2471480338929808~1664063554");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.LEVEL, "main");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LEVEL_START, bundle);

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

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        //change action bar text color
        //getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.app_name) + "</font>"));
        manager = new Manager();
        manager.getQuestions(this,false);

        SP2 = getSharedPreferences("gameState",MODE_PRIVATE);
        globalI=SP2.getInt("state",0);
        showstats=1;


        //permissions
        if (ContextCompat.checkSelfPermission(MainActivityCompatibility.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivityCompatibility.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Αδεια χρήσης χώρου αποθήκευσης");
                alertBuilder.setMessage("Η άδεια αυτή χρησιμοποιείται μόνο στην λειτουργία του πλήκτρου share");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivityCompatibility.this,
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
        SP2 = getSharedPreferences("gameState", MODE_PRIVATE);
        SharedPreferences.Editor editor = SP2.edit();
        editor.putString("usedIds",null);
        editor.apply();
        editor.commit();
    }


    int nextQuestion(int maxQ){
        Gson gson = new Gson();
        String json = SP2.getString("usedIds", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> set = gson.fromJson(json, type);

        final int min = 0;
        final int maxi = maxQ;
        int random=0 ;
        try{
            random  = new Random().nextInt((maxi - min) + 1) + min;

        }catch (IllegalArgumentException e){
            random=0;
        }
        System.out.println("random value "+random);
        if(json==null){
            ArrayList<String> set2 = new ArrayList<>();
            set2.add(String.valueOf(random));
            Gson gson1 = new Gson();
            String json1 = gson1.toJson(set2);
            SharedPreferences.Editor editor = SP2.edit();
            editor.putString("usedIds",json1);
            editor.apply();
            editor.commit();
            return random;
        }

        while(set.contains(String.valueOf(random))){
            random = new Random().nextInt((maxi - min) + 1) + min;
            System.out.println("random value "+random);

            if(set.size()>maxi){
                Toast.makeText(ctx, "Τέλος ερωτήσεων", Toast.LENGTH_LONG).show();
                resetGameState();
                break;
            }
        }

        set.add(String.valueOf(random));
//        for(int i=0;i<set.size();i++){
//            System.out.println("UsedQ "+set.get(i));
//        }

        Gson gson1 = new Gson();
        String json1 = gson1.toJson(set);
        SharedPreferences.Editor editor = SP2.edit();
        editor.putString("usedIds",json1);
        editor.apply();
        editor.commit();
        return random;

    }

    // Async Task to access the web



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
        ArrayList<Question> allquestions = gson33.fromJson(temp, type33);



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


        SharedPreferences SP5 = getSharedPreferences("categories", MODE_PRIVATE);
        isDefault = SP5.getBoolean("isDefault",true);
        isFunny = SP5.getBoolean("isFunny",false);
        isDisturbing = SP5.getBoolean("isDisturbing",false);
        isGrose = SP5.getBoolean("isGrose",false);
        isCouples = SP5.getBoolean("isCouples",false);
        isNSFW = SP5.getBoolean("isNSFW",false);


        defaultQuestionsCount=0;
        funnyQuestionsCount=0;
        disturbingQuestionsCount=0;
        groseQuestionsCount=0;
        couplesQuestionsCount=0;


        for(int i=0;i<allquestions.size();i++){
            if(allquestions.get(i).getCategory().equals("default")){
                defaultQuestionsCount++;
            }else if(allquestions.get(i).getCategory().equals("funny")){
                funnyQuestionsCount++;
            }else if(allquestions.get(i).getCategory().equals("couples")){
                couplesQuestionsCount++;
            }
        }

        System.out.println("default questions "+defaultQuestionsCount);
        System.out.println("funny questions "+funnyQuestionsCount);
        System.out.println("disturbing questions "+disturbingQuestionsCount);
        System.out.println("grose questions "+groseQuestionsCount);
        System.out.println("couples questions "+couplesQuestionsCount);



        final int answerUpColor = getResources().getColor(R.color.answer_up_color);
        final int answerDownColor = getResources().getColor(R.color.answer_down_color);


        if(newquestionscount>0) Toast.makeText(getApplicationContext(), newquestionscount+" νέες ερωτήσεις", Toast.LENGTH_LONG).show();

        SharedPreferences SP4 = getSharedPreferences("stats",MODE_PRIVATE);
        final float coeff = 100f*(1f/allquestions.size());
        max=defaultQuestionsCount+funnyQuestionsCount+groseQuestionsCount+disturbingQuestionsCount+couplesQuestionsCount-2;


        globalI=nextQuestion(max);
        while(true){
            String category = allquestions.get(globalI).getCategory();
            if((isDefault&&category.equals("default"))||(isDisturbing&&category.equals("disturbing"))||(isFunny&&category.equals("funny"))||(isGrose&&category.equals("grose"))||(isCouples&&category.equals("couples"))){
                break;
            }else{
                globalI=nextQuestion(max);
            }
        }
        System.out.println("category "+ allquestions.get(globalI).getCategory());
        System.out.println("first gloabI "+globalI);

        String[] qst =  allquestions.get(globalI).getQuestion().split("@",2);
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



                Manager manager = new Manager();
                manager.getComments(context,allquestions.get(globalI).getId());
                Log.d("or","clicked");

                Intent i = new Intent(MainActivityCompatibility.this,Comments.class);
                i.putExtra("id",allquestions.get(globalI).getId());
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
                    globalI=nextQuestion(max);

                    questionsTillAd--;
                    if(questionsTillAd<0){
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }
                        questionsTillAd=20;
                    }

                    System.out.println("max "+max);
                    while(true){
                        String category =  allquestions.get(globalI).getCategory();
                        if((isDefault&&category.equals("default"))||(isDisturbing&&category.equals("disturbing"))||(isFunny&&category.equals("funny"))||(isGrose&&category.equals("grose"))||(isCouples&&category.equals("couples"))){
                            break;
                        }else{
                            globalI=nextQuestion(max);
                        }
                    }
                    System.out.println("category "+ allquestions.get(globalI).getCategory());

                    System.out.println("globalI stats "+globalI);

                    Manager manager = new Manager();
                    if(genderS.equals("male")){
                        manager.updateStats(getApplicationContext(),allquestions.get(globalI).getId(),0);
                    }else if(genderS.equals("female")){
                        manager.updateStats(getApplicationContext(),allquestions.get(globalI).getId(),1);
                    }else{
                        manager.updateStats(getApplicationContext(),allquestions.get(globalI).getId(),2);
                    }

                    //System.out.println(other0);


                    String[] qst = allquestions.get(globalI).getQuestion().split("@", 2);
                    upperText.setText(qst[0]);
                    lowerText.setText(qst[1]);

                    currentQstUp=qst[0];
                    currentQstDown=qst[1];
                    System.out.println(qst[0]);
                    System.out.println(qst[1]);

                    manager.getStats(getApplicationContext(),allquestions.get(globalI).getId());

                    showstats=1;
                }else{
                    int male0i ,female0i ,other0i ,male1i ,female1i ,other1i;
                    Stats stats = allquestions.get(globalI).getStats();
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

                    }else{
                        ss1qst.setSpan(new ForegroundColorSpan(answerDownColor),0,currentQstDown.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

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
                    globalI=nextQuestion(max);

                    questionsTillAd--;
                    if(questionsTillAd<0){
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }
                        questionsTillAd=20;
                    }

                    System.out.println("max "+max);

                    while(true){
                        //String category = bigTable.get(2).get(globalI);
                        String category =  allquestions.get(globalI).getCategory();
                        if((isDefault&&category.equals("default"))||(isDisturbing&&category.equals("disturbing"))||(isFunny&&category.equals("funny"))||(isGrose&&category.equals("grose"))||(isCouples&&category.equals("couples"))){
                            break;
                        }else{
                            globalI=nextQuestion(max);
                        }
                    }

                    System.out.println("globalI stats "+globalI);
                    Manager manager = new Manager();
                    if(genderS.equals("male")){
                        manager.updateStats(getApplicationContext(),allquestions.get(globalI).getId(),3);
                    }else if(genderS.equals("female")){
                        manager.updateStats(getApplicationContext(),allquestions.get(globalI).getId(),4);
                    }else{
                        manager.updateStats(getApplicationContext(),allquestions.get(globalI).getId(),5);
                    }

                    String[] qst = allquestions.get(globalI).getQuestion().split("@", 2);
                    lowerText.setText(qst[1]);
                    upperText.setText(qst[0]);
                    currentQstUp=qst[0];
                    currentQstDown=qst[1];
                    System.out.println(qst[0]);
                    System.out.println(qst[1]);

                    showstats=1;
                }else{

                    int male0i ,female0i ,other0i ,male1i ,female1i ,other1i;
                    Stats stats = allquestions.get(globalI).getStats();
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
                    }else{
                        ss1qst.setSpan(new ForegroundColorSpan(answerDownColor),0,currentQstDown.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        submenuCategories = menu.add("Κατηγορίες ερωτήσεων");


        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        //TODO: make NavigationView compatible with android M
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean[] isTable={isDefault,isFunny,isDisturbing,isCouples,isGrose};
        if (id == R.id.reset) {
            globalI = 0;
            SP2 = getSharedPreferences("gameState", MODE_PRIVATE);
            SharedPreferences.Editor editor = SP2.edit();
            editor.putInt("state", 0);
            editor.apply();
            editor.commit();
        } else if (id == R.id.submitQuestion) {
            Intent i = new Intent(MainActivityCompatibility.this, SubmitQuestion.class);
            startActivity(i);
        } else if (id == R.id.changeTextColor) {
            TextView upperText = findViewById(R.id.textViewUp);
            TextView lowerText = findViewById(R.id.textViewDown);
            if (upperText.getCurrentTextColor() == -16777216 || upperText.getCurrentTextColor() == -13355980) {
                upperText.setTextColor(Color.WHITE);
                lowerText.setTextColor(Color.WHITE);
            } else {
                upperText.setTextColor(Color.BLACK);
                lowerText.setTextColor(Color.BLACK);
            }
        }else if (id==R.id.logout){
            SharedPreferences SP =getSharedPreferences("user",MODE_PRIVATE);
            SharedPreferences.Editor SPE = SP.edit();
            SPE.clear();
            SPE.apply();
            Intent i = new Intent(MainActivityCompatibility.this,LoginActivity.class);
            startActivity(i);
            finish();
        }else if(item.getTitle().equals("Κατηγορίες ερωτήσεων")){
            String[] uniqueCategoriesArray = getResources().getStringArray(R.array.categories);
            System.out.println("length" +uniqueCategoriesArray.length);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Επιλογή κατηγορίας");
            builder.setMultiChoiceItems(uniqueCategoriesArray, isTable, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    System.out.println("i "+i+" b "+b);
                    switch (i){
                        case 0:if(b){
                            isDefault=true;
                        }else{
                            isDefault=false;
                        }break;
                        case 1:if(b){
                            isFunny=true;
                        }else{
                            isFunny=false;
                        }break;
                        case 2:if(b) {
                            isDisturbing = true;
                        }else {
                            isDisturbing = false;
                        }break;
                        case 3:if(b) {
                            isCouples = true;
                        }else {
                            isCouples = false;
                        }break;
                        case 4:if(b) {
                            isGrose = true;
                        }else {
                            isGrose = false;
                        }break;

                    }
                    if(!isDefault&&!isFunny&&!isDisturbing&&!isGrose&&!isCouples){
                        isDefault=true;
                    }
                }
            });

            builder.show();



        }
        return super.onOptionsItemSelected(item);
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
