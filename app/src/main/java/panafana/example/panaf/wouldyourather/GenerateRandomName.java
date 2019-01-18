package panafana.example.panaf.wouldyourather;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class GenerateRandomName {
    String finalname;

    @RequiresApi(api = Build.VERSION_CODES.O)
    GenerateRandomName(Context ctx, int choice) throws IOException {
        // Open the file
        ArrayList<String> maleNames = new ArrayList<String>();
        ArrayList<String> femaleNames = new ArrayList<String>();
        ArrayList<String> maleAdj = new ArrayList<String>();
        ArrayList<String> femaleAdj = new ArrayList<String>();

        AssetManager assets = ctx.getAssets();
        InputStream is = assets.open("maleNames.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String strLine;

        //Read File Line By Line
        while ((strLine = br.readLine()) != null)   {
            // Print the content on the console
            maleNames.add(strLine);

        }
        //Close the input stream
        br.close();

        InputStream is2 = assets.open("maleAdj.txt");
        BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));

        String strLine2;

        //Read File Line By Line
        while ((strLine2 = br2.readLine()) != null)   {
            // Print the content on the console
            maleAdj.add(strLine2);
            //System.out.println(strLine2);

        }
        //Close the input stream
        br2.close();

        InputStream is3 = assets.open("femaleNames.txt");
        BufferedReader br3 = new BufferedReader(new InputStreamReader(is3));

        String strLine3;

        //Read File Line By Line
        while ((strLine3 = br3.readLine()) != null)   {
            // Print the content on the console
            femaleNames.add(strLine3);
            //System.out.println(strLine2);

        }
        //Close the input stream
        br3.close();

        InputStream is4 = assets.open("femaleAdj.txt");
        BufferedReader br4 = new BufferedReader(new InputStreamReader(is4));

        String strLine4;

        //Read File Line By Line
        while ((strLine4 = br4.readLine()) != null)   {
            // Print the content on the console
            femaleAdj.add(strLine4);
            //System.out.println(strLine2);

        }
        //Close the input stream
        br4.close();

        final int max,max2;
        if(choice ==0){
            max2 = maleAdj.size();
            max = maleNames.size();
        }else if (choice ==1 ){
            max2 = femaleAdj.size();
            max = femaleNames.size();
        }else {
            max2 = maleAdj.size();
            max = maleNames.size();
        }

        final int min = 0;

        final int randomName = new Random().nextInt((max - min) + 1) + min;
        final int min2 = 0;

        final int random2Surname = new Random().nextInt((max2 - min2) + 1) + min2;

        String chosenName ;
        String chosenAdj;
        if(choice ==0){
            chosenName = maleNames.get(randomName);
            chosenAdj = maleAdj.get(random2Surname);

        }else if (choice ==1 ){

             chosenName = femaleNames.get(randomName);
             chosenAdj = femaleAdj.get(random2Surname);
        }else {

             chosenName = maleNames.get(randomName);
             chosenAdj = femaleAdj.get(random2Surname);
        }

        chosenName= chosenName.substring(0,1).toUpperCase()+chosenName.substring(1).toLowerCase();
        chosenAdj = chosenAdj.substring(0,1).toUpperCase()+chosenAdj.substring(1).toLowerCase();
        System.out.println("name "+chosenName);
        System.out.println("adj "+chosenAdj);
        byte[] temp = chosenAdj.getBytes();
        System.out.println("adj2 "+(new String(temp,"UTF-8")));
        finalname=chosenAdj+" "+chosenName;

    }
    String getName(){
        return this.finalname;
    }


}
