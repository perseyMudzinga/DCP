package degree.pkgclass.prediction;

import java.util.LinkedList;
import java.util.List;

public class ReqYear {
    private List<ReqCourse> semesterOne = new LinkedList<>();
    private List<ReqCourse> semesterTwo = new LinkedList<>();

    public List<ReqCourse> getSemesterOne() {
        return semesterOne;
    }

    public void setSemesterOne(List<ReqCourse> semesterOne) {
        this.semesterOne = semesterOne;
    }

    public List<ReqCourse> getSemesterTwo() {
        return semesterTwo;
    }

    public void setSemesterTwo(List<ReqCourse> semesterTwo) {
        this.semesterTwo = semesterTwo;
    }
}
