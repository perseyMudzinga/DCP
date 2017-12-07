package degree.pkgclass.prediction;
import java.io.*;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import org.codehaus.jackson.map.ObjectMapper;

public class ParseFile {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Set<Integer> yearArray = new LinkedHashSet<>();
    private String name;
    private Student student;
    private Year year;
    private Semester semester;
    private Course course;
    private static Years reqYears = null;
    private static final List<Student> students = new LinkedList<>();
    
    
    //private static BufferedWriter out;
    
    public static void main(String[] args) throws FileNotFoundException, IOException{
        ParseFile p = new ParseFile();
        p.getCourseList();
        
        for(ReqYear r: reqYears.getYears()){
            for(ReqCourse c: r.getSemesterOne()){
                System.out.println(c.getCourseName());
            }
        }
        
    }
    
    public void load(String file){
        if(file.contains("json")){
            try {
                File in = new File(file);
                Student tmpStudent = objectMapper.readValue(in, Student.class);
                ParseFile.students.add(tmpStudent);
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
        
    }
    
    public List<Student> getStudents(){
        return ParseFile.students;
    }
    
    public void getStudentFiles(){
        String pathString = "data\\";
        Path dir = Paths.get(pathString);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file: stream) {
                load(pathString+file.getFileName());
            }
        } catch (IOException | DirectoryIteratorException x) {
            System.err.println(x);
        }
    }
    
    public Years getCourseList(){
        String file = "resources\\courses.json";
        Years tmpReq = null;
        try {
            File in = new File(file);
            tmpReq = objectMapper.readValue(in, Years.class);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return tmpReq;
    }
    
}
