package degree.pkgclass.prediction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;

public class JSonTester {
    public static void main(String[] args){
        
        try {
            ParseFiles parse = new ParseFiles();
            List<Student> students = new LinkedList<>();
            parse.getStudentFiles();
            students = parse.getStudents();
            ObjectMapper objectMapper = new ObjectMapper();
            
            
            
            for(Student s: students){
            objectMapper.writeValue(new FileOutputStream("src/resources/students/"+s.getName().replace(' ', '_').toLowerCase()+".json"), s);
            }
            
            
            
            
            
            
            } catch (IOException ex) {
            Logger.getLogger(JSonTester.class.getName()).log(Level.SEVERE, null, ex);
            }
           /* ObjectMapper objectMapper = new ObjectMapper();
            File file = new File("src/resources/students/kushinga_mukabeta.json");
            StudentA car = objectMapper.readValue(file, StudentA.class);
            car.getYears().stream().forEach((y) -> {
                y.getSemesters().stream().forEach((s) -> {
                    s.getCourses().stream().forEach((c) -> {
                        System.out.println(c.getCourseCode());
                    });
                });
            });
        } catch (IOException ex) {
            Logger.getLogger(JSonTester.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
    }
}
