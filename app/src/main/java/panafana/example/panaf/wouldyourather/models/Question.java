package panafana.example.panaf.wouldyourather.models;

import java.util.ArrayList;

public class Question {

    private String question;
    private String category;
    private String id;
    private Stats stats;
    private ArrayList<Comment> comments;

    public Question(String question,String category, String id,Stats stats,ArrayList<Comment> comments  ){
            this.category = category;
            this.question = question;
            this.id = id;
            this.stats = stats;
            this.comments = comments;
    }


    public String getQuestion(){return question;}

    public String getCategory(){return category;}

    public String getId(){return id;}

    public Stats getStats(){return stats;}

    public ArrayList<Comment> getComments(){return comments;}

    public void setQuestion(String question){this.question = question;}

    public void setCategory(String category){this.category = category;}

    public void setId(String id){this.id = id;}

    public void setStats(Stats stats){this.stats = stats;}

    public void setComments(ArrayList<Comment> comments){this.comments = comments;}

}
