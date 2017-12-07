package degree.pkgclass.prediction;

import java.util.*;

public class Semester {
    private int semesterNumber;
    private List<Course> courses = new LinkedList<>();

    public int getSemesterNumber() {
        return semesterNumber;
    }

    public void setSemesterNumber(int semesterNumber) {
        this.semesterNumber = semesterNumber;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void addCourse(Course course) {
        this.courses.add(course);
    }
    
}