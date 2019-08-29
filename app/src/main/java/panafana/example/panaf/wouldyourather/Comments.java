package panafana.example.panaf.wouldyourather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import panafana.example.panaf.wouldyourather.models.Comment;
import panafana.example.panaf.wouldyourather.models.Question;
import panafana.example.panaf.wouldyourather.utils.Manager;

public class Comments extends AppCompatActivity {

    private  String jsonSubmitedCommentResult,jsonCommentResult;
    MyListAdapter adapter;
    ListView listView;
    Context ctx;
    private FirebaseAnalytics mFirebaseAnalytics;
    ArrayList<Question> runningQuestions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button submit = findViewById(R.id.comment_submit);
        EditText comment = findViewById(R.id.comment);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        String id = getIntent().getStringExtra("id");
        int globalI = getIntent().getIntExtra("globalI",0);
        listView = findViewById(R.id.listView1);
        final SharedPreferences SP = getApplicationContext().getSharedPreferences("questions", MODE_PRIVATE);
        final String temp = SP.getString("runningQuestions",null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Question>>() {
        }.getType();
        runningQuestions = gson.fromJson(temp, type);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmt = comment.getText().toString();
                if(cmt.length()>1){
//                    SubmitComment sc = new SubmitComment();
//                    sc.execute(new String[] {submitCommentUrl,cmt,Integer.toString(question)});
//                    GetComments gc = new GetComments();
//                    gc.execute(new String[] {commentsUrl,Integer.toString(question)});
                    comment.setText("");
                    Manager manager = new Manager();
                    String user;
                    SharedPreferences SP =getSharedPreferences("user",MODE_PRIVATE);
                    if(SP.contains("username")){
                        user = SP.getString("username","noone");
                    }else{
                        user= "test";
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDateandTime = sdf.format(new Date());
                    Log.e("now",currentDateandTime);
                    ArrayList<Comment> coms = runningQuestions.get(globalI).getComments();
                    Comment com = new Comment(cmt,currentDateandTime,user);
                    coms.add(com);
                    runningQuestions.get(globalI).setComments(coms);
                    adapter.updateAdapter(coms);
                    adapter.notifyDataSetChanged();
                    manager.submitComment(getApplicationContext(),com,id);

                }

            }
        });

//        ArrayList<String> comments = new ArrayList<>();
//        ArrayList<String> users = new ArrayList<>();
//        ArrayList<String> dates = new ArrayList<>();
//
//        try {
//            JSONObject jsonResponse = new JSONObject(jsonCommentResult);
//            JSONArray jsonMainNode = jsonResponse.optJSONArray("result");
//            for (int i = 0; i < jsonMainNode.length(); i++) {
//                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
//                String tempc = jsonChildNode.optString("comment");
//                String tempu = jsonChildNode.optString("user");
//                String tempd = jsonChildNode.optString("date");
//
//                // This could be MM/dd/yyyy, you original value is ambiguous
//                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date dateValue = input.parse(tempd);
//                SimpleDateFormat output = new SimpleDateFormat("HH:mm dd/MM/yyyy ");
//                //System.out.println("" + output.format(dateValue) + " real date " + tempd);
//
//                //System.out.println(tempd);
//                comments.add(tempc);
//                users.add(tempu);
//                dates.add(output.format(dateValue));
//            }
//        }catch (JSONException e) {
//            Toast.makeText(getApplicationContext(), "Error JSON Parser" + e.toString(),
//                    Toast.LENGTH_SHORT).show();
//            Log.d("error ",e.toString());
//        } catch (NullPointerException e) {
//            Toast.makeText(this, "No internet", Toast.LENGTH_LONG).show();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }



        adapter = new MyListAdapter(this,runningQuestions.get(globalI).getComments());

        //itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, comments);
        //listView.setAdapter(itemsAdapter);
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


//
//    private class SubmitComment extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... params) {
//            String comment = params[1];
//            String question = params[2];
//            String user;
//            SharedPreferences SP =getSharedPreferences("user",MODE_PRIVATE);
//            if(SP.contains("username")){
//                user = SP.getString("username","noone");
//            }else{
//                user= "test";
//            }
//            try {
//                URL url = new URL(params[0]);
//                Uri.Builder builder = new Uri.Builder()
//                        .appendQueryParameter("comment", comment)
//                        .appendQueryParameter("question", question)
//                        .appendQueryParameter("user", user);
//                String query = builder.build().getEncodedQuery();
//
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setReadTimeout(15000);
//                conn.setConnectTimeout(15000);
//                conn.setRequestMethod("POST");
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//                OutputStream os = conn.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(
//                        new OutputStreamWriter(os, "UTF-8"));
//                writer.write(query);
//                writer.flush();
//                writer.close();
//                os.close();
//                conn.connect();
//
//                InputStream IS = conn.getInputStream();
//                // json is UTF-8 by default
//                BufferedReader reader = new BufferedReader(new InputStreamReader(IS, "UTF-8"), 8);
//                StringBuilder sb = new StringBuilder();
//                String line = null;
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line + "\n");
//                }
//                jsonSubmitedCommentResult = sb.toString();
//                Log.d("result", jsonSubmitedCommentResult);
//                conn.disconnect();
//                //System.out.println(new String(jsonStatsResult.getBytes(), "UTF-8"));
//            } catch (ClientProtocolException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//                runOnUiThread(() -> Toast.makeText(ctx, "Connection Error", Toast.LENGTH_LONG).show());
//                return "error";
//            }
//            return "success";
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            Bundle bundle = new Bundle();
//            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "submit comment");
//            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.POST_SCORE, bundle);
//        }
//    }
//
//
//    private class GetComments extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... params) {
//
//            String question = params[1];
//            try {
//                URL url = new URL(params[0]);
//                //String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(mid), "UTF-8") +"&"+URLEncoder.encode("choice", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mchoice), "UTF-8")+"&"+URLEncoder.encode("male", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mmale), "UTF-8")+"&"+URLEncoder.encode("female", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mfemale), "UTF-8")+"&"+URLEncoder.encode("other", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mother), "UTF-8");
//                Uri.Builder builder = new Uri.Builder()
//                        .appendQueryParameter("question", question);
//                String query = builder.build().getEncodedQuery();
//
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setReadTimeout(15000);
//                conn.setConnectTimeout(15000);
//                conn.setRequestMethod("POST");
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//
//
//                OutputStream os = conn.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(
//                        new OutputStreamWriter(os, "UTF-8"));
//                writer.write(query);
//
//                writer.flush();
//                writer.close();
//                os.close();
//                conn.connect();
//                InputStream IS = conn.getInputStream();
//                // json is UTF-8 by default
//                BufferedReader reader = new BufferedReader(new InputStreamReader(IS, "UTF-8"), 8);
//                StringBuilder sb = new StringBuilder();
//                String line = null;
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line + "\n");
//                }
//                jsonCommentResult = sb.toString();
//                conn.disconnect();
//                //Log.d("result", jsonStatsResult);
//                //System.out.println(new String(jsonStatsResult.getBytes(), "UTF-8"));
//            } catch (ClientProtocolException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//                runOnUiThread(() -> Toast.makeText(ctx, "Connection Error", Toast.LENGTH_LONG).show());
//                return "error";
//            }
//            return "success";
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            Log.d("comments",jsonCommentResult);
//            ArrayList<String> comments = new ArrayList<>();
//            ArrayList<String> users = new ArrayList<>();
//            ArrayList<String> dates = new ArrayList<>();
//
//            try {
//                JSONObject jsonResponse = new JSONObject(jsonCommentResult);
//                JSONArray jsonMainNode = jsonResponse.optJSONArray("result");
//                for (int i = 0; i < jsonMainNode.length(); i++) {
//                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
//                    String tempc = jsonChildNode.optString("comment");
//                    String tempu = jsonChildNode.optString("user");
//                    String tempd = jsonChildNode.optString("date");
//                    // This could be MM/dd/yyyy, you original value is ambiguous
//                    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    Date dateValue = input.parse(tempd);
//                    SimpleDateFormat output = new SimpleDateFormat("HH:mm dd/MM/yyyy ");
//                    //System.out.println("" + output.format(dateValue) + " real date " + tempd);
//
//                    comments.add(tempc);
//                    users.add(tempu);
//                    dates.add(output.format(dateValue));
//                }
//            }catch (JSONException e) {
//                Toast.makeText(getApplicationContext(), "Error JSON Parser" + e.toString(),
//                        Toast.LENGTH_SHORT).show();
//                Log.d("error ",e.toString());
//            } catch (NullPointerException e) {
//                //Toast.makeText(this, "No internet", Toast.LENGTH_LONG).show();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            String[] commentsStr = new String[comments.size()];
//            String[] usersStr = new String[users.size()];
//            String[] datesStr = new String[dates.size()];
//            for(int i =0;i<comments.size();i++){
//                commentsStr[i]= comments.get(i);
//                usersStr[i]=users.get(i);
//                datesStr[i]=dates.get(i);
//            }
//
//
//            //adapter = new MyListAdapter(Comments.this,commentsStr,usersStr,datesStr);
//
//            adapter=new MyListAdapter(Comments.this,commentsStr,usersStr,datesStr);
//            listView.invalidate();
//
//            listView.setAdapter(adapter);
//
//            //itemsAdapter.clear();
//            //itemsAdapter.addAll(comments);
//            //itemsAdapter.notifyDataSetChanged();
//        }
//    }
}
