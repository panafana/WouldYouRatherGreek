package panafana.example.panaf.wouldyourather;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import panafana.example.panaf.wouldyourather.models.Comment;

public class MyListAdapter extends ArrayAdapter<Comment> {

    private final Activity context;
    private ArrayList<Comment> comments;

    public MyListAdapter(Activity context, ArrayList<Comment> comments) {
        super(context, R.layout.mylist,comments);
        this.context=context;
       this.comments= comments;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView userText = rowView.findViewById(R.id.userList);
        TextView commentText = rowView.findViewById(R.id.commentList);
        TextView dateText = rowView.findViewById(R.id.dateList);
        Comment com = comments.get(position);


        commentText.setText(comments.get(position).getComment());
        dateText.setText(comments.get(position).getDate());
        userText.setText(comments.get(position).getUser());

        return rowView;

    }
    public void updateAdapter(ArrayList<Comment> comments) {

        this.comments = comments;
        //this.remove(comment[]);
        //and call notifyDataSetChanged


    }
}
