package panafana.example.panaf.wouldyourather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import panafana.example.panaf.wouldyourather.R;
import panafana.example.panaf.wouldyourather.models.Comment;
import panafana.example.panaf.wouldyourather.models.Question;
import panafana.example.panaf.wouldyourather.models.Stats;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

import static android.content.Context.MODE_PRIVATE;

public class Manager {

    public interface RetrofitInterface {

        @POST
        Call<ResponseBody> getQuestions(@Url String id);

        @GET("/android/get-animals")
        Call<ResponseBody> getAnimals();
    }


    public void getQuestions(final Context context){
        final String serverUrl = context.getString(R.string.server_url);
        final Utils utils = new Utils();
        final SharedPreferences SP = context.getSharedPreferences("questions", MODE_PRIVATE);
        final String temp = SP.getString("allquestions",null);
        String id;
        if(temp!=null){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Question>>() {
            }.getType();
            ArrayList<Question> allquestions = gson.fromJson(temp, type);
            id=allquestions.get(allquestions.size()-1).getId();
        }else{
            id = "000000000000000000000000";
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitInterface service=retrofit.create(RetrofitInterface.class);
        Call<ResponseBody> call = service.getQuestions(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {
                    Log.i("animals received","success");
                    try {
                        String resp =response.body().string();
                        System.out.println("resp "+resp);
                        ArrayList<Question> allquestions;
                        if(temp!=null){
                            Gson gson = new Gson();
                            Type type = new TypeToken<ArrayList<Question>>() {
                            }.getType();
                            allquestions = gson.fromJson(temp, type);
                        }else{
                            allquestions = new ArrayList<>();
                        }
                            JSONArray questions =new JSONArray(resp);
                            for(int i = 0;i<questions.length();i++){
                                String question = questions.getJSONObject(i).getString("question");
                                String category = questions.getJSONObject(i).getString("category");
                                String id = questions.getJSONObject(i).getString("_id");
                                JSONObject stats = questions.getJSONObject(i).getJSONObject("stats");
                                JSONArray comments = questions.getJSONObject(i).getJSONArray("comments");

                                int male0 = stats.getInt("male0");
                                int male1 = stats.getInt("male1");
                                int female0 = stats.getInt("female0");
                                int female1 = stats.getInt("female1");
                                int other0 = stats.getInt("other0");
                                int other1 = stats.getInt("other1");
                                Stats mystats = new Stats(male0,female0,other0,male1,female1,other1);
                                ArrayList<Comment> mycomments = new ArrayList<>();

                                for(int k = 0;k<comments.length();k++){
                                    String comment = comments.getJSONObject(k).getString("comment");
                                    String date = comments.getJSONObject(k).getString("date");
                                    String user = comments.getJSONObject(k).getString("user");
                                    Comment com = new Comment(comment,date,user);
                                    mycomments.add(com);
                                }
                                Question tempqst = new Question(question,category,id,mystats,mycomments);
                                allquestions.add(tempqst);

                            }
                            SharedPreferences.Editor editor = SP.edit();
                            Gson gson1 = new Gson();
                            String json1 = gson1.toJson(allquestions);
                            editor.putString("allquestions",json1);
                            editor.apply();



                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.e("animals received","failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("call",t.getMessage());
                Log.e("animals received","failed");

            }
        });

    }


}
