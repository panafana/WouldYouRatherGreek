package panafana.example.panaf.wouldyourather;

import android.app.Activity;
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

import panafana.example.panaf.wouldyourather.utils.Manager;

public class SubmitQuestion extends AppCompatActivity {
    Context ctx = this;
    Activity activity = this;
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
                    new Manager().submitQuestion(ctx, activity, qst.getText().toString(),qst2.getText().toString(), mFirebaseAnalytics);
//                    SendQuestion s = new SendQuestion(finalqst);
//                    s.execute();
                }
            }
        });
    }

}
