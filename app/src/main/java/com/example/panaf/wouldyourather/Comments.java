package com.example.panaf.wouldyourather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Comments extends AppCompatActivity {
    private String submitCommentUrl ="http://83.212.84.230/submitcomment.php";
    private String commentsUrl ="http://83.212.84.230/getcomments.php";
    private  String jsonSubmitedCommentResult,jsonCommentResult;
    MyListAdapter adapter;
    ListView listView;
    Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Button submit = findViewById(R.id.comment_submit);
        EditText comment = findViewById(R.id.comment);
        listView = findViewById(R.id.listView1);
        jsonCommentResult = getIntent().getStringExtra("comments");
        int question =getIntent().getIntExtra("question",0);
        System.out.println("question id "+question);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmt = comment.getText().toString();
                if(cmt.length()>1){
                    SubmitComment sc = new SubmitComment();
                    sc.execute(new String[] {submitCommentUrl,cmt,Integer.toString(question)});
                    GetComments gc = new GetComments();
                    gc.execute(new String[] {commentsUrl,Integer.toString(question)});
                    comment.setText("");
                }

            }
        });

        ArrayList<String> comments = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();

        try {
            JSONObject jsonResponse = new JSONObject(jsonCommentResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("result");
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String tempc = jsonChildNode.optString("comment");
                String tempu = jsonChildNode.optString("user");
                String tempd = jsonChildNode.optString("date");
                comments.add(tempc);
                users.add(tempu);
                dates.add(tempd);
            }
        }catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error JSON Parser" + e.toString(),
                    Toast.LENGTH_SHORT).show();
            Log.d("error ",e.toString());
        } catch (NullPointerException e) {
            Toast.makeText(this, "No internet", Toast.LENGTH_LONG).show();
        }

        String[] commentsStr = new String[comments.size()];
        String[] usersStr = new String[users.size()];
        String[] datesStr = new String[dates.size()];
        for(int i =0;i<comments.size();i++){
            commentsStr[i]= comments.get(i);
            usersStr[i]=users.get(i);
            datesStr[i]=dates.get(i);
        }

        adapter = new MyListAdapter(this,commentsStr,usersStr,datesStr);

        //itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, comments);
        //listView.setAdapter(itemsAdapter);
        listView.setAdapter(adapter);


    }


    private class SubmitComment extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            String comment = params[1];
            String question = params[2];
            try {
                List<NameValuePair> sendparams = new ArrayList<>();
                sendparams.add(new BasicNameValuePair("comment",comment));
                sendparams.add(new BasicNameValuePair("question",question));
                sendparams.add(new BasicNameValuePair("user","test"));
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
                jsonSubmitedCommentResult = sb.toString();
                Log.d("result", jsonSubmitedCommentResult);
                //System.out.println(new String(jsonStatsResult.getBytes(), "UTF-8"));
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ctx, "Connection Error", Toast.LENGTH_LONG).show());
                return "error";
            }
            return "success";
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }


    private class GetComments extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            String question = params[1];
            try {
                List<NameValuePair> sendparams = new ArrayList<>();
                sendparams.add(new BasicNameValuePair("question", question));
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
                jsonCommentResult = sb.toString();
                //Log.d("result", jsonStatsResult);
                //System.out.println(new String(jsonStatsResult.getBytes(), "UTF-8"));
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ctx, "Connection Error", Toast.LENGTH_LONG).show());
                return "error";
            }
            return "success";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("comments",jsonCommentResult);
            ArrayList<String> comments = new ArrayList<>();
            ArrayList<String> users = new ArrayList<>();
            ArrayList<String> dates = new ArrayList<>();

            try {
                JSONObject jsonResponse = new JSONObject(jsonCommentResult);
                JSONArray jsonMainNode = jsonResponse.optJSONArray("result");
                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    String tempc = jsonChildNode.optString("comment");
                    String tempu = jsonChildNode.optString("user");
                    String tempd = jsonChildNode.optString("date");
                    comments.add(tempc);
                    users.add(tempu);
                    dates.add(tempd);
                }
            }catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error JSON Parser" + e.toString(),
                        Toast.LENGTH_SHORT).show();
                Log.d("error ",e.toString());
            } catch (NullPointerException e) {
                //Toast.makeText(this, "No internet", Toast.LENGTH_LONG).show();
            }
            String[] commentsStr = new String[comments.size()];
            String[] usersStr = new String[users.size()];
            String[] datesStr = new String[dates.size()];
            for(int i =0;i<comments.size();i++){
                commentsStr[i]= comments.get(i);
                usersStr[i]=users.get(i);
                datesStr[i]=dates.get(i);
            }


            //adapter = new MyListAdapter(Comments.this,commentsStr,usersStr,datesStr);

            adapter=new MyListAdapter(Comments.this,commentsStr,usersStr,datesStr);
            listView.invalidate();

            listView.setAdapter(adapter);

            //itemsAdapter.clear();
            //itemsAdapter.addAll(comments);
            //itemsAdapter.notifyDataSetChanged();
        }
    }
}
