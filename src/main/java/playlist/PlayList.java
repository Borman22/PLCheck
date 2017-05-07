package playlist;
import java.util.Scanner;
import java.io.File;
import java.util.Arrays;
import javax.swing.JOptionPane;

public class PlayList{
    static String[][] m = new String[1000][15];  // [event, strOfEvent]
    static Event [] evnt = new Event[1000];
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
    static int errTextAmerikaON = 0;
    static int errTextAmerikaOFF = 0;
    static int totalErrTC = 0;
    static int totalErrDUR = 0;
    static int errSP100movParnerMpg = 0;
    static int errSPGostiParnerMpg = 0;
    static int number = 0;

    public static void main(String[] args) {
        openFile();
        readFile();
        checkPlayListFileCobaltAsRun();
    // StringBuffer sb = new StringBuffer ();
    // sb.setLength(50);
   
    //evnt[1] = new Event();
    //evnt[1].setDate("<date>2015-06-24</date>");
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
                            //System.out.println(m[event][14]);
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
        
        

        
        for(int i = 1; i < totalEvents; i++){
            evnt[i] = new Event();
            evnt[i].setDate(m[i][1]);
            evnt[i].setTime(m[i][2]);
            evnt[i].setDuration(m[i][3]);
            
            if(m[i][14].equals("9") || m[i][14].equals("10")){
                evnt[i].eventType = 9;
                evnt[i].setTc_in(0);
                
                String TCOutTemp;
                
                if(i < (totalEvents-1))
                    TCOutTemp = evnt[i].strContents(m[i+1][2], "<time>", "</time>");
                else 
                    TCOutTemp = "06:00:00:00";

                int tempTC_in = TCStrToFrame(evnt[i].strContents(m[i][2], "<time>", "</time>"));
                int tempTC_out = TCStrToFrame(TCOutTemp);
                        
                if(tempTC_out < tempTC_in)
                    tempTC_out += 2160000;
                    evnt[i].setTc_out(TCInFrameToIntStr(tempTC_out - tempTC_in));

                evnt[i].setAsset_id(m[i][4]);
                evnt[i].setName(m[i][5]);
                evnt[i].setFormats(evnt[i].strContents(m[i][6], "<format>", "</format>").split(", "));

                evnt[i].setStatus(m[i][7]);
                
            } else {
                evnt[i].eventType = 11;
                evnt[i].setTc_in(m[i][4]);
                evnt[i].setTc_out(m[i][5]);
                evnt[i].setAsset_id(m[i][6]);
                evnt[i].setName(m[i][7]);
                evnt[i].setFormats(evnt[i].strContents(m[i][8], "<format>", "</format>").split(", "));
                evnt[i].setStatus(m[i][9]);
            }
        }
        
    }

    static int TCStrToFrame(String s){
        char [] array = s.toCharArray();
        char arrayTemp [] = new char[8];       

        if(s.length() != 11){
            System.out.println("Таймкод должен быть в формате HH:MM:SS:FF. TC = 00:00:00:00.");
            return 0;
        }
        
        if((array[2] & array[5] & array[8]) != ':'){
            System.out.println("Таймкод должен быть в формате HH:MM:SS:FF. TC = 00:00:00:00.");
            return 0;
        }

        for(int i = 0, j = 0; i<11; i++){
            if(array[i] >= '0' & array[i] <= '9'){
                arrayTemp[j++] = array[i];
            } else if(i == 2 | i == 5 | i==8){
              //  continue;
            } else {
                System.out.println("Таймкод должен быть в формате HH:MM:SS:FF. TC = 00:00:00:00.");
                return 0;
            } 
        }
        int i = Integer.parseInt(new String(arrayTemp));
        
        int hh, mm, ss, ff;
         hh = i / 1_000_000;    // часы
        i = i - 1_000_000 * hh;
         mm = i / 10_000;  // минуты
        i = i - 10_000 * mm;
         ss = i / 100;    // секунды
         ff = i - 100 * ss; // кадры  
        return 90_000 * hh + 1500 * mm + 25 * ss + ff;
    }
    static int TCInFrameToIntStr(int i){
        int hh, mm, ss, ff;
        hh = mm = ss = ff = 0;
        hh = i/90_000;
        i = i - 90_000 * hh;
        mm = i/1500;
        i = i - 1500 * mm;
        ss = i/25;
        ff = i - 25 * ss;
        return 1_000_000 * hh + 10_000 * mm + 100 * ss + ff;
    }
     
    static void checkPlayListFileCobaltAsRun(){ 
        checkTCCobaltAsRun(evnt);
        checkFormats(evnt);
        statistic(evnt);
    }
    
    static void checkTCCobaltAsRun(Event[] events){  //доделать проверку TC_in - TC_out - TC_in
        for(event = 1; event < totalEvents; event++){
            if(events[event].getDuration() != (events[event].getTc_out() - events[event].getTc_in())){
                totalErrDUR++;
                errors[event][0] = "   DUR_ERROR!";
            }
               // errors[event][1] = "IN/OUT_SUBCLIP_ERROR";
               //     System.out.println(Arrays.deepToString(events[event].getFormat()));
               // System.out.println("");
        }
    }    

    static void checkFormats(Event[] events){
        int tempPGM;
        int tempLogo;
        int tempPartnerGostiFirst; //partner GOSTI в первом субклипе - ошибка.
        int tempPartnerGostiNext;  // отсутствует partner GOSTI в последующих субклипах - ошибка.
        int tempPartner100movFirst;
        int tempPartner100movNext;
        int tempAmericasGotTalentON;
        int tempAmericasGotTalentOFF;
        boolean thisIsNextSubclip;
        
        for(event = 1; event < totalEvents; event++ ){
            tempPGM = 0;
            tempLogo = 0;
            tempPartnerGostiFirst = 0;
            tempPartnerGostiNext = 0;
            tempPartner100movFirst = 0;
            tempPartner100movNext = 0;
            tempAmericasGotTalentON = 0;
            tempAmericasGotTalentOFF = 0;
            thisIsNextSubclip = false;
            
            String [] tempFormat = events[event].getFormat();
                                                                            //System.out.println(Arrays.deepToString(tempFormat));
            for(int i = 0; i < tempFormat.length; i++){
                                                                            //System.out.println(tempFormat[i]);
                if(tempFormat[i].equals("PGM"))
                    tempPGM++;

                if( tempFormat[i].equals("logo v2") || tempFormat[i].equals("logo Traur") )
                    tempLogo++;
            }
            
            if(event != 1){      //2 субклипа подряд или нет
                boolean sameName = false;
                int tempIndexOf;
                String currentName = events[event].getName();
                int i = events[event].getTc_in() - events[event-1].getTc_out();
                i = (i > 0) ? i : -i;
                if((tempIndexOf = currentName.indexOf(" сег.")) != -1)
                    currentName = currentName.substring(0, tempIndexOf);
                sameName = events[event-1].getName().startsWith(currentName);
                thisIsNextSubclip = ((i < 25) & sameName);
            }
            
            if(tempPGM != 1){
                errPGMFormat++;
                errors[event][1] = "  PGM_ERROR!";
            }
            
            if(tempLogo != 1){
                errLogoFormat++;
                errors[event][2] = "  Logo_ERROR!";
            }
            
            
                
            // partner GOSTI
            if( (events[event].getName().contains("Jamies") || events[event].getName().contains("Oliver")) & !events[event].getName().contains("A-")){
                if(events[event].getTc_in() == 0){  // первый клип
                    if(!events[event+1].getName().equals("SP-GOSTI")){
                        errSPGostiParnerMpg++;
                        errors[event+1][3] = "   SP-GOSTI.mpg_ERROR!";   //Ошибка. Нет SP-GOSTI.mpg
                    }
                    for (String tempFormat1 : tempFormat)
                        if (tempFormat1.contains("partner GOSTI")) 
                            tempPartnerGostiFirst++;  // Ошибка. В первом субклипе не должно быть partner GOSTI
                    if(tempPartnerGostiFirst > 0){
                        errors[event][4] = "   format_GOSTI_ERROR";
                        errGOSTIFormat++;
                    }
                } else {    // <не первый клип>
                    for (String tempFormat1 : tempFormat)
                        if (tempFormat1.contains("partner GOSTI")) 
                            tempPartnerGostiNext++;
                    if(tempPartnerGostiNext != 1) {     //нет partner GOSTI
                        if(thisIsNextSubclip){  // следующий субклип
                            if(events[event - 1].getTc_in() == 0){  //предыдущий субклип начинается с 00
                                errors[event][4] = "   format_GOSTI_ERROR";
                                errGOSTIFormat++;
                            } else {                                //предыдущий субклип начинается не с 00
                                if(errors[event - 1][4] != null){   // в предыдущем субклипе была ошибка
                                    errors[event][4] = "   format_GOSTI_ERROR";
                                    errGOSTIFormat++;
                                }
                            }
                            
                        } else {                // НЕ следующий субклип
                            errors[event][4] = "   format_GOSTI_ERROR";
                            errGOSTIFormat++;
                        }
                    } else {                            //есть partner GOSTI
                        if(thisIsNextSubclip){  //это следующий субклип
                            if(events[event - 1].getTc_in() != 0){  //предыдущий субклип начинается НЕ с 00
                                if(errors[event - 1][4] != null){   // в предыдущем субклипе была ошибка
                                    errors[event-1][4] = null;
                                    errGOSTIFormat--;
                                } else {                            // в предыдущем субклипе небыло ошибки
                                    errors[event][4] = "   format_GOSTI_ERROR";
                                    errGOSTIFormat++;
                                }
                            } //предыдущий субклип начинается с 00
                        }                       // НЕ следующий субклип
                    }
                }           // </не первый клип>
            }  // partner GOSTI if()
             
            
            
            // partner 100mov
            if( (events[event].getName().contains("Anatomiya") || events[event].getName().contains("Amerika")) & !events[event].getName().contains("A-")){
                if(events[event].getTc_in() == 0){
                    if(!events[event+1].getName().equals("SP-100movPartner")){
                        errSP100movParnerMpg++;
                        errors[event+1][3] = "   SP-100movPartner.mpg_ERROR!";   //Ошибка. Нет SP-100movPartner.mpg
                    }
                    for (String tempFormat1 : tempFormat)
                        if (tempFormat1.contains("partner 100mov")) 
                            tempPartner100movFirst++;  // Ошибка. В первом субклипе не должно быть partner 100mov
                    if(tempPartner100movFirst > 0){
                        errors[event][4] = "   format_100mov_ERROR";
                        err100movFormat++;
                    }
                } else {
                    for (String tempFormat1 : tempFormat)
                        if (tempFormat1.contains("partner 100mov")) 
                            tempPartner100movNext++;
                    if(tempPartner100movNext != 1){     //нет partner 100mov
                        if(thisIsNextSubclip){  // следующий субклип
                            if(events[event - 1].getTc_in() == 0){  //предыдущий субклип начинается с 00
                                errors[event][4] = "   format_100mov_ERROR";
                                err100movFormat++;
                            } else {                                //предыдущий субклип начинается не с 00
                                if(errors[event - 1][4] != null){   // в предыдущем субклипе была ошибка
                                    errors[event][4] = "   format_100mov_ERROR";
                                    err100movFormat++;
                                }
                            }
                            
                        } else {                // НЕ следующий субклип
                            errors[event][4] = "   format_100mov_ERROR";
                            err100movFormat++;
                        }
                    } else {                            //есть partner 100mov
                        if(thisIsNextSubclip){  //это следующий субклип
                            if(events[event - 1].getTc_in() != 0){  //предыдущий субклип начинается НЕ с 00
                                if(errors[event-1][4] != null){   // в предыдущем субклипе была ошибка
                                    errors[event-1][4] = null;
                                    err100movFormat--;
                                } else {                            // в предыдущем субклипе небыло ошибки
                                    errors[event][4] = "   format_100mov_ERROR";
                                    err100movFormat++;
                                }
                            } //предыдущий субклип начинается с 00
                        }                       // НЕ следующий субклип
                    }
                }           // </не первый клип>
            }  // partner 100mov if()                        


                             
            // text for Americas got talent
            if((events[event].getName().contains("Amerika")) & !events[event].getName().contains("A-")){
                for (String tempFormat1 : tempFormat)
                    if (tempFormat1.equals("text for Americas got talent")) 
                            tempAmericasGotTalentON++;
                if(tempAmericasGotTalentON != 1){
                    errors[event][5] = "   text for Americas_ERROR!";
                    errTextAmerikaON++;
                 }
            } 
            
            
        } //for(event = 1; event < totalEvents; event++ )
    }  // static void checkFormats(Event[] events)
                
     
    static void statistic(Event[] events){
        for(event = 1; event < totalEvents; event++){
            System.out.print(event + "   " + events[event].toString());
            for(int i = 0; i < 13; i++){
                if (errors[event][i] != null)
                    System.out.print(errors[event][i]);   
            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println("Ошибок в Duration: " + totalErrDUR);
        //System.out.println("Ошибок в таймкоде: " + totalErrTC);
        System.out.println("Ошибок в format  PGM: " + errPGMFormat);
        System.out.println("Ошибок в format  logo: " + errLogoFormat);
        System.out.println("Ошибок в format  partner GOSTI: " + errGOSTIFormat);
        System.out.println("Ошибок в format  partner 100mov: " + err100movFormat);
        System.out.println("Ошибок в format  text for Americas got talent: " + errTextAmerikaON);
        //System.out.println("Ошибок в format  text for Americas got talent off: " + errTextAmerikaOFF);
        System.out.println("Ошибок в SP-100movPartner.mpg: " + errSP100movParnerMpg);
        System.out.println("Ошибок в SP-GOSTI.mpg: " + errSPGostiParnerMpg);
        
        
 
        System.out.println("Проверить вручную:");
        System.out.println("1. На мультиках знак круг");
        System.out.println("2. text for Americas got talent off");
        System.out.println("3. ");
        
    }
    
}

