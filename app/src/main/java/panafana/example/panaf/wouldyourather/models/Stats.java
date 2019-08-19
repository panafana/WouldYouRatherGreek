package panafana.example.panaf.wouldyourather.models;

public class Stats {

    private int male0;
    private int female0;
    private int other0;
    private int male1;
    private int female1;
    private int other1;

    public Stats(int male0,int female0,int other0,int male1,int female1,int other1){
        this.female0 = female0;
        this.female1 = female1;
        this.male0 = male0;
        this.male1 = male1;
        this.other0 = other0;
        this.other1 = other1;
    }

    public int getFemale0() {
        return female0;
    }

    public int getFemale1() {
        return female1;
    }

    public int getMale0() {
        return male0;
    }

    public int getMale1() {
        return male1;
    }

    public int getOther0() {
        return other0;
    }

    public int getOther1() {
        return other1;
    }

    public void setFemale0(int female0) {
        this.female0 = female0;
    }

    public void setFemale1(int female1) {
        this.female1 = female1;
    }

    public void setMale0(int male0) {
        this.male0 = male0;
    }

    public void setMale1(int male1) {
        this.male1 = male1;
    }

    public void setOther0(int other0) {
        this.other0 = other0;
    }

    public void setOther1(int other1) {
        this.other1 = other1;
    }
}

