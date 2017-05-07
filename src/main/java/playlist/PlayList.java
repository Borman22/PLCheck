package playlist;
import java.util.Scanner;
import java.io.File;
import java.util.Arrays;
import javax.swing.JOptionPane;

public class PlayList{
    static String[][] m = new String[1000][15];  // [event, strInEvent]
    static Scanner scn;
    static boolean flagEquals;
    static int strInEvent;
    static int event = 0;
    static int totalEvents = 0;

    public static void main(String[] args) {
        openFile();
        readFile();
        checkPlayListFile();
    }
    
    private static void openFile(){
            try{
                scn = new Scanner(new File("d://Borman//1"));
            } catch(Exception e){JOptionPane.showMessageDialog(null, "Файл не найден");}
        } 
    
    private static void readFile(){

        while(scn.hasNext()){
            strInEvent = 0;
            do{
                try{
                    m[event][strInEvent] = scn.nextLine();
                } catch(Exception e){
                    //System.out.println("Конец файла!");
                    return;
                }
                
                flagEquals = m[event][strInEvent++].equals("</event>");
            }while(!flagEquals);
            
            for(int index = 0; index < strInEvent; index++)
                //System.out.println("[" + event + ", " + index + "]" + m[event][index]);
            if(strInEvent == 9) m[event][14] = "9";
                else m[event][14] = "11";
            ++event;
            //System.out.println("------------------------ Событие №" + event + "      Строк в этом событии   " + strInEvent);
            totalEvents = event;
        }
    }
        
    static void checkPlayListFile(){ 
        System.out.println("Начинаем обработку");
        TimeCode TC = new TimeCode("06:00:06:23");
        String tempTime, tempDuration, tempTCin, tempTCout; 
        int totalFalse = 0;
        for(event = 1; event < totalEvents; event++){
            tempTCin = (m[event][2].substring(8, 19)); //time
            tempTime = tempTCin;
            tempDuration = m[event][3].substring(12, 23); //duration 
            if(m[event][14].equals("9")){
                if(event == totalEvents-1)
                    tempTCout = "06:00:00:00";
                else tempTCout = m[event+1][2].substring(8, 19); //time
            } else {
                tempTCin = tempTime = m[event][4].substring(9, 20); //time
                tempTCout = m[event][5].substring(10, 21); //duration 
              }
            //System.out.println("time = " + tempTime + "     in = " + tempTCin + "    out = " + tempTCout + "    dur = " + tempDuration);
            if(TC.checkTC(tempTCin, tempTCout, tempDuration) == false)
                totalFalse++;
        }
        System.out.println("Найденых ошибок: " + totalFalse);
    }
}

