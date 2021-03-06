package panafana.example.panaf.wouldyourather;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.analytics.FirebaseAnalytics;

public class SubmitQuestion extends AppCompatActivity {
    Context ctx = this;
    String result1;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_question);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        final EditText qst = findViewById(R.id.question);
        final EditText qst2 = findViewById(R.id.question2);
        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(qst.getText().toString().equals(""))&&!(qst2.getText().toString().equals(""))){
                    String finalqst = qst.getText().toString() +" @ "+ qst2.getText().toString();
                    SendQuestion s = new SendQuestion(finalqst);
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
            String reg_url = "http://83.212.84.230/submitquestion.php";
            try {
                URL url = new URL(reg_url);


                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("question", mquestion);
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

                Log.d("Response", r);
                //httpURLConnection.connect();
                conn.disconnect();

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
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "submit question");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.POST_SCORE, bundle);
                finish();
            } else {
                Toast.makeText(ctx, "Error", Toast.LENGTH_LONG).show();
            }
        }
    }
}
