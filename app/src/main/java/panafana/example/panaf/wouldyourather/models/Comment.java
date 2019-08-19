package panafana.example.panaf.wouldyourather.models;


public class Comment {

    private String comment;
    private String date;
    private String user;

    public Comment(String comment, String date, String user){

        this.comment = comment;
        this.date = date;
        this.user = user;
    }

    public String getComment(){return comment;}

    public String getDate(){return date;}

    public String getUser(){return user;}

    public void setComment(String comment){this.comment = comment;}

    public void setDate(String date){this.date = date;}

    public void setUser(String user){this.user = user;}
}
