package degree.pkgclass.prediction;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Computations {
    
    List<FormerStudentMarks> formerMarks = new LinkedList<>();
    private float[] tmpMarkArray;
    private float finalMark;
    
    public Computations(){
        try {
            getMarksFromFile();
            //predict(78);
        } catch (IOException ex) {
            //
        }
    }
    
    private void getMarksFromFile() throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\wamp\\www\\MASTER\\marks.txt")));
        String inputLine;
        
        while((inputLine = reader.readLine())!= null){
            String[] tmpArray = inputLine.split("}");
            for(String c: tmpArray){
                if(-1 != c.indexOf("{")){
                    String cn = c.substring(c.indexOf("{")+1, c.length());
                    String[] cutToNumbers = cn.split(",");
                    int[] nums = new int[4];
                    
                    for(int i = 0; i<4; i++){
                        nums[i] = Integer.parseInt(cutToNumbers[i]);
                    }
                    
                    FormerStudentMarks fs = new FormerStudentMarks(nums[0],nums[1],nums[2],nums[3]);
                    formerMarks.add(fs);
                    
                }
            }
        }
    }
    
    public void learn(FormerStudentMarks fsm){
        formerMarks.add(fsm);
    }
    
    public final void predict(float y1){
        float y12Average = y1;
        tmpMarkArray = new float[formerMarks.size()+1];
        int index = 0;
        boolean isAvailable = false;
        float av = 0;
        int avCount = 0;
        
        for (FormerStudentMarks marks: formerMarks){
            float mAverage = (marks.getYearOne());
            
            if(mAverage == y12Average){
                av += y12Average;
                avCount += 1;
                isAvailable = true;
            }
            
            tmpMarkArray[index]=mAverage;
            index +=1;
        }
        
        if(av != 0){
            this.finalMark = av/avCount;
        }
        
        if(!isAvailable){
            tmpMarkArray[tmpMarkArray.length-1] = y12Average;
            //System.out.println(tmpMarkArray[tmpMarkArray.length-1]);
            sortArray();
            getClass(y12Average);
        }
    }
    
    public final void predict(float y1, float y2){
        float y12Average = (y1+y2)/2;
        tmpMarkArray = new float[formerMarks.size()+1];
        int index = 0;
        boolean isAvailable = false;
        float av = 0;
        int avCount = 0;
        
        for (FormerStudentMarks marks: formerMarks){
            float mAverage = (marks.getYearOne()+marks.getYearTwo())/2;
            
            if(mAverage == y12Average){
                av += y12Average;
                avCount += 1;
                isAvailable = true;
            }
            
            tmpMarkArray[index]=mAverage;
            index +=1;
        }
        
        if(av != 0){
            this.finalMark = av/avCount;
        }
        
        if(!isAvailable){
            tmpMarkArray[tmpMarkArray.length-1] = y12Average;
            sortArray();
            getClass(y12Average);
        }
    }
    
    public final void predict(float y1, float y2, float y3){
        float y12Average = (y1+y2+y3)/3;
        tmpMarkArray = new float[formerMarks.size()+1];
        int index = 0;
        boolean isAvailable = false;
        float av = 0;
        int avCount = 0;
        
        for (FormerStudentMarks marks: formerMarks){
            float mAverage = (marks.getYearOne()+marks.getYearTwo()+marks.getYearThree())/3;
            
            if(mAverage == y12Average){
                av += y12Average;
                avCount += 1;
                isAvailable = true;
            }
            
            tmpMarkArray[index]=mAverage;
            index +=1;
        }
        
        if(av != 0){
            this.finalMark = av/avCount;
        }
        
        if(!isAvailable){
            tmpMarkArray[tmpMarkArray.length-1] = y12Average;
            sortArray();
            getClass(y12Average);
        }
    }
    
    private void getClass(float avg){
        float upperBound = 0;
        float lowerBound = 0;
        for(int i = 0; i < tmpMarkArray.length; i++){
            if(tmpMarkArray[i] == avg){
                if(i+1 == tmpMarkArray.length ){
                    lowerBound = tmpMarkArray[i-1];
                }else if(i == 0){
                    upperBound = tmpMarkArray[i+1];
                }else{
                    upperBound = tmpMarkArray[i+1];
                    lowerBound = tmpMarkArray[i-1];
                }
                
                break;
            }
        }
        if(upperBound != 0 && lowerBound != 0){
            this.finalMark = (upperBound+lowerBound) / 2;
        }else if(upperBound != 0){
            this.finalMark = upperBound;
        }
        else if(lowerBound != 0){
            this.finalMark = lowerBound;
        }
    }
    
    private void sortArray(){
        int end = 0;
        while(true){
            for(int i = 1; i < tmpMarkArray.length; i++){
                if(tmpMarkArray[i] < tmpMarkArray[i-1]){
                    swap(i, i-1);
                }
            }
            end+=1;
            if(end == tmpMarkArray.length){
                break;
            }
        }
    }
    
    private void swap(int a, int b){
        float mark1 = tmpMarkArray[a];
        tmpMarkArray[a] = tmpMarkArray[b];
        tmpMarkArray[b] = mark1;
    }
    
    public float getFinalMark(){
        return this.finalMark;
    }
    
    public String getFinalGrade(){
        if(this.finalMark < 50){
            return "F";
        }
        else if(this.finalMark < 60){
            return "3";
        }
        else if(this.finalMark < 70){
            return "2.2";
        }
        else if(this.finalMark < 80){
            return "2.1";
        }
        else if(this.finalMark >= 80){
            return "1";
        }
        else{
            return "Invalid";
        }
    }
    
}
