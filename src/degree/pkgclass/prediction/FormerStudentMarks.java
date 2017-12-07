package degree.pkgclass.prediction;

public class FormerStudentMarks {
    private float yearOne;
    private float yearTwo;
    private float yearThree;
    private float yearFour;

    public FormerStudentMarks(int yearOne, int yearTwo, int yearThree, int yearFour) {
        this.yearOne = yearOne;
        this.yearTwo = yearTwo;
        this.yearThree = yearThree;
        this.yearFour = yearFour;
    }

    public float getYearOne() {
        return yearOne;
    }

    public void setYearOne(int yearOne) {
        this.yearOne = yearOne;
    }

    public float getYearTwo() {
        return yearTwo;
    }

    public void setYearTwo(int yearTwo) {
        this.yearTwo = yearTwo;
    }

    public float getYearThree() {
        return yearThree;
    }

    public void setYearThree(int yearThree) {
        this.yearThree = yearThree;
    }

    public float getYearFour() {
        return yearFour;
    }

    public void setYearFour(int yearFour) {
        this.yearFour = yearFour;
    }
    
    public float getFinalMark(){
        float y1 = (float) (this.yearOne * 0.1);
        float y2 = (float) (this.yearTwo * 0.3);
        float y3 = (float) (this.yearThree * 0.2);
        float y4 = (float) (this.yearFour * 0.4);
        return y1+y2+y3+y4;
    }
    
}
