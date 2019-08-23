package panafana.example.panaf.wouldyourather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import panafana.example.panaf.wouldyourather.LoginActivity;
import panafana.example.panaf.wouldyourather.MainActivity;
import panafana.example.panaf.wouldyourather.R;
import panafana.example.panaf.wouldyourather.models.Comment;
import panafana.example.panaf.wouldyourather.models.Question;
import panafana.example.panaf.wouldyourather.models.Stats;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

import static android.content.Context.MODE_PRIVATE;

public class Manager {

    public interface RetrofitInterface {

        @POST("/android/get-questions")
        Call<ResponseBody> getQuestions(@Body JsonObject id);

        @POST("/android/update-stats")
        Call<ResponseBody> updateStats(@Body JsonObject obj);

        @POST("/android/get-stats")
        Call<ResponseBody> getStats(@Body JsonObject obj);

        @POST("/android/get-comments")
        Call<ResponseBody> getComments(@Body JsonObject obj);

        @POST("/android/get-all-stats")
        Call<ResponseBody> getAllStats();

        @POST("/android/submit-comment")
        Call<ResponseBody> submitComment(@Body JsonObject obj);

        @POST("/android/signin")
        Call<ResponseBody> login(@Body JsonObject obj);

    }


    public void getQuestions(final Context context,boolean getStats){
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

//                        for(int i=0;i<allquestions.size();i++){
//                            Log.e("id",allquestions.get(i).getId());
//                            Log.e("question",allquestions.get(i).getQuestion());
//                            Log.e("category",allquestions.get(i).getCategory());
//                            Log.e("stats", String.valueOf(allquestions.get(i).getStats().getMale0()));
//                            Log.e("stats", String.valueOf(allquestions.get(i).getStats().getFemale0()));
//                            Log.e("stats", String.valueOf(allquestions.get(i).getStats().getOther0()));
//                            Log.e("stats", String.valueOf(allquestions.get(i).getStats().getMale1()));
//                            Log.e("stats", String.valueOf(allquestions.get(i).getStats().getFemale1()));
//                            Log.e("stats", String.valueOf(allquestions.get(i).getStats().getOther1()));
//                            ArrayList<Comment> coms = allquestions.get(i).getComments();
//                            for(int j=0;j<coms.size();j++){
//                                Log.e("comment",coms.get(j).getComment());
//                                Log.e("date",coms.get(j).getDate());
//                                Log.e("user",coms.get(j).getUser());
//                            }
//
//                        }

                        if(getStats){
                           Manager manager = new Manager();
                            manager.getAllStats(context);
                        }
                        if(allquestions.size()>0){
                            ((MainActivity)context).playGame();
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

    public void updateStats(final Context context,String id,int position) {
        final String serverUrl = context.getString(R.string.server_url);
        final Utils utils = new Utils();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("position", position);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitInterface service=retrofit.create(RetrofitInterface.class);
        Call<ResponseBody> call = service.updateStats(jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        Log.d("update stats",response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("update stats","Success");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("update stats","Error");
            }
        });



    }

    public void getStats(final Context context,String id) {
        final String serverUrl = context.getString(R.string.server_url);
        final Utils utils = new Utils();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitInterface service=retrofit.create(RetrofitInterface.class);
        Call<ResponseBody> call = service.getStats(jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        String responsestr = response.body().string();
                        Log.d("get stats",responsestr);

                        JSONObject new_stats = new JSONObject(responsestr);

                        System.out.println("new "+new_stats);
                        JSONArray array = new_stats.getJSONArray("stats");

                        final SharedPreferences SP = context.getSharedPreferences("questions", MODE_PRIVATE);
                        final String temp = SP.getString("allquestions",null);
                        Gson gson = new Gson();
                        Type type = new TypeToken<ArrayList<Question>>() {
                        }.getType();
                        ArrayList<Question> allquestions = gson.fromJson(temp, type);
                        int size = allquestions.size();
                        for(int i = 0; i<size;i++){
                            if(allquestions.get(i).getId().equals(id)){
                                allquestions.get(i).setStats(new Stats(array.optInt(0),array.optInt(1),array.optInt(2),array.optInt(3),array.optInt(4),array.optInt(5)));
                                break;
                            }
                        }
                        SharedPreferences.Editor editor = SP.edit();
                        Gson gson1 = new Gson();
                        String json1 = gson1.toJson(allquestions);
                        editor.putString("allquestions",json1);
                        editor.apply();
                        Log.d("stats","updated");



                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("get stats", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("get stats","Error");
            }
        });



    }

    public void getComments(final Context context,final String id){
        final String serverUrl = context.getString(R.string.server_url);
        final Utils utils = new Utils();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitInterface service=retrofit.create(RetrofitInterface.class);
        Call<ResponseBody> call = service.getComments(jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        String responsestr = response.body().string();
                        Log.d("get comments",response.body().string());

                        JSONObject new_comments = new JSONObject(responsestr);

                        System.out.println("new "+new_comments);
                        JSONArray array = new_comments.getJSONArray("comments");

                        ArrayList<Comment> new_coms = new ArrayList<>();
                        for(int i = 0; i < array.length();i++){
                            JSONObject temp = array.getJSONObject(i);
                            Comment temp_com = new Comment(temp.getString("comment"),temp.getString("date"),temp.getString("user"));
                            new_coms.add(temp_com);
                            Log.e("com1",temp.getString("comment"));
                        }

                        final SharedPreferences SP = context.getSharedPreferences("questions", MODE_PRIVATE);
                        final String temp = SP.getString("allquestions",null);
                        Gson gson = new Gson();
                        Type type = new TypeToken<ArrayList<Question>>() {
                        }.getType();
                        ArrayList<Question> allquestions = gson.fromJson(temp, type);
                        int size = allquestions.size();
                        for(int i = 0; i<size;i++){
                            if(allquestions.get(i).getId().equals(id)){
                                allquestions.get(i).setComments(new_coms);
                                break;
                            }
                        }
                        SharedPreferences.Editor editor = SP.edit();
                        Gson gson1 = new Gson();
                        String json1 = gson1.toJson(allquestions);
                        editor.putString("allquestions",json1);
                        editor.apply();
                        Log.d("get comments","updated");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("get comments", String.valueOf(response.code()));
                }else{
                    try {
                        String responsestr = response.body().string();
                        Log.d("get comments",response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.d("get comments","Error");
            }
        });

    }

    public void getAllStats(final Context context) {
        final String serverUrl = context.getString(R.string.server_url);
        final Utils utils = new Utils();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitInterface service=retrofit.create(RetrofitInterface.class);
        Call<ResponseBody> call = service.getAllStats();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        String responsestr = response.body().string();
                        Log.d("getAll stats",response.body().string());

                        JSONArray new_stats = new JSONArray(responsestr);

                        System.out.println("new "+new_stats);


                        final SharedPreferences SP = context.getSharedPreferences("questions", MODE_PRIVATE);
                        final String temp = SP.getString("allquestions",null);
                        Gson gson = new Gson();
                        Type type = new TypeToken<ArrayList<Question>>() {
                        }.getType();
                        ArrayList<Question> allquestions = gson.fromJson(temp, type);
                        int size = allquestions.size();
                        if(size==new_stats.length()){
                            for(int i = 0; i<size;i++){
                                JSONArray stats_array = new_stats.getJSONObject(i).getJSONArray("stats");
                                allquestions.get(i).setStats(new Stats(stats_array.optInt(0),stats_array.optInt(1),stats_array.optInt(2),stats_array.optInt(3),stats_array.optInt(4),stats_array.optInt(5)));

                            }
                        }else{
                            Manager manager = new Manager();
                            manager.getQuestions(context,true);
                        }
                        SharedPreferences.Editor editor = SP.edit();
                        Gson gson1 = new Gson();
                        String json1 = gson1.toJson(allquestions);
                        editor.putString("allquestions",json1);
                        editor.apply();
                        Log.d("all stats","updated");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("getAll stats", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.d("getAll stats","Error");
            }
        });

    }

    public void submitComment(final Context context,Comment comment,String id) {
        final String serverUrl = context.getString(R.string.server_url);
        final Utils utils = new Utils();
        String com = comment.getComment();
        String user = comment.getUser();
        String date = comment.getDate();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("comment",com);
        jsonObject.addProperty("user",user);
        jsonObject.addProperty("date",date);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);
        Call<ResponseBody> call = service.submitComment(jsonObject);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        String responsestr = response.body().string();

                        Log.d("submint comment  stats", "Success");
                        Log.d("submint comment  stats", responsestr);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("submint comment  stats","Error");
            }
        });

    }


    public void login(final String username, String password, Context context, final LoginActivity activity){
        String serverUrl = context.getString(R.string.server_url);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password );


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitInterface favoritesService=retrofit.create(RetrofitInterface.class);
        Call<ResponseBody> call = favoritesService.login(jsonObject);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        if(response.body().string().equals("Success")){
                            Log.i("logged in","success");
                            try {
                                activity.login(true,username,response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            try {
                                activity.login(false,username,response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    Log.e("logged in","failed");
                    try {
                        activity.login(false,username,response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("log-in failed","failed");
                activity.login(false,username,t.getMessage());
            }
        });


    }



}
