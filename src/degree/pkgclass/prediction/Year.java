package degree.pkgclass.prediction;
import java.util.*;

public class Year {
    private int year;
    private Set<Semester> semesters = new LinkedHashSet<>();


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Set<Semester> getSemesters() {
        return semesters;
    }

    public void addSemester(Semester semester) {
        if(this.semesters.size()<=2){
            this.semesters.add(semester);
        }else{
            System.out.println("A year takes only two semesters.");
        }
    }
    
    
}
