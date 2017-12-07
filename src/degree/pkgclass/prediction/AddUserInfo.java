package degree.pkgclass.prediction;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;

public class AddUserInfo {
    private final ObjectMapper objectMapper = new ObjectMapper();
    Set<Integer> years = new LinkedHashSet<>();
    
    
    public void addUser(Student student){
        try {
            student.setFile(student.getName().replace(' ', '_').toLowerCase()+".json");
            objectMapper.writeValue(new FileOutputStream("data\\"+student.getName().replace(' ', '_').toLowerCase()+".json"), student);
            System.out.println(student.getCurrentYear());
        } catch (IOException ex) {
            Logger.getLogger(AddUserInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void addYear(int year){
        this.years.add(year);
    }
    
    public void addCourses(Student student){
        try {
            objectMapper.writeValue(new FileOutputStream("data\\"+student.getName().replace(' ', '_').toLowerCase()+".json"), student);
        } catch (IOException ex) {
            Logger.getLogger(AddUserInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
