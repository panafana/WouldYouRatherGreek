package panafana.example.panaf.wouldyourather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import panafana.example.panaf.wouldyourather.R;
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
        Call<ResponseBody> getQuestions(@Url String fileUrl);

        @GET("/android/get-animals")
        Call<ResponseBody> getAnimals();
    }


    public void getQuestions(final Context context){
        final String serverUrl = context.getString(R.string.server_url);
        final Utils utils = new Utils();
        final SharedPreferences SP = context.getSharedPreferences("questions", MODE_PRIVATE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitInterface service=retrofit.create(RetrofitInterface.class);
        Call<ResponseBody> call = service.getQuestions();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {
                    Log.i("animals received","success");
                    try {
                        String resp =response.body().string();
                        System.out.println("resp "+resp);

                        JSONArray categories =new JSONArray(resp);
                        SharedPreferences.Editor editor = SP.edit();
                        SharedPreferences.Editor editor2 = SP2.edit();
                        ArrayList<Animal> allanimals = new ArrayList<>();

                        ArrayList<Category> allcategories = new ArrayList<>();
                        int size = categories.length();
                        for(int i =0;i<size;i++){

                            System.out.println("animals "+categories.getJSONObject(i).get("animals"));
                            JSONArray animals =  categories.getJSONObject(i).getJSONArray("animals");
                            ArrayList<Animal> categoryAnimals = new ArrayList<>();
                            for(int j = 0;j<animals.length();j++){
                                total_downloads++;
                                System.out.println("animals "+animals.get(j));
                                JSONArray jsonSounds = animals.getJSONObject(j).getJSONArray("sound");
                                List<String> sounds = new ArrayList<>();
                                for(int k=0;k<jsonSounds.length();k++){
                                    sounds.add(jsonSounds.getString(k));
                                    donwloadMp3(context,jsonSounds.getString(k));
                                }
                                Animal animal = new Animal(animals.getJSONObject(j).getString("name"),
                                        animals.getJSONObject(j).getString("image"),
                                        sounds,
                                        animals.getJSONObject(j).getString("category"));
                                allanimals.add(animal);

                                categoryAnimals.add(animal);
                                //pre-caching images
                                Picasso.get().load(serverUrl + "images/"+animal.getImage()).fetch();

                            }
                            Category cat = new Category(categories.getJSONObject(i).getString("name"),categories.getJSONObject(i).getString("image"),categoryAnimals);
                            allcategories.add(cat);
                            //pre-caching images
                            Picasso.get().load(serverUrl + "images/"+cat.getImage()).fetch();
                            categoryAnimals.clear();

                        }
                        Gson gson1 = new Gson();
                        String json1 = gson1.toJson(allanimals);
                        editor.putString("allanimals",json1);
                        editor.apply();
                        System.out.println("animal stored");
                        Gson gson2 = new Gson();
                        Category cat = new Category("All Animals","all.jpg",allanimals);
                        allcategories.add(cat);
                        Picasso.get().load(serverUrl + "images/"+cat.getImage()).fetch();
                        String json2 = gson2.toJson(allcategories);
                        editor2.putString("allcategories",json2);
                        editor2.apply();
                        System.out.println("categories stored");

                        ((MainActivity)context).dataLoaded();

                        SharedPreferences SP3 = context.getSharedPreferences("preferences", MODE_PRIVATE);
                        SP3.edit().putBoolean("updated",true).apply();

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
                super.onFailure(call,t);
                Log.e("call",t.getMessage());
                Log.e("animals received","failed");

            }
        });

    }


}
