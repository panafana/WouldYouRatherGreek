package panafana.example.panaf.wouldyourather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

import static android.content.Context.MODE_PRIVATE;

public class Manager {

    public interface RetrofitInterface {

        @POST("/android/get-questions")
        Call<ResponseBody> getQuestions(@Body JsonObject id);

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
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitInterface service=retrofit.create(RetrofitInterface.class);
        Call<ResponseBody> call = service.getQuestions(jsonObject);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {
                    Log.e("questions received","success");
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
                            JSONArray stats = questions.getJSONObject(i).getJSONArray("stats");
                            JSONArray comments = questions.getJSONObject(i).getJSONArray("comments");

                            int male0 = stats.getInt(0);
                            int male1 = stats.getInt(3);
                            int female0 = stats.getInt(1);
                            int female1 = stats.getInt(4);
                            int other0 = stats.getInt(2);
                            int other1 = stats.getInt(5);
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

                        ArrayList<String> allcategories = new ArrayList<>();
                        for(int i=0;i<allquestions.size();i++){
                            String tempcat = allquestions.get(i).getCategory();
                            if(!allcategories.contains(tempcat)){
                                allcategories.add(tempcat);
                            }
                        }
                        Log.e("cat",allcategories.get(0));
                        Gson gson2 = new Gson();
                        String json2 = gson2.toJson(allcategories);
                        editor.putString("allcategories",json2);
                        editor.apply();

                        for(int i=0;i<allquestions.size();i++){
                            Log.e("id",allquestions.get(i).getId());
                            Log.e("question",allquestions.get(i).getQuestion());
                            Log.e("category",allquestions.get(i).getCategory());
                            Log.e("stats", String.valueOf(allquestions.get(i).getStats().getMale0()));
                            Log.e("stats", String.valueOf(allquestions.get(i).getStats().getFemale0()));
                            Log.e("stats", String.valueOf(allquestions.get(i).getStats().getOther0()));
                            Log.e("stats", String.valueOf(allquestions.get(i).getStats().getMale1()));
                            Log.e("stats", String.valueOf(allquestions.get(i).getStats().getFemale1()));
                            Log.e("stats", String.valueOf(allquestions.get(i).getStats().getOther1()));
                            ArrayList<Comment> coms = allquestions.get(i).getComments();
                            for(int j=0;j<coms.size();j++){
                                Log.e("comment",coms.get(j).getComment());
                                Log.e("date",coms.get(j).getDate());
                                Log.e("user",coms.get(j).getUser());
                            }

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.e("questions received","failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("call",t.getMessage());
                Log.e("questions received","failed");

            }
        });

    }


}
