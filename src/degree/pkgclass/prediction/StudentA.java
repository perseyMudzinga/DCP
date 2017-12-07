package degree.pkgclass.prediction;
import java.util.*;

public class StudentA {
    public String name;
    public String file;
    public Set<Year> years = new LinkedHashSet<>();

    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    
}