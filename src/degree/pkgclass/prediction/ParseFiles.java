package degree.pkgclass.prediction;
import java.io.*;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ParseFiles {
    
    private static BufferedReader in;
    private final Set<Integer> yearArray = new LinkedHashSet<>();
    private String name;
    private Student student;
    private Year year;
    private Semester semester;
    private Course course;
    private static final List<Student> students = new LinkedList<>();
    
    
    //private static BufferedWriter out;
    
    public static void main(String[] args) throws FileNotFoundException, IOException{
        new ParseFile().getStudentFiles();
        students.stream().forEach((s) -> {
            System.out.println(s.getName());
        });
    }
    
    public void load(String file) throws FileNotFoundException, IOException{
        in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String inLine ;
        String wholeLine = "";
        String[] lineArray;
        
        while((inLine = in.readLine()) != null){
            wholeLine += inLine;
        }
        
        lineArray = wholeLine.split(";");
        for(String s: lineArray){
            if(!wholeLine.equals("") && s.length()>4){
                //Name
                if(s.substring(0, 4).equals("name")){
                    name = s.substring(s.indexOf("=")+1, s.length());
                    student = new Student();
                    student.setName(name);
                    student.setFile(file);
                    students.add(student);
                }

                //Years
                if(s.substring(0, 4).equals("year")){
                    String strYear = s.substring(s.indexOf("=")+1, s.length());
                    int localYear = Integer.parseInt(strYear);

                    this.year = new Year();
                    this.year.setYear(localYear);
                    student.addYear(this.year);
                    yearArray.add(localYear);
                }

                //Semester
                if(s.substring(0, 8).equals("semester")){
                    String strSem = s.substring(s.indexOf("=")+1, s.length());
                    String semNumberStr = s.substring(0,s.indexOf("="));
                    String[] firstPart = strSem.split(",,");
                    int localYear = 0;
                    
                    this.semester = new Semester();
                    if(semNumberStr.equals("semester1")){
                        this.semester.setSemesterNumber(1);
                    }else if(semNumberStr.equals("semester2")){
                        this.semester.setSemesterNumber(2);
                    }
                    for(String a: firstPart){

                        if(a.substring(0, 4).equals("year")){
                            String strYear = a.substring(a.indexOf("__")+2, a.length());
                            localYear = Integer.parseInt(strYear);

                        }
                        if(a.substring(0, 6).equals("course")){
                            String[] secondPart = a.split("--");
                            for(String b: secondPart){
                                String[] thirdPart = b.split("%");
                                String code = thirdPart[0].substring(thirdPart[0].indexOf("__")+2, thirdPart[0].length());
                                String cname = thirdPart[1].substring(thirdPart[1].indexOf("__")+2, thirdPart[1].length());
                                String cgrade = thirdPart[2].substring(thirdPart[2].indexOf("__")+2, thirdPart[2].length());
                                //System.out.println(year+"\t"+semNumberStr+"\t"+code+"\t"+cname+"\t"+cgrade);
                                course = new Course();
                                course.setCourseCode(code);
                                course.setCourseNarration(cname);
                                course.setCourseMark(Float.parseFloat(cgrade));

                                for(Year y: student.getYears()){
                                    if(y.getYear() == localYear){  
                                        this.semester.addCourse(course);
                                        /*if(y.getSemesters().isEmpty()){
                                            y.addSemester(semester);
                                        }else{
                                            for(Semester se:y.getSemesters()){
                                                if(se.getSemesterNumber() != this.semester.getSemesterNumber()){
                                                    y.addSemester(semester);
                                                }
                                            }
                                        }*/
                                        y.addSemester(semester);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
           
        }
    }
    
    public List<Student> getStudents(){
        return ParseFiles.students;
    }
    
    public void getStudentFiles(){
        String pathString = "src/resources/students/";
        Path dir = Paths.get(pathString);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file: stream) {
                load(pathString+file.getFileName());
            }
        } catch (IOException | DirectoryIteratorException x) {
            System.err.println(x);
        }
    }
    
    
    
}
