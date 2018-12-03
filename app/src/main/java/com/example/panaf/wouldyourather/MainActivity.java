package com.example.panaf.wouldyourather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity {
    SharedPreferences SP;
    SharedPreferences SP2;

    Context ctx = this;
    private String jsonResult;
    private String jsonStatsResult;
    private String url = "http://83.212.84.230/getquestions.php";
    private String getStatsUrl ="http://83.212.84.230/getstats.php";
    int globalI =0;
    int showstats;
    float x=0,y=0,xu=0,yu=0;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accessWebService();
        SP2 = getSharedPreferences("gameState",MODE_PRIVATE);
        globalI=SP2.getInt("state",0);
        showstats=1;
        final ImageView or = findViewById(R.id.or);
        or.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //do something when pressed down
                    //Log.d("or", "or clicked");
                    or.setImageResource(R.drawable.button_pressed);
                    or.setColorFilter(ctx.getResources().getColor(R.color.tint_whitee),PorterDuff.Mode.MULTIPLY);
                    return true;
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    //do something when let go
                    or.setImageResource(R.drawable.button_unpressed);
                    or.performClick();
                    or.setColorFilter(null);
                    return true;
                }
                return false;
            }
        });
        or.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("or","clicked");
            }
        });
    }

    ArrayList<Integer> refreshStats(String key){
        SP = getSharedPreferences("questions", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = SP.getString(key, null);
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        ArrayList<Integer>  set = gson.fromJson(json, type);

        return set;

    }

    void nextQuestion(){
        SP2 = getSharedPreferences("gameState",MODE_PRIVATE);
        globalI=SP2.getInt("state",0);
        globalI++;
        SharedPreferences.Editor editor = SP2.edit();
        editor.putInt("state",globalI);
        editor.apply();
        editor.commit();

        Log.d("i",String.valueOf(globalI));
    }

    // Async Task to access the web
    private class JsonReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
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
                List<NameValuePair> sendparams = new ArrayList<>();
                Log.d("id sent",id);
                sendparams.add(new BasicNameValuePair("id", id));
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(sendparams, HTTP.UTF_8);
                httppost.setEntity(ent);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                jsonResult = sb.toString();
                Log.d("result",jsonResult);
                System.out.println(new String(jsonResult.getBytes(),"UTF-8"));
            }
            catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ctx, "Connection Error", Toast.LENGTH_LONG).show();
                return null;
            }
            return null;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        protected void onPostExecute(String result) {
            makeLists();
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

            String[] qst = questions.get(globalI).split("@",2);
            System.out.println(qst[0]);
            System.out.println(qst[1]);
            upperText.setText(qst[0]);
            lowerText.setText(qst[1]);
            //upperText.setShadowLayer(2, 2, 4, Color.BLACK);
            //lowerText.setShadowLayer(10, 2, 4, Color.BLACK);
            nextQuestion();

            upperImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent2) {
                    float x2=0,y2=0;
                    if(motionEvent2.getAction() == MotionEvent.ACTION_DOWN){
                        //do something when pressed down
                        //Log.d("or", "or clicked");
                        upperImage.setVisibility(View.INVISIBLE);
                        upperImage2.setVisibility(View.VISIBLE);
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
                        upperImage2.setVisibility(View.INVISIBLE);
                        upperImage.setVisibility(View.VISIBLE);
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
                        lowerImage.setVisibility(View.INVISIBLE);
                        lowerImage2.setVisibility(View.VISIBLE);
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
                        lowerImage.setVisibility(View.VISIBLE);
                        lowerImage2.setVisibility(View.INVISIBLE);
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
                    if(showstats==0) {
                        //Log.d("click", "Up clicked");
                        //send correct gender on stats submit
                        if(genderS.equals("male")){
                            submitStats ss = new submitStats(globalI - 1, 0, 1, 0, 0);
                            ss.execute((Void) null);
                        }else if(genderS.equals("female")){
                            submitStats ss = new submitStats(globalI - 1, 0, 0, 1, 0);
                            ss.execute((Void) null);
                        }else{
                            submitStats ss = new submitStats(globalI - 1, 0, 0, 0, 1);
                            ss.execute((Void) null);
                        }
                        GetStats gs = new GetStats();
                        gs.execute(new String[] {getStatsUrl});
                        System.out.println(other0);

                        if (globalI < questions.size() - 1) {
                            String[] qst = questions.get(globalI).split("@", 2);
                            upperText.setText(qst[0]);
                            lowerText.setText(qst[1]);
                            System.out.println(qst[0]);
                            System.out.println(qst[1]);
                            nextQuestion();
                            editor.putInt("state", globalI);
                            editor.apply();
                            editor.commit();
                        } else {
                            Toast.makeText(ctx, "Τέλος ερωτήσεων", Toast.LENGTH_LONG).show();
                            globalI =0;
                            SP2 = getSharedPreferences("gameState",MODE_PRIVATE);
                            SharedPreferences.Editor editor = SP2.edit();
                            editor.putInt("state",0);
                            editor.apply();
                            editor.commit();
                        }
                        showstats=1;
                    }else{
                        int male0i ,female0i ,other0i ,male1i ,female1i ,other1i;
                        if(globalI>1){
                             male0i =(male0.get(globalI-1));
                             female0i=(female0.get(globalI-1));
                             other0i = (other0.get(globalI-1));
                             male1i=(male1.get(globalI-1));
                             female1i=(female1.get(globalI-1));
                            other1i=(other1.get(globalI-1));
                        }else{
                             male0i =(male0.get(globalI));
                             female0i=(female0.get(globalI));
                             other0i = (other0.get(globalI));
                             male1i=(male1.get(globalI));
                             female1i=(female1.get(globalI));
                             other1i=(other1.get(globalI));
                        }
                        float lowerstatsmale = (float) (male1i)/(float) (male0i+male1i);
                        float lowerstatfesmale = (float) (female1i)/(float) (female0i+female1i);
                        float lowerstatsother = (float) (other1i)/(float) (other1i+other0i);
                        float upperstatsmale = (float) (male0i)/(float) (male0i+male1i);
                        float upperstatfesmale = (float) (female0i)/(float) (female0i+female1i);
                        float upperstatsother = (float) (other0i)/(float) (other0i+other1i);
                        float lowerstats =(float)(male1i+female1i+other1i)/(float)(male0i+female0i+other0i+male1i+female1i+other1i);
                        float upperstats =(float)(male0i+female0i+other0i)/(float)(male0i+female0i+other0i+male1i+female1i+other1i);
                        //System.out.println("lowerstats "+lowerstats);
                        int lowerstatsshow = (int) (lowerstats*100);
                        int upperstatsshow = (int) (upperstats*100);
                        int lowerstatsmaleshow = (int) (lowerstatsmale*100);
                        int lowerstatsfemaleshow = (int) (lowerstatfesmale*100);
                        int lowerstatsothershow = (int)(lowerstatsother*100);
                        int upperstatsmaleshow = (int) (upperstatsmale*100);
                        int upperstatsfemaleshow = (int) (upperstatfesmale*100);
                        int upperstatsothershow = (int)(upperstatsother*100);
                        String lowerString1 = "Άνδρες "+Integer.toString(lowerstatsmaleshow)+"% ("+Integer.toString(male1i)+") \nΓυναίκες "+Integer.toString(lowerstatsfemaleshow)+"% ("+Integer.toString(female1i)+") \nΆλλο "+Integer.toString(lowerstatsothershow)+"% ("+Integer.toString(other1i)+")";
                        String lowerString2 = Integer.toString(lowerstatsshow)+"%"+"\n";
                        SpannableString ss1=  new SpannableString(lowerString2+lowerString1);
                        ss1.setSpan(new RelativeSizeSpan(0.8f), lowerString2.length(),lowerString1.length()+lowerString2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
                        //System.out.println("other0 "+other0i);
                        //System.out.println("other1 "+other1i);
                        String upperString1 ="Άνδρες "+Integer.toString(upperstatsmaleshow)+"% ("+Integer.toString(male0i)+") \nΓυναίκες "+Integer.toString(upperstatsfemaleshow)+"% ("+Integer.toString(female0i)+") \nΆλλο "+Integer.toString(upperstatsothershow)+"% ("+Integer.toString(other0i)+")";
                        String upperString2 =Integer.toString(upperstatsshow)+"%"+"\n";
                        SpannableString ss2=  new SpannableString(upperString2+upperString1);
                        ss2.setSpan(new RelativeSizeSpan(0.8f), upperString2.length(),upperString1.length()+upperString2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
                        lowerText.setText( ss1);
                        //lowerText.setText(Integer.toString(lowerstatsshow)+"%"+"\n"+"Άνδρες "+Integer.toString(lowerstatsmaleshow)+"% ("+Integer.toString(male1i)+") \nΓυναίκες "+Integer.toString(lowerstatsfemaleshow)+"% ("+Integer.toString(female1i)+") \nΆλλο "+Integer.toString(lowerstatsothershow)+"% ("+Integer.toString(other1i)+")");
                        //upperText.setText( Integer.toString(upperstatsshow)+"%"+"\n"+"Άνδρες "+Integer.toString(upperstatsmaleshow)+"% ("+Integer.toString(male0i)+") \nΓυναίκες "+Integer.toString(upperstatsfemaleshow)+"% ("+Integer.toString(female0i)+") \nΆλλο "+Integer.toString(upperstatsothershow)+"% ("+Integer.toString(other0i)+")");
                        upperText.setText(ss2);
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
                    if(showstats==0) {
                        //Log.d("click", "Down clicked");
                        if(genderS.equals("male")){
                            submitStats ss = new submitStats(globalI - 1, 1, 1, 0, 0);
                            ss.execute((Void) null);
                        }else if(genderS.equals("female")){
                            submitStats ss = new submitStats(globalI - 1, 1, 0, 1, 0);
                            ss.execute((Void) null);
                        }else{
                            submitStats ss = new submitStats(globalI - 1, 0, 0, 0, 1);
                            ss.execute((Void) null);
                        }
                        GetStats gs = new GetStats();
                        gs.execute(new String[] {getStatsUrl});

                        if (globalI < questions.size() - 1) {
                            String[] qst = questions.get(globalI).split("@", 2);
                            lowerText.setText(qst[1]);
                            upperText.setText(qst[0]);
                            System.out.println(qst[0]);
                            System.out.println(qst[1]);
                            nextQuestion();
                            editor.putInt("state", globalI);
                            editor.apply();
                            editor.commit();
                        } else {
                            Toast.makeText(ctx, "Τέλος ερωτήσεων", Toast.LENGTH_LONG).show();
                            globalI =0;
                            SP2 = getSharedPreferences("gameState",MODE_PRIVATE);
                            SharedPreferences.Editor editor = SP2.edit();
                            editor.putInt("state",0);
                            editor.apply();
                            editor.commit();
                        }
                        showstats=1;
                    }else{
                        int male0i ,female0i ,other0i ,male1i ,female1i ,other1i;
                        if(globalI>1){
                            male0i =(male0.get(globalI-1));
                            female0i=(female0.get(globalI-1));
                            other0i = (other0.get(globalI-1));
                            male1i=(male1.get(globalI-1));
                            female1i=(female1.get(globalI-1));
                            other1i=(other1.get(globalI-1));
                        }else{
                            male0i =(male0.get(globalI));
                            female0i=(female0.get(globalI));
                            other0i = (other0.get(globalI));
                            male1i=(male1.get(globalI));
                            female1i=(female1.get(globalI));
                            other1i=(other1.get(globalI));
                        }
                        float lowerstatsmale = (float) (male1i)/(float) (male0i+male1i);
                        float lowerstatfesmale = (float) (female1i)/(float) (female0i+female1i);
                        float lowerstatsother = (float) (other1i)/(float) (other1i+other0i);
                        float upperstatsmale = (float) (male0i)/(float) (male0i+male1i);
                        float upperstatfesmale = (float) (female0i)/(float) (female0i+female1i);
                        float upperstatsother = (float) (other0i)/(float) (other0i+other1i);
                        float lowerstats =(float)(male1i+female1i+other1i)/(float)(male0i+female0i+other0i+male1i+female1i+other1i);
                        float upperstats =(float)(male0i+female0i+other0i)/(float)(male0i+female0i+other0i+male1i+female1i+other1i);
                        //System.out.println("lowerstats "+lowerstats);
                        int lowerstatsshow = (int) (lowerstats*100);
                        int upperstatsshow = (int) (upperstats*100);
                        int lowerstatsmaleshow = (int) (lowerstatsmale*100);
                        int lowerstatsfemaleshow = (int) (lowerstatfesmale*100);
                        int lowerstatsothershow = (int)(lowerstatsother*100);
                        int upperstatsmaleshow = (int) (upperstatsmale*100);
                        int upperstatsfemaleshow = (int) (upperstatfesmale*100);
                        int upperstatsothershow = (int)(upperstatsother*100);
                        String lowerString1 = "Άνδρες "+Integer.toString(lowerstatsmaleshow)+"% ("+Integer.toString(male1i)+") \nΓυναίκες "+Integer.toString(lowerstatsfemaleshow)+"% ("+Integer.toString(female1i)+") \nΆλλο "+Integer.toString(lowerstatsothershow)+"% ("+Integer.toString(other1i)+")";
                        String lowerString2 = Integer.toString(lowerstatsshow)+"%"+"\n";
                        SpannableString ss1=  new SpannableString(lowerString2+lowerString1);
                        ss1.setSpan(new RelativeSizeSpan(0.8f), lowerString2.length(),lowerString1.length()+lowerString2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
                        //System.out.println("other0 "+other0i);
                        //System.out.println("other1 "+other1i);
                        String upperString1 ="Άνδρες "+Integer.toString(upperstatsmaleshow)+"% ("+Integer.toString(male0i)+") \nΓυναίκες "+Integer.toString(upperstatsfemaleshow)+"% ("+Integer.toString(female0i)+") \nΆλλο "+Integer.toString(upperstatsothershow)+"% ("+Integer.toString(other0i)+")";
                        String upperString2 =Integer.toString(upperstatsshow)+"%"+"\n";
                        SpannableString ss2=  new SpannableString(upperString2+upperString1);
                        ss2.setSpan(new RelativeSizeSpan(0.8f), upperString2.length(),upperString1.length()+upperString2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
                        lowerText.setText( ss1);
                        //lowerText.setText(Integer.toString(lowerstatsshow)+"%"+"\n"+"Άνδρες "+Integer.toString(lowerstatsmaleshow)+"% ("+Integer.toString(male1i)+") \nΓυναίκες "+Integer.toString(lowerstatsfemaleshow)+"% ("+Integer.toString(female1i)+") \nΆλλο "+Integer.toString(lowerstatsothershow)+"% ("+Integer.toString(other1i)+")");
                        //upperText.setText( Integer.toString(upperstatsshow)+"%"+"\n"+"Άνδρες "+Integer.toString(upperstatsmaleshow)+"% ("+Integer.toString(male0i)+") \nΓυναίκες "+Integer.toString(upperstatsfemaleshow)+"% ("+Integer.toString(female0i)+") \nΆλλο "+Integer.toString(upperstatsothershow)+"% ("+Integer.toString(other0i)+")");
                        upperText.setText(ss2);
                        //System.out.println("other0 "+other0i);
                        //System.out.println("other1 "+other1i);
                        showstats=0;
                    }
                }
            });
        }
    }// end async task

    public void accessWebService() {
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[] { url });
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

                String outPut = qst;
                String outPut2 = cat;
                questions.add(outPut);
                category.add(outPut2);
                ids.add(id);
                males0.add(male0);
                females0.add(female0);
                others0.add(other0);
                males1.add(male1);
                females1.add(female1);
                others1.add(other1);
                System.out.println("New data: "+ (i+1));
            }

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error JSON Parser" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            Toast.makeText(this, "No internet", Toast.LENGTH_LONG).show();
        }
        //converting array lists back to Gson and storing
        SharedPreferences.Editor editor = SP.edit();
        ArrayList<String> set1 = new ArrayList<>();
        ArrayList<String> set13 = new ArrayList<>();
        ArrayList<String> set14 = new ArrayList<>();
        ArrayList<Integer> set15 = new ArrayList<>();
        ArrayList<Integer> set16 = new ArrayList<>();
        ArrayList<Integer> set17 = new ArrayList<>();
        ArrayList<Integer> set18 = new ArrayList<>();
        ArrayList<Integer> set19 = new ArrayList<>();
        ArrayList<Integer> set110 = new ArrayList<>();

        set1.addAll(questions);
        set13.addAll(category);
        set14.addAll(ids);
        set15.addAll(males0);
        set16.addAll(females0);
        set17.addAll(others0);
        set18.addAll(males1);
        set19.addAll(females1);
        set110.addAll(others1);

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
        ArrayList<Integer> set15 = new ArrayList<>();
        ArrayList<Integer> set16 = new ArrayList<>();
        ArrayList<Integer> set17 = new ArrayList<>();
        ArrayList<Integer> set18 = new ArrayList<>();
        ArrayList<Integer> set19 = new ArrayList<>();
        ArrayList<Integer> set110 = new ArrayList<>();

        set15.addAll(males0);
        set16.addAll(females0);
        set17.addAll(others0);
        set18.addAll(males1);
        set19.addAll(females1);
        set110.addAll(others1);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.reset) {
            globalI =0;
            SP2 = getSharedPreferences("gameState",MODE_PRIVATE);
            SharedPreferences.Editor editor = SP2.edit();
            editor.putInt("state",0);
            editor.apply();
            editor.commit();
        }

        return super.onOptionsItemSelected(item);
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
            // TODO: attempt authentication against a network service.
            String reg_url = "http://83.212.84.230/sendstats.php";
            String response = null;
            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                //httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(mid), "UTF-8") +"&"+URLEncoder.encode("choice", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mchoice), "UTF-8")+"&"+URLEncoder.encode("male", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mmale), "UTF-8")+"&"+URLEncoder.encode("female", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mfemale), "UTF-8")+"&"+URLEncoder.encode("other", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mother), "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
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
                //httpURLConnection.connect();
                httpURLConnection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            SP = getSharedPreferences("questions", MODE_PRIVATE);
            try {
                List<NameValuePair> sendparams = new ArrayList<>();
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(sendparams, HTTP.UTF_8);
                httppost.setEntity(ent);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                jsonStatsResult = sb.toString();
                //Log.d("result", jsonStatsResult);
                //System.out.println(new String(jsonStatsResult.getBytes(), "UTF-8"));
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ctx, "Connection Error", Toast.LENGTH_LONG).show();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            makeStatsLists();
        }
    }




}
