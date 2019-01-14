package com.example.panaf.wouldyourather;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private String[] comment;
    private  String[] user;
    private  String[] date;

    public MyListAdapter(Activity context, String[] comment,String[] user, String[] date) {
        super(context, R.layout.mylist, comment);
        this.context=context;
        this.comment=comment;
        this.user=user;
        this.date=date;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView userText = (TextView) rowView.findViewById(R.id.userList);
        TextView commentText = rowView.findViewById(R.id.commentList);
        TextView dateText = rowView.findViewById(R.id.dateList);
        commentText.setText(comment[position]);
        dateText.setText(date[position]);
        userText.setText(user[position]);

        return rowView;

    }
    public void updateAdapter(String[] comment,String[] user, String[] date) {
        this.comment=comment;
        this.user=user;
        this.date=date;
        //this.remove(comment[]);
        //and call notifyDataSetChanged
        this.notifyDataSetChanged();

    }
}
