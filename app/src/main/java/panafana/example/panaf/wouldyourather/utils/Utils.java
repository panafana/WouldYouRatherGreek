package panafana.example.panaf.wouldyourather.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Handler;

import com.github.ybq.android.spinkit.style.FadingCircle;

import panafana.example.panaf.wouldyourather.R;

public class Utils {

    ProgressDialog progress;


    public void show_progressBar(Context context) {

        progress = ProgressDialog.show(context, "", context.getResources().getString(R.string.please_wait), true);
        FadingCircle doubleBounce = new FadingCircle();
        doubleBounce.setColor(R.color.colorPrimary);
        doubleBounce.setBounds(0, 0, 100, 100);
        progress.setIndeterminateDrawable(doubleBounce);

    }
    public void hide_progressBar() {
        if(progress!=null){
            progress.dismiss();
        }
    }

    public boolean isNetworkAvailable(Context context) {
        boolean hasInternet;
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        hasInternet = connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
        return hasInternet;
    }

    public void AlertDialog(final Context context, String Msg, final String reason) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(Msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
