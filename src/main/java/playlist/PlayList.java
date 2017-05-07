package playlist;
import java.util.Scanner;
import java.io.File;
import java.util.Arrays;
import javax.swing.JOptionPane;

public class PlayList{
    static String[][] m = new String[1000][15];  // [event, strOfEvent]
    static String[][] errors = new String[1000][13];
    static Scanner scn;
    static boolean flagEquals;
    static int event = 0;
    static int totalEvents = 0;
    static int strOfEvent;
    static String bufStrOfEvent;
    static int format = 0; 
    static int name = 0; 
    static int tc_in = 0; 
    //static int tc_out = 0;
    static int id = 0;    
    static int errPGMFormat = 0; 
    static int errLogoFormat = 0;
    static int errGOSTIFormat = 0;
    static int err100movFormat = 0;
    static int errTextAmerika = 0;
    static int totalErrTC = 0;
    static int errSP100movParnerMpg = 0;
    static int errSPGostiParnerMpg = 0;
    static int number = 0;

    public static void main(String[] args) {
        openFile();
        readFile();
        checkPlayListFileCobaltAsRun();
    // StringBuffer sb = new StringBuffer ();
    // sb.setLength(50);
    }
    
    
    
    private static void openFile(){
            try{
                scn = new Scanner(new File("d://Borman//1"));
            } catch(Exception e){JOptionPane.showMessageDialog(null, "Файл не найден");}
        } 
    
    private static void readFile(){
        while(scn.hasNext()){
            try{
                bufStrOfEvent = scn.nextLine();
                if (bufStrOfEvent.equals("<event>")){
                    m[event][14] = Integer.toString(strOfEvent);
                    event++;
                    strOfEvent = 0;
                } 
            m[event][strOfEvent++] = bufStrOfEvent;
                //System.out.println("[" + event + ", " + (strOfEvent-1) + "]" + m[event][strOfEvent-1]);
            } catch(Exception e){    //Скорее всего catch никогда не выполнится
                return;
            }
        }
        m[event][14] = Integer.toString(strOfEvent);
        totalEvents = ++event;
    }
             
    static void checkPlayListFileCobaltAsRun(){ 
        checkTCCobaltAsRun();
        checkFormats();
        statistic();
    }
    
    static void checkTCCobaltAsRun(){  
        TimeCode TC = new TimeCode("06:00:00:00");
        String tempTime, tempDuration, tempTCin, tempTCout; 
        for(event = 1; event < totalEvents; event++){
            tempTime = (m[event][2].substring(8, 19)); //time
            tempTCin = tempTime;
            number++;
            errors[event][0] = number + ". [Time = " + tempTime;
            tempDuration = m[event][3].substring(12, 23); //duration 
            if(m[event][14].equals("9") || m[event][14].equals("10")){
                if(event == totalEvents-1)
                 tempTCout = "06:00:00:00";
                else tempTCout = m[event+1][2].substring(8, 19); //time
            } else {
                tempTCin = m[event][4].substring(9, 20); //tc_in
                tempTCout = m[event][5].substring(10, 21); //tc_out 
              }
    //        System.out.println("time = " + tempTime + "     in = " + tempTCin + "    out = " + tempTCout + "    dur = " + tempDuration);
    //        System.out.print("Time = " + tempTime + ",  ");
            errors[event][1] = ",  IN = " + tempTCin;
            errors[event][2] = ",  OUT = " + tempTCout;
            errors[event][3] = ",  DUR = " + tempDuration + "]";
            if(TC.checkTC(tempTCin, tempTCout, tempDuration) == false){
                totalErrTC++;
                errors[event][4] = " is false   "; 
            }
            else errors[event][4] = " is true   ";
        }
    }
    
    
    static void checkFormats(){
        for(event = 1; event < totalEvents; event++ ){
            if(m[event][14].equals("9") || m[event][14].equals("10")){

                name = 5;
                format = 6;
                tc_in = format;
            } else {
                tc_in = 4; 
                name = 7;
                format = 8;
                
            }
            
            if(m[event][format].contains("PGM")){
                //errors[event][5] = "PGM: true   ";                 
            } else {
                errors[event][5] = "PGM: false   ";
                errPGMFormat++;
            }

            if(m[event][format].contains("logo v2") || m[event][format].contains("logo Traur")){
                //errors[event][6] = "Logo v2/logo Traur: true   ";                 
            } else {
                errors[event][6] = "Logo v2/logo Traur: false   ";
                errLogoFormat++;
            } 
        
            // partner GOSTI
            if((m[event][name].contains("Jamies") || m[event][name].contains("Oliver")) & !m[event][name].contains(">A-")){
                if(m[event][tc_in].contains("00:00:00:00")){
                        if(!m[event+1][5].contains("SP-GOSTI")) {
                        errSPGostiParnerMpg++;
                        errors[event+1][9] = "SP-GOSTI.mpg: false   ";
                    }// else errors[event+1][9] = "SP-GOSTI.mpg: true   ";
                    //      ( m[event][format].contains("partner GOSTI") ) ? {errors[event][7] = "GOSTI: false   "; errGOSTIFormat++;} : (errors[event][7] = "GOSTI: true   ";)
                    if(m[event][format].contains("partner GOSTI")){
                        errors[event][7] = "GOSTI: false   ";
                            errGOSTIFormat++;
                        }// else errors[event][7] = "GOSTI: true   ";
                } else if(m[event][format].contains("partner GOSTI"))
                    /*errors[event][7] = "GOSTI: true   "*/;
                    else { errors[event][7] = "GOSTI: false   ";
                        errGOSTIFormat++;
                    }
            } 
            
       //*  // partner 100mov
            if((m[event][name].contains("Anatomiya") | m[event][name].contains("Amerika")) & !m[event][name].contains(">A-")){
                if(m[event][tc_in].contains("00:00:00:00")){
                    if(!m[event+1][5].contains("100movPartner")) {
                        errSP100movParnerMpg++;
                        errors[event+1][9] = "SP-100movPartner.mpg: false   ";
                    }// else errors[event+1][9] = "SP-100movPartner.mpg: true   ";
        //      ( m[event][format].contains("partner GOSTI") ) ? {errors[event][7] = "GOSTI: false   "; errGOSTIFormat++;} : (errors[event][7] = "GOSTI: true   ";)
                    if(m[event][format].contains("partner 100mov")){
                        errors[event][7] = "100mov: false   ";
                            err100movFormat++;
                        }// else errors[event][7] = "100mov: true   ";
                } else if(m[event][format].contains("partner 100mov"))
                    /*errors[event][7] = "100mov: true   "*/;
                    else { errors[event][7] = "100mov: false   ";
                        err100movFormat++;
                    }
            } 
            
            // text for Americas got talent
            if((m[event][name].contains("Amerika")) & !m[event][name].contains("A-")){
                    if(m[event][format].contains("text for Americas"))
                        /*errors[event][8] = "text for Americas: true   "*/;
                    else { errors[event][8] = "text for Americas: false   ";
                        errTextAmerika++;
                    }
            } 
            
            
            
            //*/
            
            
            
        }
    }
    
    static void statistic(){
        for(event = 0; event < totalEvents; event++){
            for(int i = 0; i < 13; i++){
                if (errors[event][i] != null)
                    System.out.print(errors[event][i]);   
            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println("Ошибок в таймкоде: " + totalErrTC);
        System.out.println("Ошибок в format  PGM: " + errPGMFormat);
        System.out.println("Ошибок в format  logo: " + errLogoFormat);
        System.out.println("Ошибок в format  partner GOSTI: " + errGOSTIFormat);
        System.out.println("Ошибок в format  partner 100mov: " + err100movFormat);
        System.out.println("Ошибок в format  text for Americas got talent: " + errTextAmerika);
        System.out.println("Ошибок в SP-100movPartner.mpg: " + errSP100movParnerMpg);
        System.out.println("Ошибок в SP-GOSTI.mpg: " + errSPGostiParnerMpg);
        
    }
    
}

