package degree.pkgclass.prediction;

import java.util.LinkedList;
import java.util.List;


public class CalculateUAv {
    
    private int semestersAvailable;
    private float totalMark = 0;
    private int numOfCourses = 0;
    private float average;
    private final int yearsAvailable = 0;
    private final List<Float> marks = new LinkedList<>();
    private final List<Float> markList = new LinkedList<>();
    Computations compute = new Computations();
    private String finalGrade;
    private float finalMark;
    
    
    public void processYears(Student stu){
        totalMark = 0;
        numOfCourses = 0;
        average = 0;
        semestersAvailable = 0;
        markList.clear();
        for(Year y: stu.getYears()){
            semestersAvailable+=getSemNumber(y);
        }
        makePredictions();
    }
    
    public int getNumberOfSemesters(){
        return semestersAvailable;
    }
    
    private int getSemNumber(Year y){
        float a = 0;
        float av = 0;
        int numOfCourses = 0;
        if(!y.getSemesters().isEmpty()){
            
            for(Semester s: y.getSemesters()){
                for(Course c: s.getCourses()){
                    a+=c.getCourseMark();
                }
                numOfCourses += s.getCourses().size();
            }
            av = a / numOfCourses;
            markList.add(av);
        }
        
        return y.getSemesters().size();
    }
    
    private float getAverage(){
        average = totalMark/numOfCourses;
        numOfCourses = 0;
        totalMark = 0;
        return average;
    }
    
    public void makePredictions(){
        Object[] a = markList.toArray();
        if(a.length == 1){
            compute.predict((float) a[0]);
            this.finalGrade = compute.getFinalGrade();
            this.finalMark = compute.getFinalMark();
        }else if(a.length == 2){
            compute.predict((float) a[0],(float) a[1]);
            this.finalGrade = compute.getFinalGrade();
            this.finalMark = compute.getFinalMark();
        }else if(a.length > 2){
            compute.predict((float) a[0], (float) a[1], (float) a[2]);
            this.finalGrade = compute.getFinalGrade();
            this.finalMark = compute.getFinalMark();
        }
    }

    public int getYearsAvailable() {
        return yearsAvailable;
    }

    public String getGrade() {
        return this.finalGrade;
    }

    public float getFinalMark() {
        return finalMark;
    }
    
    
}
