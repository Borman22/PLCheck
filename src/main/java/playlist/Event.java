
package playlist;

public class Event {
    public int NumberOfEvent;
    
    private String date;
    private TimeCode time = new TimeCode();
    private TimeCode duration = new TimeCode();
    private TimeCode tc_in = new TimeCode();
    private TimeCode tc_out = new TimeCode();
    private int asset_id = 0;
    private String name;
    private String [] format;
    private String status;

    Event(){
        this.NumberOfEvent = 0;
        this.date = " ";
        name = " ";
        status = " ";
    }
    
    public void setNumberOfEvent(int N){
        this.NumberOfEvent = N;
    }
    
    public void setDate(String DATE){
        this.date = strContents(DATE, "<date>", "</date>");
    }
    public void setTime(String TIME){
        time.setTC(strContents(TIME, "<time>", "</time>"));
    }
    public void setDuration(String DURATION){
        duration.setTC(strContents(DURATION, "<duration>", "</duration>"));
    }
    
    public void setTc_in(String TC_IN){
        String tempStrTCin = strContents(TC_IN, "<tc_in>", "</tc_in>");
        if (tempStrTCin.endsWith("@25"))
            tempStrTCin = tempStrTCin.substring(0, tempStrTCin.length()-3);
        tc_in.setTC(tempStrTCin);
    }
    public void setTc_in(int TC_IN){
        tc_in.setTC(TC_IN);
    }
    
    public void setTc_out(String TC_OUT){
        String tempStrTCout = strContents(TC_OUT, "<tc_out>", "</tc_out>");
        if (tempStrTCout.endsWith("@25"))
            tempStrTCout = tempStrTCout.substring(0, tempStrTCout.length()-3);
        tc_out.setTC(tempStrTCout);
     }
    public void setTc_out(int TC_OUT){
        tc_out.setTC(TC_OUT);
    }
    
    public void setAsset_id(String ID){
        asset_id = Integer.parseInt(strContents(ID, "<asset_id>", "</asset_id>"));
    }
    public void setName(String NAME){
        this.name = strContents(NAME, "<name>", "</name>");
    }
    public void setFormats(String [] FORMATS){
        this.format = FORMATS;
    }
    public void setStatus(String STATUS){
        this.status = strContents(STATUS, "<status>", "</status>");
    }
    
    
    
    
    public int getNumberOfEvent(){
        return NumberOfEvent;
    }
    public String getDate(String DATE){
        return date;
    }
    public int getTime(){
        return time.getTCInFrame();
    }
    public int getDuration(){
        return duration.getTCInFrame();
    }
    public int getTc_in(){
        return tc_in.getTCInFrame();
    }
    public int getTc_out(){
        return tc_out.getTCInFrame();
    }
    public int getAsget_id(){
        return asset_id;
    }
    public String getName(){
        return name;
    }
    public String [] getFormat(){
        return format;
    }
    public String getStatus(String STATUS){
        return status;
    }
    
    @Override
    public String toString(){
        return NumberOfEvent + "   Time = " + time.toString() + "   IN = " + tc_in.toString() + "   OUT = " + tc_out.toString() + "   DUR = " + duration.toString() + "   ";
    }
    String strContents(String str, String teg_start, String teg_stop){
         return str.substring((str.indexOf(teg_start) + teg_start.length()), str.lastIndexOf(teg_stop));
    }
    
    public boolean isSameName(String canonicalNAME){
        return name.startsWith(canonicalNAME);
    }
    public boolean isSameTC_Out(int currentTCInFrame){
        int i = tc_out.getTCInFrame() - currentTCInFrame;
        i = (i > 0) ? i : -i;
        return (i < 25);
    }
    public String getCanonicalName(){
        String tempName = name; 
        int tempIndexEnd = name.indexOf(" сег.");
        int tempIndexVTR = name.indexOf("VTR");
            if(tempIndexEnd == -1)
                tempIndexEnd = tempIndexVTR;
            else if(!(tempIndexVTR == -1))
                tempIndexEnd = (tempIndexEnd < tempIndexVTR) ? tempIndexEnd : tempIndexVTR;

        int tempIndexStart = 0;
        while((tempIndexStart != tempName.length()) && (  (Character.isDigit(tempName.charAt(tempIndexStart)) || (tempName.charAt(tempIndexStart) == '_'))  ) )
            tempIndexStart++;
        
        if((tempIndexStart != 0) | (tempIndexEnd != -1)){
            tempIndexEnd = (tempIndexEnd == -1) ? name.length() : tempIndexEnd;
            tempName = name.substring(tempIndexStart, tempIndexEnd);
        }
        return tempName;
    }
}
