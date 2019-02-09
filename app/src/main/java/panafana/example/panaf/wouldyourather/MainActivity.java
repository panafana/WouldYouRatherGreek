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
import android.os.AsyncTask;
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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.TextViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import static java.lang.Math.abs;
import static java.lang.Math.round;

//import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
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
    int max;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //MobileAds.initialize(this,"ca-app-pub-2471480338929808~1664063554");

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

        System.out.println("ad banner " + mAdView.isLoading());

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        accessWebService();
        GetStats g = new GetStats();
        g.execute(new String[]{getStatsUrl});
        SP2 = getSharedPreferences("gameState", MODE_PRIVATE);

        showstats = 1;
        SharedPreferences SP4 = getSharedPreferences("stats", MODE_PRIVATE);
        sanity = SP4.getFloat("sanity", 50.00f);

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
                        if(id>9){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mDrawerLayout.closeDrawers();
                                }
                            }, 200);

                        }

                        //noinspection SimplifiableIfStatement
                        if (id == R.id.reset) {
                            resetGameState();
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
                        } else if (id == R.id.logout) {
                            SharedPreferences SP = getSharedPreferences("user", MODE_PRIVATE);
                            SharedPreferences.Editor SPE = SP.edit();
                            SPE.clear();
                            SPE.apply();
                            Intent i = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }else if(id<10){



                        }

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;

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

        String[] uniqueCategoriesArray = getResources().getStringArray(R.array.categories);
        Menu menu = navigationView.getMenu();
        Menu submenuCategories = menu.addSubMenu(Menu.NONE,Menu.NONE,0,"Κατηγορίες ερωτήσεων");

        for(int i=0;i<uniqueCategoriesArray.length;i++){
            submenuCategories.add(Menu.NONE,i,Menu.NONE,(uniqueCategoriesArray[i])).setCheckable(true).setIcon(R.drawable.ic_check_box_outline_blank_black_24dp).setChecked(false).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    System.out.println(menuItem.toString());
                    if(menuItem.isChecked()){
                        menuItem.setIcon(R.drawable.ic_check_box_outline_blank_black_24dp);
                        switch (menuItem.toString()){
                            case "Γενικές":isDefault=false;
                                resetGameState();
                                break;
                            case "Αστείες":isFunny=false;
                                resetGameState();
                                break;
                            case "Ανησυχητικές":isDisturbing=false;
                                resetGameState();
                                break;
                            case "Αηδιαστικές": isGrose=false;
                                resetGameState();
                                break;
                        }
                        //menuItem.setChecked(false);
                        if(!isDefault&&!isFunny&&!isDisturbing&&!isGrose){
                            isDefault=true;
                            max=defaultQuestionsCount;
                            submenuCategories.getItem(0).setChecked(true).setIcon(R.drawable.ic_check_box_black_24dp);
                        }
                        System.out.println("max "+max);
                        System.out.println("default "+isDefault);
                        System.out.println("funny "+isFunny);
                        System.out.println("disturbing "+isDisturbing);
                        System.out.println("grose "+isGrose);
                    }else{

                        menuItem.setIcon(R.drawable.ic_check_box_black_24dp);
                        switch (menuItem.toString()){
                            case "Γενικές":isDefault=true;
                                resetGameState();
                                break;
                            case "Αστείες":isFunny=true;
                                resetGameState();
                                break;
                            case "Ανησυχητικές":isDisturbing=true;
                                resetGameState();
                                break;
                            case "Αηδιαστικές": isGrose=true;
                                resetGameState();
                                break;
                        }
                        System.out.println("max "+max);
                        System.out.println("default "+isDefault);
                        System.out.println("funny "+isFunny);
                        System.out.println("disturbing "+isDisturbing);
                        System.out.println("grose "+isGrose);
                        //menuItem.setChecked(true);
                    }
                    return false;
                }
            });
        }
        submenuCategories.getItem(0).setChecked(true).setIcon(R.drawable.ic_check_box_black_24dp);



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
        SP2 = getSharedPreferences("gameState", MODE_PRIVATE);
        SharedPreferences.Editor editor = SP2.edit();
        editor.putString("usedIds",null);
        editor.apply();
        editor.commit();
    }

    ArrayList<Integer> refreshStats(String key){
        SP = getSharedPreferences("questions", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = SP.getString(key, null);
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        ArrayList<Integer>  set = gson.fromJson(json, type);

        return set;

    }

    int nextQuestion(int maxQ){

        int counter=0;
        Gson gson = new Gson();
        String json = SP2.getString("usedIds", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> set = gson.fromJson(json, type);

        final int min = 0;
        final int maxi = maxQ;
        int random = new Random().nextInt((maxi - min) + 1) + min;
        System.out.println("random value "+random);
        if(json==null){
            return random;
        }

        while(set.contains(String.valueOf(random))){
            random = new Random().nextInt((maxi - min) + 1) + min;
            System.out.println("random value "+random);

            counter++;
            if(counter>maxi){
                Toast.makeText(ctx, "Τέλος ερωτήσεων", Toast.LENGTH_LONG).show();
                resetGameState();
                break;
            }
        }

        set.add(String.valueOf(random));

        Gson gson1 = new Gson();
        String json1 = gson1.toJson(set);


        SharedPreferences.Editor editor = SP2.edit();
        editor.putString("usedIds",json1);
        editor.apply();
        editor.commit();

        return random;


    }

    // Async Task to access the web
    private class JsonReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            SP = getSharedPreferences("questions", MODE_PRIVATE);

            String id;
            Gson gson4 = new Gson();
            //System.out.println(SP.getString("ids", null));
            if(SP.contains("ids")) {
                String json4 = SP.getString("ids", null);
                Type type4 = new TypeToken<ArrayList<String>>() {
                }.getType();
                ArrayList<String> set4 = gson4.fromJson(json4, type4);
                ArrayList<String> ids = new ArrayList<>(set4);
                if((ids.size() - 1)>0){
                    id = new String(ids.get(ids.size() - 1));
                }else{
                    id = "0";
                }

            }else {
                id = "0";
            }

            try {
                URL url = new URL(params[0]);
                //String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(mid), "UTF-8") +"&"+URLEncoder.encode("choice", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mchoice), "UTF-8")+"&"+URLEncoder.encode("male", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mmale), "UTF-8")+"&"+URLEncoder.encode("female", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mfemale), "UTF-8")+"&"+URLEncoder.encode("other", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mother), "UTF-8");
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("id", id);
                String query = builder.build().getEncodedQuery();

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);

                writer.flush();
                writer.close();
                os.close();
                conn.connect();


                InputStream IS = conn.getInputStream();
                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(IS, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                jsonResult = sb.toString();
                Log.d("result",jsonResult);
                System.out.println(new String(jsonResult.getBytes(),"UTF-8"));
                conn.disconnect();
            }
            catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ctx, "Connection Error", Toast.LENGTH_LONG).show());
                resetGameState();
                return null;
            }
            return null;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        protected void onPostExecute(String result) {
            makeLists();

            playGame();

        }
    }// end async task

    public void accessWebService() {
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(url);
    }

    // build hash set for list view
    public void makeLists() {
        //getting already stored questions
        SP = getSharedPreferences("questions", MODE_PRIVATE);
        ArrayList<String> questions = new ArrayList<>();
        ArrayList<String> category = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<Integer> males0 = new ArrayList<>();
        ArrayList<Integer> females0 = new ArrayList<>();
        ArrayList<Integer> others0 = new ArrayList<>();
        ArrayList<Integer> males1 = new ArrayList<>();
        ArrayList<Integer> females1 = new ArrayList<>();
        ArrayList<Integer> others1 = new ArrayList<>();
        Gson gson = new Gson();
        String json = SP.getString("questions", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> set = gson.fromJson(json, type);
        Gson gson3 = new Gson();
        String json3 = SP.getString("category", null);
        Type type3 = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> set3 = gson3.fromJson(json3, type3);
        Gson gson4 = new Gson();
        String json4 = SP.getString("ids", null);
        Type type4 = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> set4 = gson4.fromJson(json4, type4);
        Gson gson5 = new Gson();
        String json5 = SP.getString("male0", null);
        Type type5 = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        ArrayList<Integer> set5 = gson5.fromJson(json5, type5);
        Gson gson6 = new Gson();
        String json6 = SP.getString("female0", null);
        Type type6 = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        ArrayList<Integer> set6 = gson6.fromJson(json6, type6);
        Gson gson7 = new Gson();
        String json7 = SP.getString("other0", null);
        Type type7 = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        ArrayList<Integer> set7 = gson7.fromJson(json7, type7);
        Gson gson8 = new Gson();
        String json8 = SP.getString("male1", null);
        Type type8 = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        ArrayList<Integer> set8 = gson8.fromJson(json8, type8);
        Gson gson9 = new Gson();
        String json9 = SP.getString("female1", null);
        Type type9 = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        ArrayList<Integer> set9 = gson9.fromJson(json9, type9);
        Gson gson10 = new Gson();
        String json10 = SP.getString("other1", null);
        Type type10 = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        ArrayList<Integer> set10 = gson10.fromJson(json10, type10);

        if(set!=null) {
            questions = new ArrayList<>(set);
            category = new ArrayList<>(set3);
            ids = new ArrayList<>(set4);
            males0 = new ArrayList<>(set5);
            females0 = new ArrayList<>(set6);
            others0 = new ArrayList<>(set7);
            males1 = new ArrayList<>(set8);
            females1 = new ArrayList<>(set9);
            others1 = new ArrayList<>(set10);
        }
        //splitting Json data to variables and adding them to the array lists
        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("result");
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String qst = jsonChildNode.optString("question");
                String cat = jsonChildNode.optString("category");
                String id = jsonChildNode.optString("id");
                Integer male0 = jsonChildNode.optInt("male0");
                Integer female0 = jsonChildNode.optInt("female0");
                Integer other0 = jsonChildNode.optInt("other0");
                Integer male1 = jsonChildNode.optInt("male1");
                Integer female1 = jsonChildNode.optInt("female1");
                Integer other1 = jsonChildNode.optInt("other1");

                questions.add(qst);
                category.add(cat);
                ids.add(id);
                males0.add(male0);
                females0.add(female0);
                others0.add(other0);
                males1.add(male1);
                females1.add(female1);
                others1.add(other1);
                System.out.println("New data: "+ (i+1));
                newquestionscount=i+1;
            }

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error JSON Parser" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            Toast.makeText(this, "No internet", Toast.LENGTH_LONG).show();
        }
        //converting array lists back to Gson and storing
        SharedPreferences.Editor editor = SP.edit();

        ArrayList<String> set1 = new ArrayList<>(questions);
        ArrayList<String> set13 = new ArrayList<>(category);
        ArrayList<String> set14 = new ArrayList<>(ids);
        ArrayList<Integer> set15 = new ArrayList<>(males0);
        ArrayList<Integer> set16 = new ArrayList<>(females0);
        ArrayList<Integer> set17 = new ArrayList<>(others0);
        ArrayList<Integer> set18 = new ArrayList<>(males1);
        ArrayList<Integer> set19 = new ArrayList<>(females1);
        ArrayList<Integer> set110 = new ArrayList<>(others1);

        Gson gson1 = new Gson();
        String json1 = gson1.toJson(set1);
        Gson gson13 = new Gson();
        String json13 = gson13.toJson(set13);
        Gson gson14 = new Gson();
        String json14 = gson14.toJson(set14);
        Gson gson15 = new Gson();
        String json15 = gson15.toJson(set15);
        Gson gson16 = new Gson();
        String json16 = gson16.toJson(set16);
        Gson gson17 = new Gson();
        String json17 = gson17.toJson(set17);
        Gson gson18 = new Gson();
        String json18 = gson18.toJson(set18);
        Gson gson19 = new Gson();
        String json19 = gson19.toJson(set19);
        Gson gson110 = new Gson();
        String json110 = gson110.toJson(set110);

        editor.putString("questions", json1);
        editor.putString("category", json13);
        editor.putString("ids", json14);
        editor.putString("male0", json15);
        editor.putString("female0", json16);
        editor.putString("other0", json17);
        editor.putString("male1", json18);
        editor.putString("female1", json19);
        editor.putString("other1", json110);
        editor.apply();
        editor.commit();
        System.out.println("stored");
        //System.out.println("Messages: " + messages);
        //System.out.println("Signatures: " + signatures);
        //System.out.println("timestamps: " + timestamps);
        //System.out.println("ids: " + ids);
        //System.out.println("questions "+questions);
        //System.out.println("other0"+json17);
        //System.out.println("other1"+json110);

    }

    public void makeStatsLists(){
        SP = getSharedPreferences("questions", MODE_PRIVATE);
        ArrayList<Integer> males0 = new ArrayList<>();
        ArrayList<Integer> females0 = new ArrayList<>();
        ArrayList<Integer> others0 = new ArrayList<>();
        ArrayList<Integer> males1 = new ArrayList<>();
        ArrayList<Integer> females1 = new ArrayList<>();
        ArrayList<Integer> others1 = new ArrayList<>();
        //splitting Json data to variables and adding them to the array lists
        try {
            JSONObject jsonResponse = new JSONObject(jsonStatsResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("result");
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                Integer male0 = jsonChildNode.optInt("male0");
                Integer female0 = jsonChildNode.optInt("female0");
                Integer other0 = jsonChildNode.optInt("other0");
                Integer male1 = jsonChildNode.optInt("male1");
                Integer female1 = jsonChildNode.optInt("female1");
                Integer other1 = jsonChildNode.optInt("other1");
                males0.add(male0);
                females0.add(female0);
                others0.add(other0);
                males1.add(male1);
                females1.add(female1);
                others1.add(other1);
            }

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error JSON Parser" + e.toString(),
                    Toast.LENGTH_SHORT).show();
            Log.d("error JSON",e.toString());
        } catch (NullPointerException e) {
            Toast.makeText(this, "No internet", Toast.LENGTH_LONG).show();
        }
        //converting array lists back to Gson and storing
        SharedPreferences.Editor editor = SP.edit();

        ArrayList<Integer> set15 = new ArrayList<>(males0);
        ArrayList<Integer> set16 = new ArrayList<>(females0);
        ArrayList<Integer> set17 = new ArrayList<>(others0);
        ArrayList<Integer> set18 = new ArrayList<>(males1);
        ArrayList<Integer> set19 = new ArrayList<>(females1);
        ArrayList<Integer> set110 = new ArrayList<>(others1);

        Gson gson15 = new Gson();
        String json15 = gson15.toJson(set15);
        Gson gson16 = new Gson();
        String json16 = gson16.toJson(set16);
        Gson gson17 = new Gson();
        String json17 = gson17.toJson(set17);
        Gson gson18 = new Gson();
        String json18 = gson18.toJson(set18);
        Gson gson19 = new Gson();
        String json19 = gson19.toJson(set19);
        Gson gson110 = new Gson();
        String json110 = gson110.toJson(set110);

        editor.putString("male0", json15);
        editor.putString("female0", json16);
        editor.putString("other0", json17);
        editor.putString("male1", json18);
        editor.putString("female1", json19);
        editor.putString("other1", json110);
        editor.apply();
        editor.commit();
        System.out.println("stored");
        //System.out.println("other0"+ json17);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void playGame(){
        SP = getSharedPreferences("questions", MODE_PRIVATE);
        SharedPreferences SP3 = getSharedPreferences("gender",MODE_PRIVATE);
        final String genderS = SP3.getString("gender","other");
        Log.d("saved ", String.valueOf(SP.getAll()));
        final SharedPreferences.Editor editor = SP2.edit();
        Gson gson = new Gson();
        String json = SP.getString("questions", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String>  set = gson.fromJson(json, type);
        Gson gson2 = new Gson();
        String json2 = SP.getString("category", null);
        Type type2 = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String>  set2 = gson2.fromJson(json2, type2);
        Gson gson3 = new Gson();
        String json3 = SP.getString("ids", null);
        Type type3 = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String>  set3 = gson3.fromJson(json3, type3);

        final ArrayList<String> questions = new ArrayList<>(set);
        ArrayList<String> categories = new ArrayList<>(set2);
        ArrayList<String> ids = new ArrayList<>(set3);

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

        ArrayList<ArrayList<String>> bigTable = new ArrayList<>();
        bigTable.add(ids);
        bigTable.add(questions);
        bigTable.add(categories);




        for(int i=0;i<bigTable.get(0).size();i++){
            if(bigTable.get(2).get(i).equals("default")){
                defaultQuestionsCount++;
            }else if(bigTable.get(2).get(i).equals("funny")){
                funnyQuestionsCount++;
            }else if(bigTable.get(2).get(i).equals("disturbing")){
                disturbingQuestionsCount++;
            }else if(bigTable.get(2).get(i).equals("grose")){
                groseQuestionsCount++;
            }
        }

        System.out.println("default questions "+defaultQuestionsCount);
        System.out.println("funny questions "+funnyQuestionsCount);
        System.out.println("disturbing questions "+disturbingQuestionsCount);
        System.out.println("grose questions "+groseQuestionsCount);



        final int answerUpColor = getResources().getColor(R.color.answer_up_color);
        final int answerDownColor = getResources().getColor(R.color.answer_down_color);


        if(newquestionscount>0) Toast.makeText(getApplicationContext(), newquestionscount+" νέες ερωτήσεις", Toast.LENGTH_LONG).show();

        SharedPreferences SP4 = getSharedPreferences("stats",MODE_PRIVATE);
        final float coeff = 100f*(1f/questions.size());
        max=defaultQuestionsCount+funnyQuestionsCount+groseQuestionsCount+disturbingQuestionsCount-1;


        globalI=nextQuestion(max);
        while(true){
            String category = bigTable.get(2).get(globalI);
            if((isDefault&&category.equals("default"))||(isDisturbing&&category.equals("disturbing"))||(isFunny&&category.equals("funny"))||(isGrose&&category.equals("grose"))){
                break;
            }else{
                globalI=nextQuestion(max);
            }
        }
        System.out.println("category "+bigTable.get(2).get(globalI));
        System.out.println("first gloabI "+globalI);

        String[] qst = bigTable.get(1).get(globalI).split("@",2);
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

        or.setOnClickListener(view -> Log.d("or","clicked"));

        shareImage.setOnTouchListener((view, motionEvent) -> {
                //do something when pressed down
                //Log.d("or", "or clicked");
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
                Log.d("or","clicked");
                GetComments gc = new GetComments();
                gc.execute(commentsUrl,Integer.toString(globalI));
            }
        });

        upperImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent2) {
                float x2,y2;
                if(motionEvent2.getAction() == MotionEvent.ACTION_DOWN){
                    //do something when pressed down
                    //Log.d("or", "or clicked");

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        // UI code goes here
                        upperImage.setVisibility(View.INVISIBLE);
                        upperImage2.setVisibility(View.VISIBLE);
                    });
                    //upperImage.setColorFilter(ctx.getResources().getColor(R.color.tint_blue),PorterDuff.Mode.MULTIPLY);
                    xu=motionEvent2.getX();
                    yu=motionEvent2.getY();
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


                ArrayList<Integer> male0 = new ArrayList<>(refreshStats("male0"));
                ArrayList<Integer> female0 = new ArrayList<>(refreshStats("female0"));
                ArrayList<Integer> other0 = new ArrayList<>(refreshStats("other0"));
                ArrayList<Integer> male1 = new ArrayList<>(refreshStats("male1"));
                ArrayList<Integer> female1 = new ArrayList<>(refreshStats("female1"));
                ArrayList<Integer> other1 = new ArrayList<>(refreshStats("other1"));
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
                    /*
                    max=0;
                    if(isDefault){
                        max+=defaultQuestionsCount;
                    }
                    if(isFunny){
                        max+=funnyQuestionsCount;
                    }
                    if(isGrose){
                        max+=groseQuestionsCount;
                    }
                    if(isDisturbing){
                        max+=disturbingQuestionsCount;
                    }
                    */
                    System.out.println("max "+max);
                    while(true){
                        String category = bigTable.get(2).get(globalI);
                        if((isDefault&&category.equals("default"))||(isDisturbing&&category.equals("disturbing"))||(isFunny&&category.equals("funny"))||(isGrose&&category.equals("grose"))){
                            break;
                        }else{
                            globalI=nextQuestion(max);
                        }
                    }
                    System.out.println("category "+bigTable.get(2).get(globalI));

                    System.out.println("globalI stats "+globalI);
                    if(genderS.equals("male")){
                        submitStats ss = new submitStats(globalI+1, 0, 1, 0, 0);
                        ss.execute((Void) null);
                    }else if(genderS.equals("female")){
                        submitStats ss = new submitStats(globalI+1, 0, 0, 1, 0);
                        ss.execute((Void) null);
                    }else{
                        submitStats ss = new submitStats(globalI+1, 0, 0, 0, 1);
                        ss.execute((Void) null);
                    }
                    GetStats gs = new GetStats();
                    gs.execute(getStatsUrl);
                    //System.out.println(other0);


                        String[] qst = bigTable.get(1).get(globalI).split("@", 2);
                        upperText.setText(qst[0]);
                        lowerText.setText(qst[1]);

                        currentQstUp=qst[0];
                        currentQstDown=qst[1];
                        System.out.println(qst[0]);
                        System.out.println(qst[1]);

                    showstats=1;
                }else{


                    int male0i ,female0i ,other0i ,male1i ,female1i ,other1i;

                    try{
                        male0i =(male0.get(globalI));
                    }catch (IndexOutOfBoundsException e){
                        globalI=0;
                        male0i =(male0.get(globalI));
                    }
                    female0i=(female0.get(globalI));
                    other0i = (other0.get(globalI));
                    male1i=(male1.get(globalI));
                    female1i=(female1.get(globalI));
                    other1i=(other1.get(globalI));

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
                ArrayList<Integer> male0 = new ArrayList<>(refreshStats("male0"));
                ArrayList<Integer> female0 = new ArrayList<>(refreshStats("female0"));
                ArrayList<Integer> other0 = new ArrayList<>(refreshStats("other0"));
                ArrayList<Integer> male1 = new ArrayList<>(refreshStats("male1"));
                ArrayList<Integer> female1 = new ArrayList<>(refreshStats("female1"));
                ArrayList<Integer> other1 = new ArrayList<>(refreshStats("other1"));
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
                    /*
                    max=0;
                    if(isDefault){
                        max+=defaultQuestionsCount;
                    }
                    if(isFunny){
                        max+=funnyQuestionsCount;
                    }
                    if(isGrose){
                        max+=groseQuestionsCount;
                    }
                    if(isDisturbing){
                        max+=disturbingQuestionsCount;
                    }
                    */
                    System.out.println("max "+max);

                    while(true){
                        String category = bigTable.get(2).get(globalI);
                        if((isDefault&&category.equals("default"))||(isDisturbing&&category.equals("disturbing"))||(isFunny&&category.equals("funny"))||(isGrose&&category.equals("grose"))){
                            break;
                        }else{
                            globalI=nextQuestion(max);
                        }
                    }
                    System.out.println("category "+bigTable.get(2).get(globalI));

                    System.out.println("globalI stats "+globalI);
                    if(genderS.equals("male")){
                        submitStats ss = new submitStats(globalI+1, 1, 1, 0, 0);
                        ss.execute((Void) null);
                    }else if(genderS.equals("female")){
                        submitStats ss = new submitStats(globalI+1, 1, 0, 1, 0);
                        ss.execute((Void) null);
                    }else{
                        submitStats ss = new submitStats(globalI+1, 0, 0, 0, 1);
                        ss.execute((Void) null);
                    }
                    GetStats gs = new GetStats();
                    gs.execute(new String[] {getStatsUrl});

                        String[] qst = bigTable.get(1).get(globalI).split("@", 2);
                        lowerText.setText(qst[1]);
                        upperText.setText(qst[0]);
                        currentQstUp=qst[0];
                        currentQstDown=qst[1];
                        System.out.println(qst[0]);
                        System.out.println(qst[1]);

                    showstats=1;
                }else{

                    int male0i = 0,female0i ,other0i ,male1i ,female1i ,other1i;
                    try{
                        male0i =(male0.get(globalI));
                    }catch (IndexOutOfBoundsException e){
                        globalI=0;
                        male0i =(male0.get(globalI));
                    }

                    female0i=(female0.get(globalI));
                    other0i = (other0.get(globalI));
                    male1i=(male1.get(globalI));
                    female1i=(female1.get(globalI));
                    other1i=(other1.get(globalI));

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

    private class GetComments extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String id = params[1];
            SP = getSharedPreferences("comments", MODE_PRIVATE);
            System.out.println("comment id "+id);
            try {

                URL url = new URL(params[0]);
                //String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(mid), "UTF-8") +"&"+URLEncoder.encode("choice", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mchoice), "UTF-8")+"&"+URLEncoder.encode("male", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mmale), "UTF-8")+"&"+URLEncoder.encode("female", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mfemale), "UTF-8")+"&"+URLEncoder.encode("other", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mother), "UTF-8");
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("question", id);

                String query = builder.build().getEncodedQuery();

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);

                writer.flush();
                writer.close();
                os.close();
                conn.connect();

                /*
                List<NameValuePair> sendparams = new ArrayList<>();
                sendparams.add(new BasicNameValuePair("question",id));
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(sendparams, HTTP.UTF_8);
                httppost.setEntity(ent);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                */
                InputStream IS = conn.getInputStream();
                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(IS, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                jsonCommentResult = sb.toString();
                System.out.println("json Comments "+jsonCommentResult);
                conn.disconnect();
                //System.out.println(new String(jsonStatsResult.getBytes(), "UTF-8"));
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ctx, "Connection Error", Toast.LENGTH_LONG).show());
                return "error";
            }
            return id;
        }

        @Override
        protected void onPostExecute(String result) {
            if(!(result.equals("error"))){
                Log.d("comments",jsonCommentResult);
                System.out.println("comments "+jsonCommentResult);
                Intent i = new Intent(MainActivity.this,Comments.class);
                i.putExtra("comments",jsonCommentResult);
                i.putExtra("question",Integer.valueOf(result));
                startActivity(i);
            }

        }
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

    public class submitStats extends AsyncTask<Void, Void, String> {

        private  int mid;
        private  int mchoice;
        private  int mmale ;
        private  int mfemale;
        private  int mother;

        submitStats(int id,int choice, int male,int female,int other) {
            mid = id;
            mchoice = choice;
            mmale = male;
            mfemale = female;
            mother = other;
        }

        @Override
        protected String doInBackground(Void... params) {
            String reg_url = "http://83.212.84.230/sendstats.php";
            String response = null;
            try {

                URL url = new URL(reg_url);
                //String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(mid), "UTF-8") +"&"+URLEncoder.encode("choice", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mchoice), "UTF-8")+"&"+URLEncoder.encode("male", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mmale), "UTF-8")+"&"+URLEncoder.encode("female", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mfemale), "UTF-8")+"&"+URLEncoder.encode("other", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mother), "UTF-8");
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("id", String.valueOf(mid))
                        .appendQueryParameter("choice", String.valueOf(mchoice))
                        .appendQueryParameter("male", String.valueOf(mmale))
                        .appendQueryParameter("female", String.valueOf(mfemale))
                        .appendQueryParameter("other", String.valueOf(mother));
                String query = builder.build().getEncodedQuery();

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);

                writer.flush();
                writer.close();
                os.close();
                conn.connect();
                int responseCode=conn.getResponseCode();

                /*
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                //httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                */
                InputStream IS = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(IS));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                // Pass data to onPostExecute method
                String r =(result.toString());
                IS.close();

                //Log.d("ResponseStats", httpURLConnection.getResponseMessage());
                Log.d("ResponseStats", r);
                response=r;
                conn.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                resetGameState();
                return "Failed";
            }
            if(response.contains("Success")){
                return "Success";
            }else{
                return "Invalid";
            }
        }

        @Override
        protected void onPostExecute(final String success) {

        }

    }


    private class GetStats extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            SP = getSharedPreferences("questions", MODE_PRIVATE);

            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                //writer.write(query);

                writer.flush();
                writer.close();
                os.close();
                conn.connect();

                /*
                List<NameValuePair> sendparams = new ArrayList<>();
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(sendparams, HTTP.UTF_8);
                httppost.setEntity(ent);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                */
                InputStream IS = conn.getInputStream();
                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(IS, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                jsonStatsResult = sb.toString();
                conn.disconnect();
                //Log.d("result", jsonStatsResult);
                //System.out.println(new String(jsonStatsResult.getBytes(), "UTF-8"));
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ctx, "Connection Error", Toast.LENGTH_LONG).show());
                resetGameState();
                return "error";
            }
            return "success";
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("success")) {
                makeStatsLists();
            }
        }

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
