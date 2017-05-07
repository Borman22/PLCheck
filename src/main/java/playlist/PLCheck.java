package playlist;
import java.util.Scanner;
import java.io.File;
import java.io.*;
import java.util.Arrays;
import javax.swing.JOptionPane;

public class PLCheck{
    static String[][] m = new String[1000][15];  // [event, strOfEvent]
    static Event [] evnt = new Event[1000];
    static String[][] errors = new String[1000][13];
    static int programs[][] = new int[100][10];
    static Scanner scn;
    static boolean flagEquals;
    static int event = 0;
    static int totalEvents = 0;
    static int totalPrograms = 0;
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
    static int errTC = 0;
    static int errDUR = 0;
    static int errSP100movParnerMpg = 0;
    static int errSPGostiParnerMpg = 0;
    static int errAnonsDate = 0;
    static int errAnonsTime = 0;
    static int errAnonsProgram = 0;
    static int number = 0;
    static int errDaliFormat = 0;
    static String canonicalName;
    
    public static String[][] progNameAndDali = {
	{"Vtoraya", "Dali HS-Druge Jittya"},
	{"Vacances", "Dali HS-Les Vacances de iAmour"},
	{"Mister", "Мистер Бин", "Dali HS-Mister Bin"},
	{"Ogon", "Dali HS-Ogon Lyubvi"},
	{"Diabola", "Dali HS-Santa Diabla"},
	{"Numo", "Dali MS-321 Numo"},
	{"Ernie", "Dali MS-Bert and Ernie"},
	{"Elmo", "Dali MS-Elmo"},
	{"Graysya", "Dali MS-Graysya"},
	{"Grover", "Dali MS-Grover"},
	{"Redyska", "Редиска", "Dali MS-Rediska"},
	{"3x4", "Зх4", "Dali PR-3x4"},
	{"Amerika", "Dali PR-Amerika Mae Talant"},
	{"Anatomiya", "Анатомия славы", "Dali PR-Anatomiya Slavy"},
	{"Pozhenimsya", "Dali PR-Davay Pozhenimsya"},
	{"Enyky", "Эники", "Dali PR-Enyky Benyky"},
	{"GLUPERS", "Глуперсы", "Dali PR-Glupers"},
	{"Hovanky", "семейные прятки", "Dali PR-Hovanky"},
	{"ROZVODI", "Rozvodi", "Rozvody", "Крутые разводы", "Dali PR-Kruti Rozvodi"},
	{"Lolita", "Dali PR-Lolita"},
	{"More", "Море по колено", "MK_", "Dali PR-More po kolino"},
	{"Jamies 15", "Jamies15", "Jamies_15", "Обед за 15 минут", "Dali PR-Oliver 15min"},
	{"za 30 min", "Обед за 30 минут", "Jamies30M", "JO30MM", "Dali PR-Oliver 30min"},
	{"Jamies_Big_Festival", "Dali PR-Oliver Festival"},
	{"Jamies Great Britain", "Британская кухня", "Dali PR-Oliver Great Britain"},
	{"Jamies_American_Road_Trip", "Кулинарные путешествия", "Dali PR-Oliver Mandrivki"},   // ?????????????????   Великий Кормчий Джейми 1 с. Великий Харчевой Джейми сег. 1-2
	{"_Twist", "Рецепты Джейми", "Dali PR-Oliver Recepty"},
	{"Paral", "Dali PR-Paralelniy Svit"},
	{"Rozkishne", "Dali PR-Rozkishne zhittya"},
	{"SimeyniPrystrasti", "Semeynye_strasti", "Dali PR-Simeyni Pristrasti"}, // 6???
	{"Virus", "Dali PR-Virus Smihu"},
	{"Vuso", "УсоЛапоХвост", "Dali PR-Vusolapohvist"},
	{"groshi", "деньги", "Dali PR-Za groshi"}
        //{"Карамба", "Dali PR-Karamba"}
    };
    
    public static void main(String[] args)  throws FileNotFoundException, IOException  {
        openFile();
        readFile();
        checkPlayListFileCobaltAsRun();
    }
    
    private static void openFile(){
            try{
                scn = new Scanner((new File("d://Borman//1")), "UTF-8");
            } catch(Exception e){JOptionPane.showMessageDialog(null, "              Чё нада?!", "Опа!", JOptionPane.ERROR_MESSAGE);}  // "Файл не найден"
        } 
  
    private static void readFile(){
        while(scn.hasNextLine()){
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
            evnt[i].setNumberOfEvent(i);
            evnt[i].setDate(m[i][1]);
            evnt[i].setTime(m[i][2]);
            evnt[i].setDuration(m[i][3]);
            
            if(m[i][14].equals("9") || m[i][14].equals("10")){
                evnt[i].setTc_in(0);
                
                String TCOutTemp;
                
                if(i < (totalEvents-1))
                    TCOutTemp = evnt[i].strContents(m[i+1][2], "<time>", "</time>");
                else 
                    TCOutTemp = "06:00:00:00";

                int tempTC_in = TimeCode.TCStrToFrame(evnt[i].strContents(m[i][2], "<time>", "</time>"));
                int tempTC_out = TimeCode.TCStrToFrame(TCOutTemp);
                        
                if(tempTC_out < tempTC_in)
                    tempTC_out += 2160000;
                evnt[i].setTc_out(TimeCode.TCInFrameToIntStr(tempTC_out - tempTC_in));

                evnt[i].setAsset_id(m[i][4]);
                evnt[i].setName(m[i][5]);
                evnt[i].setFormats(evnt[i].strContents(m[i][6], "<format>", "</format>").split(", "));

                evnt[i].setStatus(m[i][7]);
                
            } else {
                evnt[i].setTc_in(m[i][4]);
                evnt[i].setTc_out(m[i][5]);
                evnt[i].setAsset_id(m[i][6]);
                evnt[i].setName(m[i][7]);
                evnt[i].setFormats(evnt[i].strContents(m[i][8], "<format>", "</format>").split(", "));
                evnt[i].setStatus(m[i][9]);
            }
        }
    }

     
    static void checkPlayListFileCobaltAsRun(){ 
        checkTCCobaltAsRun(evnt);
        checkFormats(evnt);
        statistic(evnt);
    }
    
    static void checkTCCobaltAsRun(Event[] events){  //доделать проверку TC_in - TC_out - TC_in
        for(event = 1; event < totalEvents; event++){
            if(events[event].getDuration() != (events[event].getTc_out() - events[event].getTc_in())){
                errDUR++;
		int tempIntStrTC = TimeCode.TCInFrameToIntStr(events[event].getTc_out() - events[event].getTc_in());
		TimeCode tempTC = new TimeCode(tempIntStrTC);
                errors[event][0] = "   DUR_ERROR!(" + tempTC.toString() + ")";
            }
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
        int tempDali;
        boolean thisIsNextSubclip;
        String bufPrgName = "qqq";
        int numberOfPrg = 0;
        int numberOfSubclip = 0;
        String [] tempFormat;
        String [] tempAnons;
        TimeCode tempTC = new TimeCode();
        StringBuffer buf;
        int tempInt;
        int subClip;
	String formatDali;
        
        for(event = 1; event < totalEvents; event++ ){
            tempPGM = 0;
            tempLogo = 0;
            tempPartnerGostiFirst = 0;
            tempPartnerGostiNext = 0;
            tempPartner100movFirst = 0;
            tempPartner100movNext = 0;
            thisIsNextSubclip = false;
   
            
            tempFormat = events[event].getFormat();
                                                                            //System.out.println(Arrays.deepToString(tempFormat));
            for(int i = 0; i < tempFormat.length; i++){
                                                                            //System.out.println(tempFormat[i]);
                if(tempFormat[i].equals("PGM"))
                    tempPGM++;

                if( tempFormat[i].equals("logo v2") || tempFormat[i].equals("logo Traur") )
                    tempLogo++;
            }
            
            if(event != 1){      //2 субклипа подряд или нет
                canonicalName = events[event].getCanonicalName();
                int i = events[event].getTc_in() - events[event-1].getTc_out();
                i = (i > 0) ? i : -i;
                thisIsNextSubclip = ((i < 25) & events[event-1].isSameName(canonicalName));
            }
            
            //System.out.println(event + " " + events[event].getName() + "   =    " + canonicalName);
            
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
                if(! (events[event].getName().contains("Christmas") || events[event].getName().contains("Great") || events[event].getName().contains("Trip") || events[event].getName().contains("Big")) ){
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
                } else {            //не 15 минут, не рецепты и не 30 минут
                    if(events[event].getName().contains("Jamies"))
                        for (String tempFormat1 : tempFormat)
                            if (tempFormat1.contains("partner GOSTI")) 
                                tempPartnerGostiFirst++;  // Ошибка. В первом субклипе не должно быть partner GOSTI
                    if(tempPartnerGostiFirst > 0){
                        errors[event][4] = "   format_GOSTI_ERROR";
                        errGOSTIFormat++;
                    }
                }
                
            } // partner GOSTI !(Jamies 15" || "Oliver")

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
                        }  // НЕ следующий субклип
                    }
                } // </не первый клип>
                
            }  // partner 100mov if()                        

            
            
            
            
            
            //Anons errors
            if( (events[event].getDuration() < 500) && (events[event].getName().startsWith("A-"))){ // это анонс  
                buf = new StringBuffer();
                tempAnons = events[event].getName().split("-");
                tempTC.setTC((buf.append(tempAnons[1]).append(':').append(tempAnons[2]).append(":00:00").toString())  );
                tempInt = tempTC.getTCInFrame() - events[event].getTime();
                tempInt = (tempInt > 0) ? tempInt : -tempInt;
                //System.out.println("tempTC.getTCInFrame() = " + tempTC.getTCInFrame() + "    events[event].getTc_in() = " + events[event].getTime());
                //System.out.println("tempTC = " + tempTC.toString() + "   events[event] = " + events[event].toString() + "   " + tempInt);
                if(tempInt > 7500){
                    errors[event][10] = "   AnonsTime_ERROR!";
                    errAnonsTime++;	
                }
                
                buf = new StringBuffer();
                buf.append(tempAnons[4]).append('-').append(tempAnons[3]).toString();
                if(!(events[event].getDate().contains(buf))){
                    errors[event][10] = "   AnonsDate_ERROR!";
                    errAnonsDate++;
                }
                
                // Сделать, чтобы программы, которые в анонсе соответствовали программам, которые в эфире
                for(String element: tempAnons){
                    
                }
            }

            // Создаем массив всех программ programs[номер программы][номер субклипа]
            if((events[event].getDuration() > 1500) && !(events[event].getName().contains("Slalom"))){   // файл > 1 мин
                if(events[event].getTc_in() == 0){    // начинается с 00:00?
                    programs[numberOfPrg][9] = numberOfSubclip + 1;  //записываем сколько субклипов в предыдущей программе
                    numberOfSubclip = 0;
                    numberOfPrg++;
                    programs[numberOfPrg][numberOfSubclip] = event;
                    bufPrgName = canonicalName;
                } else {        //файл начинается не с 00:00
                    if(bufPrgName.equals(canonicalName)){ // имя совпадает
                        if( !(events[programs[numberOfPrg][numberOfSubclip]].isSameTC_Out( events[event].getTc_in() )) ){    // TC отличается - пишем ошибку
                            errTC++;
                            errors[event][8] = "   IN/OUT/ClipName_ERROR   "/* + events[programs[numberOfPrg][numberOfSubclip]].getTc_out() + "   " + events[event].getTc_in()*/;
                        }
                        numberOfSubclip++;
                        programs[numberOfPrg][numberOfSubclip] = event;
                    } else {    // имя не совпадает - ищем, чтобы таймокд совпадал
                        boolean find = false;
                        programs[numberOfPrg][9] = numberOfSubclip + 1;  //записываем сколько субклипов в предыдущей программе
                        numberOfPrg++;
                        numberOfSubclip = 1;
                        programs[numberOfPrg][numberOfSubclip] = event;
                        bufPrgName = canonicalName;
                        if(numberOfPrg != 1){   //это не первая найденная программа
                            for(int tempEvent = event; tempEvent >= programs[numberOfPrg-1][0]; tempEvent--){
                                //System.out.println(tempEvent + "  это до сравнения bufPrgName = [" + bufPrgName + "]   " + events[tempEvent].getName() + "  events[tempEvent].isSameName(bufPrgName)" + events[tempEvent].isSameName(bufPrgName) + "  tc " + events[tempEvent].isSameTC_Out(events[event].getTc_in()));
                                if((events[tempEvent].isSameTC_Out(events[event].getTc_in())) && events[tempEvent].isSameName(bufPrgName)){  //нашли
                                    programs[numberOfPrg][0] = tempEvent;
                                    find = true;
                                    break;
                                }
                            }    
                            if(!find){
                                programs[numberOfPrg][0] = event;
                                errTC++;
                                errors[event][8] = "   IN/OUT/ClipName_ERROR";
				//System.out.println("Количество субклипов = " + programs[numberOfPrg - 1][9]);
                            }   
                           
                        } else {    //это первая найденная программа
                            programs[numberOfPrg][0] = event;
                            errTC++;
                            errors[event][8] = "   IN/OUT/ClipName_ERROR";
                        }
                    } //имя не совпадает
                }   //файл начинается не с 00:00
            }// файл < 1 мин
        } //for(event = 1; event < totalEvents; event++ )
        totalPrograms = numberOfPrg;
        programs[numberOfPrg][9] = numberOfSubclip + 1;  //записываем сколько субклипов в последней программе
        
        
        
        
        
        //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        
        
        
        
        
        // проверяем все форматы программ
        for (int program = 1; program < totalPrograms+1; program++){
        /*    //if(programs[program][0] != 0)
            //    System.out.println("________________________________________Программа " + program + "________________________________________");
            for(int j = 0; j < programs[program][9]; j++){
                System.out.printf("%87s  %-60s", events[programs[program][j]].toString(), events[programs[program][j]].getName());
                //System.out.println(programs[program][9]);
		String [] tempFormat2 = events[programs[program][j]].getFormat();
		
		
		for(String tempFormat21:tempFormat2){
		    System.out.print("[" + tempFormat21 + "]" + "   ");
 
		}
		
		System.out.println();
            }
	    System.out.printf("%100s-----------\n\n", formatDali);
            System.out.println();
	*/
        //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
            
            //text for Americas got talent off            
            if(events[programs[program][0]].getName().contains("Amerika")){
                for(subClip = 0; subClip < programs[program][9]; subClip++){  //перебераем все субклипы в этой программе
                    tempFormat = events[programs[program][subClip]].getFormat(); 
                    tempAmericasGotTalentON = 0;
                    tempAmericasGotTalentOFF = 0;
                    
                    
                    for (String tempFormat1 : tempFormat){  //перебираем форматы в текущем субклипе - programs[program][subClip]
                        if (tempFormat1.equals("text for Americas got talent")) 
                            tempAmericasGotTalentON++;
                        if ( tempFormat1.equals("text for Americas got talent off") )
                            tempAmericasGotTalentOFF++;
                    }
                    if(tempAmericasGotTalentON != 1){
                        errors[programs[program][subClip]][5] = "   text for Americas_ERROR!";
                        errTextAmerikaON++;
                    }  
                    if (subClip == (programs[program][9] - 1)){
                        if(tempAmericasGotTalentOFF != 1){
                            errors[programs[program][subClip]][6] = "   text for Americas off_ERROR!";
                            errTextAmerikaOFF++;
                        } 
                    } else {        // не последний субклип
                        if(tempAmericasGotTalentOFF != 0){
                            errors[programs[program][subClip]][6] = "   text for Americas off_ERROR!";
                            errTextAmerikaOFF++;
                        } 
                    }

                } //for...  - перебераем все субклипы в этой программе
            
            } //if Amerika
            
            //Проверим все форматы Dali на наличие/отсутствие. Кроме HS-Krasivie-i-ambicioznie и KrasivoJit
            for(subClip = 0; subClip < programs[program][9]; subClip++){ //перебераем все субклипы в этой программе
                
                tempFormat = events[programs[program][subClip]].getFormat();
                tempDali = 0;
                for (String tempFormat1 : tempFormat){  //перебираем форматы в текущем субклипе - programs[program][subClip]
                    if ( tempFormat1.startsWith("Dali") )
                        tempDali++;
                }
                   // System.out.println("[" + program + "][" + subClip + "]  Dali = " + tempDali + " " + tempFormat1 + "              subClip = " + subClip + "  (programs[program][9] - 1) =" + (programs[program][9] - 1) );
                if( (subClip != (programs[program][9] - 1)) && (tempDali != 0)){
		    errors[programs[program][subClip]][7] = "   format_Dali_ERROR!"; // (не должно быть Dali)
		    errDaliFormat++;
		    if((subClip == 0) && (programs[program][0] == programs[program][1]) && errors[programs[program][subClip]][7].equals("   format_Dali_ERROR!")){
			errors[programs[program][subClip]][7] = null; // (На Dali не обращаем внимание)
			errDaliFormat--;
		    }
                }
                if( (subClip == (programs[program][9] - 1)) && (tempDali != 1)){
                    if(! ((events[programs[(program < totalPrograms) ? (program+1) : program][0]].getName().contains("ambicioznie")) || (events[programs[(program < totalPrograms) ? (program+1) : program][0]].getName().contains("KrasivoJit")) || (events[programs[(program < totalPrograms) ? (program+1) : program][0]].getName().contains("BeetParty")))){  //не пишем ошибку, если следующий клип (если он есть) HS-Krasivie-i-ambicioznie, KrasivoJit
                        errors[programs[program][subClip]][7] = "   format_Dali_ERROR!"; // (ожидается format Dali)
                        errDaliFormat++;
                    }
                } 
            }   //for(subClip = 0; subClip < programs[program][9]; subClip++){ //перебераем все субклипы в этой программе
	    
	    if(program != 1){
		formatDali = getDali(events[programs[program][0]].getName());
		String [] tempFormat2 = events[programs[program-1][programs[program-1][9] - 1]].getFormat();
	//System.out.println(Arrays.deepToString(tempFormat2) + "    " + formatDali + "      это программа  " + events[programs[program][0]].getName());
		boolean sameFormat = false;
		for(String tempFormat21:tempFormat2)
		    if(tempFormat21.equals(formatDali))
			sameFormat = true;
		    if(!formatDali.equals("") && !sameFormat){
			if(errors[programs[program - 1][programs[program-1][9] - 1]][7] != null){
			    errors[programs[program - 1][programs[program-1][9] - 1]][7] = errors[programs[program - 1][programs[program-1][9] - 1]][7].concat("  must be   [ " + formatDali + " ]");
			} else {
			    errors[programs[program - 1][programs[program-1][9] - 1]][7] = "   format_Dali_ERROR!  must be  [ " + formatDali + " ]";
			    errDaliFormat++;
			}
		    }
	    }
        } //for (int i = 1; i < totalPrograms; i++){
    }  // static void checkFormats(Event[] events)

    static void statistic(Event[] events){
        for(event = 1; event < totalEvents; event++){
            System.out.printf("%87s", events[event].toString());
            System.out.printf("%-60s", events[event].getName());
            System.out.print(".");
            for(int i = 0; i < 13; i++){
                if (errors[event][i] != null)
                    System.out.print(errors[event][i]); //System.out.printf("%8s\n", number);  
            }
            System.out.println("");
        }
        

        System.out.println("");
        System.out.println("                                ВСЕГО ОШИБОК: " + (errDUR + errTC + errPGMFormat + errLogoFormat + errGOSTIFormat + err100movFormat + errTextAmerikaON + errTextAmerikaOFF + errSP100movParnerMpg + errSPGostiParnerMpg + errDaliFormat + errAnonsDate + errAnonsTime + errAnonsProgram));
        
        System.out.println("");
        if(errDUR != 0) System.out.println("Ошибок в Duration: " + errDUR);
        if(errTC != 0) System.out.println("Ошибок в таймкоде или имени субклипа: " + errTC);
        if(errPGMFormat != 0) System.out.println("Ошибок в format  PGM: " + errPGMFormat);
        if(errLogoFormat != 0) System.out.println("Ошибок в format  logo: " + errLogoFormat);
        if(errGOSTIFormat != 0) System.out.println("Ошибок в format  partner GOSTI: " + errGOSTIFormat);
        if(err100movFormat != 0) System.out.println("Ошибок в format  partner 100mov: " + err100movFormat);
        if(errTextAmerikaON != 0) System.out.println("Ошибок в format  text for Americas got talent: " + errTextAmerikaON);
        if(errTextAmerikaOFF != 0) System.out.println("Ошибок в format  text for Americas got talent off: " + errTextAmerikaOFF);
        if(errSP100movParnerMpg != 0) System.out.println("Ошибок в SP-100movPartner.mpg: " + errSP100movParnerMpg);
        if(errSPGostiParnerMpg != 0) System.out.println("Ошибок в SP-GOSTI.mpg: " + errSPGostiParnerMpg);
        if(errDaliFormat != 0) System.out.println("Ошибок в format  Dali... : " + errDaliFormat);
        
        if(errAnonsDate != 0) System.out.println("Ошибок в дате анонса: " + errAnonsDate);
        if(errAnonsTime != 0) System.out.println("Ошибок во времени анонса: " + errAnonsTime);
        if(errAnonsProgram != 0) System.out.println("Ошибок в программах анонса: " + errAnonsProgram);

        System.out.println("");
        System.out.println("Проверить вручную:");
        System.out.println("1. Знак круг");
        System.out.println("2. Знак треугольник");
        //System.out.println("3. Склеротики (названия программ)");
    }
    
    private static String getDali(String programName) {
        for (String[] progNameAndDali1 : progNameAndDali) 
            for (int j = 0; j < progNameAndDali1.length - 1; j++)
                if (programName.contains(progNameAndDali1[j]))
                    return progNameAndDali1[progNameAndDali1.length - 1];
        return "";
    }    
}

