package degree.pkgclass.prediction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class ParseMarks {
    private static BufferedReader in;
    private static BufferedWriter out;
    private static String arrayOne[];
    private static String arrayTwo[];
    private static String arrayThree[];
    private static String arrayFour[];
    
    public static void main(String[] args) throws IOException{
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\wamp\\www\\MASTER\\raw.txt")));
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\wamp\\www\\MASTER\\marks.txt")));
            String inputLine;
            int i = 0;
            while ((inputLine = in.readLine()) != null){
                
                switch(i){
                    case 0:
                        arrayOne = inputLine.split("\t");
                        break;
                    case 1:
                        arrayTwo = inputLine.split("\t");
                        break;
                    case 2:
                        arrayThree = inputLine.split("\t");
                        break;
                    case 3:
                        arrayFour = inputLine.split("\t");
                        break;
                }
                
                i+=1;
            }
            
            for(int k = 0; k < arrayOne.length; k++){
                //System.out.println(arrayFour.length);
                out.write("Student_"+k+": {"+arrayOne[k]+","+arrayTwo[k]+","+arrayThree[k]+","+arrayFour[k]+"} \n");
                System.out.println(arrayOne[k]+"\t"+arrayTwo[k]+"\t"+arrayThree[k]+"\t"+arrayFour[k]);
            }
            
            out.flush();
            out.close();
                
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        in.close();
    }
}
