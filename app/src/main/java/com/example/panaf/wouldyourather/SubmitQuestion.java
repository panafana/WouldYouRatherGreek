package com.example.panaf.wouldyourather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class SubmitQuestion extends AppCompatActivity {
    Context ctx = this;
    String result1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_question);
        final EditText qst = findViewById(R.id.question);
        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(qst.getText().toString().equals(""))){
                    SendQuestion s = new SendQuestion(qst.getText().toString());
                    s.execute();
                }
            }
        });
    }

    public class SendQuestion extends AsyncTask<Void, Void, String> {
        
        final String mquestion;

        SendQuestion(String question) {
            mquestion = question;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String reg_url = "http://83.212.84.230/submitquestion.php";
            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                //httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode("question", "UTF-8") + "=" + URLEncoder.encode(mquestion, "UTF-8");
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
                String r = (result.toString());
                IS.close();
                result1=r;
                Log.d("Response", httpURLConnection.getResponseMessage());
                Log.d("Response", r);
                //httpURLConnection.connect();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                return "Connection Error";
            }

            if (result1.contains("Success")) {
                return "Success";
            } else {
                return "Error";
            }
        }


        @Override
        protected void onPostExecute(final String success) {
            if (success.equals("Success")) {
                finish();
            } else {
                Toast.makeText(ctx, "Error", Toast.LENGTH_LONG).show();
            }
        }
    }
}
