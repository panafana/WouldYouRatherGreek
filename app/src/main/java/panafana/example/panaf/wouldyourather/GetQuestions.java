package panafana.example.panaf.wouldyourather;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetQuestions  extends AppCompatActivity {

    AlertDialog alertDialog;
    private static Context ctx ;
    static SharedPreferences SP;

    private static String jsonResult;
    private String url = "http://83.212.84.230/getquestions.php";

    GetQuestions (SharedPreferences SP ){
        accessWebService();
        makeLists();
    }


    // Async Task to access the web
    public static class JsonReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);


            String id;
            Gson gson4 = new Gson();
            //System.out.println(SP.getString("ids", null));
            if(SP.contains("ids")) {
                String json4 = SP.getString("ids", null);
                Type type4 = new TypeToken<ArrayList<String>>() {
                }.getType();
                ArrayList<String> set4 = gson4.fromJson(json4, type4);
                ArrayList<String> ids = new ArrayList<>(set4);
                id = new String(ids.get(ids.size() - 1));
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
                //jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
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

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
            }

            catch (IOException e) {
                // e.printStackTrace();
                Toast.makeText(ctx.getApplicationContext(),
                        "Error..." + e.toString(), Toast.LENGTH_LONG).show();
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {


        }
    }// end async task

    public void accessWebService() {
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[] { url });
    }

    // build hash set for list view
    public void makeLists() {
        List<Map<String, String>> employeeList = new ArrayList<Map<String, String>>();

        //SharedPreferences SP= cntx.getSharedPreferences("questions", cntx.MODE_PRIVATE);
        //getting already stored questions

        ArrayList<String> questions = new ArrayList<>();
        ArrayList<String> category = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();
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

        if(set!=null) {
            questions = new ArrayList<>(set);
            category = new ArrayList<>(set3);
            ids = new ArrayList<>(set4);
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

                String outPut = qst;
                String outPut2 = cat;
                questions.add(outPut);
                category.add(outPut2);
                ids.add(id);
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
        ArrayList<String> set12 = new ArrayList<>();
        ArrayList<String> set13 = new ArrayList<>();
        ArrayList<String> set14 = new ArrayList<>();

        set1.addAll(questions);
        set13.addAll(category);
        set14.addAll(ids);
        Gson gson1 = new Gson();
        String json1 = gson1.toJson(set1);
        Gson gson13 = new Gson();
        String json13 = gson13.toJson(set13);
        Gson gson14 = new Gson();
        String json14 = gson14.toJson(set14);

        editor.putString("questions", json1);
        editor.putString("category", json13);
        editor.putString("ids", json14);
        editor.apply();
        editor.commit();
        System.out.println("stored");
        //System.out.println("Messages: " + messages);
        //System.out.println("Signatures: " + signatures);
        //System.out.println("timestamps: " + timestamps);
        System.out.println("ids: " + ids);

/*
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, employeeList,
                android.R.layout.simple_list_item_1,
                new String[] { "whiteboard" }, new int[] { android.R.id.text1 });
        listView.setAdapter(simpleAdapter);
*/

    }



}
