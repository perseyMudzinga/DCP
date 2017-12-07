package degree.pkgclass.prediction;
import java.util.*;

public class Student {
    private String name;
    private String file;
    private final Set<Year> years = new LinkedHashSet<>();
    private int currentYear = 0;
    private int firstYear = 0;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public int getCurrentYear(){
        return currentYear;
    }
    
    public int getFirstYear(){
        return firstYear;
    }
    
    
    public Set<Year> getYears() {
        return years;
    }

    public void addYear(Year year) {
        this.years.add(year);
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
    
    @Override
    public String toString() {
        return this.name;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public void setFirstYear(int firstYear) {
        this.firstYear = firstYear;
    }
    
}
