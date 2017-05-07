package playlist;
import java.util.Scanner;
import java.io.File;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;



public class PLCheck{
    static String[][] m = new String[1000][15];  // [event, strOfEvent]
    static Event [] evnt = new Event[1000];
    static String[][] errors = new String[1000][15];
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
    static int errFormat = 0;
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
    static int errZnakKrugFormat = 0;
    static int errZnakKrug16Format = 0;
    static String canonicalName;
    
    
    public static void main(String[] args)  throws FileNotFoundException, IOException  {
	long start = System.currentTimeMillis();
        openFile();
        readFile();
        checkPlayListFileCobaltAsRun();
	long stop = System.currentTimeMillis();
	System.out.println("");
	System.out.println("Плейлист проверен за " + (stop - start) + " мс");

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
                evnt[i].setFormats(evnt[i].strContents(m[i][6], "<format>", "</format>").trim().split(", "));

                evnt[i].setStatus(m[i][7]);
                
            } else {
                evnt[i].setTc_in(m[i][4]);
                evnt[i].setTc_out(m[i][5]);
                evnt[i].setAsset_id(m[i][6]);
                evnt[i].setName(m[i][7]);
                evnt[i].setFormats(evnt[i].strContents(m[i][8], "<format>", "</format>").trim().split(", "));
                evnt[i].setStatus(m[i][9]);
            }
        }
    }

     
    static void checkPlayListFileCobaltAsRun(){ 
        checkTCCobaltAsRun(evnt);
        checkFormats(evnt);
        statistic(evnt);
	//sendByNet();
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
	int tempLogoBitva;
	int tempLogoTraur;
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
	
	
	
	// Определим дату 
	String date = events[1].getDate();
	int year = Integer.parseInt(date.substring(0, 4));
	int month = Integer.parseInt(date.substring(5, 7)) - 1;  // 0 - Январь
	int day = Integer.parseInt(date.substring(8, 10));  // Суббота - 7 день недели, а Воскресенье - 1 день недели (Американская система)!!!

	Calendar calendar = new GregorianCalendar(year, month, day);

        for(event = 1; event < totalEvents; event++ ){
            tempPGM = 0;
            tempLogo = 0;
	    tempLogoBitva = 0;
	    tempLogoTraur = 0;
	    
            tempPartnerGostiFirst = 0;
            tempPartnerGostiNext = 0;
            tempPartner100movFirst = 0;
            tempPartner100movNext = 0;
            thisIsNextSubclip = false;
	    
   
            
            tempFormat = events[event].getFormat();
                                                                            //System.out.println(Arrays.deepToString(tempFormat));
            for(int i = 0; i < tempFormat.length; i++){
                                                                            //System.out.println(tempFormat[i]);
                if(tempFormat[i].contains("PGM"))
                    tempPGM++;

                if( tempFormat[i].contains("logo v2") || tempFormat[i].contains("logo Traur") || tempFormat[i].equals("logo NewYear"))
                    tempLogo++;
		
		if( tempFormat[i].contains("Logo for Bitva") || tempFormat[i].contains("logo NewYear Right") )
                    tempLogoBitva++;
		
		if(tempFormat[i].contains("logo Traur"))
                    tempLogoTraur++;
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
                errors[event][1] = "   PGM_ERROR!";
            }
            
            if( (tempLogo + tempLogoBitva) != 1 ){
                errLogoFormat++;
                errors[event][2] = "   Logo_ERROR!";
            }
            
            if(events[event].getName().contains("T-Tonometr"))
		if(!((tempLogoTraur == 0 && tempLogoBitva != 0) || (tempLogoTraur != 0 && tempLogoBitva == 0))){
		    errLogoFormat++;
		    if(errors[event][2] == null)
			errors[event][2] = "   Logo_ERROR! [Logo for Bitva]";
		    else errors[event][2] = errors[event][2] + " [Logo for Bitva]";
		}

	    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//            // Проверяем знак круг
//	    boolean thisIsMult = isThisMult(events[event].getName());
//            int znakKrug = 0;
//	    int znakKrugMT = 0; //manual time
//            for (String tempFormat1 : tempFormat){  //перебираем форматы в текущем субклипе - programs[program][subClip]
//                if (tempFormat1.equals("znak krug")) 
//                    znakKrug++;
//                if ( tempFormat1.equals("znak krug manual") )
//                    znakKrugMT++;
//            }
//	    if(thisIsMult){ // Это мультик
//		if(events[event].getDuration() > 4500){ // > 3 минут
//		    if( !((znakKrug == 1) && (znakKrugMT == 0)) ){
//			errZnakKrugFormat++;
//			errors[event][13] = "   format znak krug_ERROR!_(must be [znak krug] )";
//		    }
//		} else {   // < 3 минут
//		    if( !((znakKrug == 0) && (znakKrugMT == 1)) ){
//			errZnakKrugFormat++;
//			errors[event][13] = "   format znak krug_ERROR!_(must be [znak krug manual] )";
//		    }
//		}	
//		   
//            } else { // это не мультик
//		if((znakKrug + znakKrugMT) != 0){
//		    errZnakKrugFormat++;
//		    errors[event][13] = "   format znak krug_ERROR!";
//		}
//	    }
//	    
//	    // Проверяем знак круг в MS-Fixies-Aeroplan
//	    if(events[event].getName().contains("MS-Fixies-Aeroplan")){ // Это MS-Fixies-Aeroplan - значит должен быть трехминутный знак круг
//		if( (znakKrug == 1) && (znakKrugMT == 0) ){
//		    if(errors[event][13] != null){
//			errors[event][13] = null;
//			errZnakKrugFormat--;
//		    }
//		} else {
//		    errZnakKrugFormat++;
//		    errors[event][13] = "   format znak krug_ERROR!_(must be [znak krug] )";
//		}
//            } 
	    
	    // Проверяем znak krug 16 на сериалах (HS-...)
	    int znakKrug16 =  0;
	    for (String tempFormat1 : tempFormat)  //перебираем форматы в текущем субклипе - programs[program][subClip]
		if (tempFormat1.equals("znak krug 16")) 
		    znakKrug16++;
	    if(events[event].getName().contains("HS-")||events[event].getName().contains("Секс и город")||events[event].getName().contains("Морская полиция")||events[event].getName().contains("Медиум")){  // Это сериал
		if(znakKrug16 != 1){
			errZnakKrug16Format++;
			errors[event][14] = "   format znak krug 16_ERROR!";
		} 
	    } else { // это не сереал
		if(znakKrug16 != 0){
		    errZnakKrug16Format++;
		    errors[event][14] = "   format znak krug 16_ERROR!";
		}
	    }
                       

            
            // partner GOSTI
            if( (events[event].getName().contains("Jamies") || events[event].getName().contains("Oliver")) & !events[event].getName().contains("A-")){
//                if(! (events[event].getName().contains("Christmas") || events[event].getName().contains("Great") || events[event].getName().contains("Trip") || events[event].getName().contains("Big")) ){
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
//                } 
//		else {            //не 15 минут, не рецепты и не 30 минут
//                    if(events[event].getName().contains("Jamies"))
//                        for (String tempFormat1 : tempFormat)
//                            if (tempFormat1.contains("partner GOSTI")) 
//                                tempPartnerGostiFirst++;  // Ошибка. В первом субклипе не должно быть partner GOSTI
//                    if(tempPartnerGostiFirst > 0){
//                        errors[event][4] = "   format_GOSTI_ERROR";
//                        errGOSTIFormat++;
//                    }
//                }
                
            } // partner GOSTI !(Jamies 15" || "Oliver")

            // partner 100mov
            if( (events[event].getName().contains("Anatomiya") || events[event].getName().contains("Amerika") || events[event].getName().contains("Америка имеет талант")) & !events[event].getName().contains("A-")){
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
                
                // TODO Сделать, чтобы программы, которые в анонсе соответствовали программам, которые в эфире
                for(String element: tempAnons){
                    
                }
            }

            // Создаем массив всех программ programs[номер программы][номер субклипа]
            if((events[event].getDuration() > 1500) && !(events[event].getName().contains("Slalom") || events[event].getName().contains("SLALOM") || events[event].getName().contains("T-Tonometr"))){   // файл > 1 мин
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
			    for(int tempEvent = event; tempEvent >= 0; tempEvent--){
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
                            }     
                        }
                    } //имя не совпадает
                }   //файл начинается не с 00:00
            }// файл < 1 мин
        } //for(event = 1; event < totalEvents; event++ )
        totalPrograms = numberOfPrg;
        programs[numberOfPrg][9] = numberOfSubclip + 1;  //записываем сколько субклипов в последней программе
        
        
        
        
        
        //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        
        //Проходимся по всем событиям, кроме программ титров MS-Fixies-Aeroplan и тонометра и смотрим, чтобы было только по 2 формата, или 2-3, когда траур (+свеча)
        for(event = 1; event < totalEvents; event++){
            if(!isThisProgramsSubclip(event) && !(events[event].getName().contains("MS-Fixies-Aeroplan") || events[event].getName().contains("T-Tonometr"))){
                tempFormat = events[event].getFormat();
		// Если это траурный день, то может быть по 3 формата (3 - свеча)
		int tempLogoTraur2 = 0;
		int tempLogoSvecha = 0;
		for (String str:tempFormat){
		    if(str.contains("logo Traur"))
			tempLogoTraur2++;
		    if(str.contains("Svecha"))
			tempLogoSvecha++;
		}
		if(tempLogoTraur2 == 0)
		    if(tempFormat.length != 2){
			errors[event][12] = "   format_ERROR!_(must be 2 formats - [PGM, logo] )"; 
			errFormat++;
		    }
		else { // Это траурный день
		    if(tempLogoSvecha != 0)
			if(tempFormat.length != 2){
			    errors[event][12] = "   format_ERROR!_(must be 2 formats - [PGM, logo Traur] )"; 
			    errFormat++;
			}
		    else if(tempFormat.length != 3){
			    errors[event][12] = "   format_ERROR!_(must be 3 formats - [PGM, logo Traur, Svecha] )"; 
			    errFormat++;
		    }
		}
	    } else { // Проходимся по тонометрам и проверяем, стоит ли на них склеротик
		String tonometrsDaliFormat = "";
		String programmsDaliFormat = "";
		if(events[event].getName().contains("T-Tonometr")){
		    for(String str:events[event].getFormat()){
			if(str.startsWith("Dali")){
			    tonometrsDaliFormat = str;
			}
		    }
		    for(int j = 0; j < totalPrograms; j++){
			if(programs[j][0] > event){
			    programmsDaliFormat = getDali(events[programs[j][0]].getName());
			    // Сравниваем формат с Dali и если он совпадает - выходим, а если нет - пишем ошибку
			    if(!programmsDaliFormat.equals(tonometrsDaliFormat)){
				errors[events[event].getNumberOfEvent()][7] = "   format_Dali_ERROR!"; // (ожидается format Dali)
				errDaliFormat++;
			    }
			    break;
			}   
		    }  
		}
	    }
	}
	
	
	
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
  
	    
	    
	    
	    
	    
	    
	
	    
	    
	   
            //text for Americas got talent off            
            if(events[programs[program][0]].getName().contains("Amerika") || events[programs[program][0]].getName().contains("Америка имеет талант")){
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
            
            //Проверим все форматы Dali на наличие/отсутствие. Кроме HS-Krasivie-i-ambicioznie и KrasivoJit и еще несколько
            for(subClip = 0; subClip < programs[program][9]; subClip++){ //перебераем все субклипы в этой программе
                
                tempFormat = events[programs[program][subClip]].getFormat();
                tempDali = 0;
                for (String tempFormat1 : tempFormat){  //перебираем форматы в текущем субклипе - programs[program][subClip]
		    if ( tempFormat1.startsWith("Dali") )
                        tempDali++;
                }
                   // System.out.println("[" + program + "][" + subClip + "]  Dali = " + tempDali + " " + tempFormat1 + "              subClip = " + subClip + "  (programs[program][9] - 1) =" + (programs[program][9] - 1) );
                if( (subClip != (programs[program][9] - 1)) && (tempDali != 0)){
		    errors[programs[program][subClip]][7] = "   format_Dali_ERROR!"; // (не должно быть Dali на всех субклипах, кроме последнего)
		    errDaliFormat++;
		    if((subClip == 0) && (programs[program][0] == programs[program][1]) && errors[programs[program][subClip]][7].contains("   format_Dali_ERROR!")){
			errors[programs[program][subClip]][7] = null; // (На Dali не обращаем внимание - когда первый субклип не найден - он считается отдельной программой и на него ставится еррор)
			errDaliFormat--;
		    }
                }
		
		
		
		if( (subClip == (programs[program][9] - 1)) && ((tempDali != 1) && (events[programs[program][subClip]].getDuration() > 3375))){  // > 2:15   
		    if(program < totalPrograms) { // Это не последняя программа, так что можно обратиться к следующей
			if(!isWithoutDali(events[programs[program+1][0]].getName()) ){  //не пишем ошибку, если следующий клип HS-Krasivie-i-ambicioznie, KrasivoJit, BeetParty
			    errors[programs[program][subClip]][7] = "   format_Dali_ERROR!"; // (ожидается format Dali)
			    errDaliFormat++;
			}
		    } else  { // Это последняя программа
				errors[programs[program][subClip]][7] = "   format_Dali_ERROR!"; // (ожидается format Dali)
				errDaliFormat++;
			    }
                } else if(  (subClip == (programs[program][9] - 1)) && (tempDali != 0) && (events[programs[program][subClip]].getDuration() < 3375) ){
		    errors[programs[program][subClip]][7] = "   format_Dali_ERROR! [<2:15]"; // (здесь не должно быть format Dali)
		    errDaliFormat++;
		}
                /*if( (subClip == (programs[program][9] - 1)) && (tempDali != 1)){
                    if(! ((events[programs[(program < totalPrograms) ? (program+1) : program][0]].getName().contains("ambicioznie")) || (events[programs[(program < totalPrograms) ? (program+1) : program][0]].getName().contains("KrasivoJit")) || (events[programs[(program < totalPrograms) ? (program+1) : program][0]].getName().contains("BeetParty")))){  //не пишем ошибку, если следующий клип (если он есть) HS-Krasivie-i-ambicioznie, KrasivoJit
                        errors[programs[program][subClip]][7] = "   format_Dali_ERROR!"; // (ожидается format Dali)
                        errDaliFormat++;
                    }
                }*/
		
            }   //for(subClip = 0; subClip < programs[program][9]; subClip++){ //перебераем все субклипы в этой программе
	    
	    if(program != 1){
		formatDali = getDali(events[programs[program][0]].getName());
		String [] tempFormat2 = events[programs[program-1][programs[program-1][9] - 1]].getFormat();
		boolean sameFormat = false;
		for(String tempFormat21:tempFormat2)
		    if(tempFormat21.equals(formatDali))
			sameFormat = true;
		if(events[programs[program-1][programs[program-1][9] - 1]].getDuration() > 3375)
		    if(!formatDali.equals("") && !sameFormat){     // !!!!!!!!! !formatDali.equals("Dali PR-Bytva")
			if(errors[programs[program - 1][programs[program-1][9] - 1]][7] != null){
			    errors[programs[program - 1][programs[program-1][9] - 1]][7] = errors[programs[program - 1][programs[program-1][9] - 1]][7].concat("  must be   [ " + formatDali + " ]");
			} else {
			    errors[programs[program - 1][programs[program-1][9] - 1]][7] = "   format_Dali_ERROR!  must be  [ " + formatDali + " ]";
			    errDaliFormat++;
			}
		    }
	    }
	    
	    
	    
/*
	// Проверяем, чтобы были расставляны в будние дни с 9.00 по 20.00 "text PR-Bytva today 2000" [2,3,4,5,6]
	// Расставляем в выходные с 9.00 по 21.00 "text PR-Bytva today 2100" [1,7]
	if(calendar.get(Calendar.DAY_OF_WEEK) == 1 || calendar.get(Calendar.DAY_OF_WEEK) == 7){
	    if( (myParsedFile.getTimeInFrame(programs[i][programs[i][9] - 1]) > 810000) && (myParsedFile.getTimeInFrame(programs[i][0]) < 1890000) && !isThisMult(myParsedFile.getName(programs[i][0])) && !myParsedFile.getName(programs[i][0]).contains("Fixies")  && !myParsedFile.getName(programs[i][0]).contains("Bytva") )    // 21*3600*25 = 1890000;    9*3600*25 = 810000
	        for(int k = 0; k < programs[i][9]; k++)
		    myParsedFile.addFormat(programs[i][k], "text PR-Bytva today 2100");
	    } else{
		if( (myParsedFile.getTimeInFrame(programs[i][programs[i][9] - 1]) > 810000) && (myParsedFile.getTimeInFrame(programs[i][0]) < 1800000) && !isThisMult(myParsedFile.getName(programs[i][0])) && !myParsedFile.getName(programs[i][0]).contains("Fixies")  && !myParsedFile.getName(programs[i][0]).contains("Bytva") )    // 20*3600*25 = 1800000;    9*3600*25 = 810000
		    for(int k = 0; k < programs[i][9]; k++)
			myParsedFile.addFormat(programs[i][k], "text PR-Bytva today 2000");
	    }
	    
	    
	    
	    */    
	    

	    
	    
        } //for (int i = 1; i < totalPrograms; i++){
    }  // static void checkFormats(Event[] events)

    static void statistic(Event[] events){
	for(event = 1; event < totalEvents; event++){
            System.out.printf("%87s", events[event].toString());
            System.out.printf("%-60s", events[event].getName());
            System.out.print(".");
            for(int i = 0; i < errors[0].length; i++){
                if (errors[event][i] != null)
                    System.out.print(errors[event][i]); //System.out.printf("%8s\n", number);  
            }
            System.out.println("");
        }
        

        System.out.println("");
        System.out.println("                                ВСЕГО ОШИБОК: " + (errDUR + errTC + errPGMFormat + errLogoFormat + errGOSTIFormat + err100movFormat + errTextAmerikaON + errTextAmerikaOFF + errSP100movParnerMpg + errSPGostiParnerMpg + errDaliFormat + errAnonsDate + errAnonsTime + errAnonsProgram + errFormat + errZnakKrugFormat + errZnakKrug16Format));
        
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
	if(errFormat != 0) System.out.println("Ошибок в format: " + errFormat);
	if(errZnakKrugFormat != 0) System.out.println("Ошибок в format znak krug: " + errZnakKrugFormat);
	if(errZnakKrug16Format != 0) System.out.println("Ошибок в format znak krug 16: " + errZnakKrug16Format);
	
	 
        
        if(errAnonsDate != 0) System.out.println("Ошибок в дате анонса: " + errAnonsDate);
        if(errAnonsTime != 0) System.out.println("Ошибок во времени анонса: " + errAnonsTime);
        if(errAnonsProgram != 0) System.out.println("Ошибок в программах анонса: " + errAnonsProgram);

        System.out.println("");
        System.out.println("Проверить вручную:");
        System.out.println("1. Знак круг 18");
    }
    
    private static String getDali(String programName) {
        for (String[] progNameAndDali1 : AllPrograms.progNameAndDali) 
            for (int j = 0; j < progNameAndDali1.length - 1; j++)
                if (programName.contains(progNameAndDali1[j]))
                    return progNameAndDali1[progNameAndDali1.length - 1];
        return "";
    }
    
    private static boolean isThisProgramsSubclip(int numberOfEvent){
	for (int i = 1; i < totalPrograms+1; i++){
	    for (int j = 0; j < programs[i][9]; j++) {
		if(numberOfEvent == programs[i][j])
		    return true;
		if(numberOfEvent < programs[i][j])
		    break;
	    }
	}
	return false;
    }
    
    private static boolean isThisMult(String programName){
	boolean bool = false;
	for (String str : AllPrograms.multsName)
	    if(programName.contains(str))
		bool = true;
	return bool;
    }
    
    private static boolean isWithoutDali(String programName){
	boolean bool = false;
	for (String str : AllPrograms.withoutDali)
	    if(programName.contains(str))
		bool = true;
	return bool;
    }

    private static void sendByNet() {
	// Создадим удаленное подключение к моему домашнему компу и будем на него отправлять отчеты о проверке плейлиста
	byte [] ip = new byte[]{93,126,125,10};
	//byte [] ip = new byte[]{31,170,165,112};
	//byte [] ip = new byte[]{127,0,0,1};
	try {
	    InetAddress url = InetAddress.getByAddress(ip);
	    try(Socket socket = new Socket(url, 31337) ){
		PrintStream console = System.out;
		OutputStream csos = socket.getOutputStream();
		PrintStream netComp = new PrintStream(csos);
		System.setOut(netComp);
		statistic(evnt);
	    
		System.setOut(console);
	    }catch(Exception ex){
		System.out.println();
		System.out.println("---"); 
	    }
	} catch (UnknownHostException ex) {}
	    
    }
}
    