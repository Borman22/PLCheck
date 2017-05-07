
package playlist;

public class Event {
    public int eventType; //9-10 или 11 строк
    
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
        this.eventType = 11;
        this.date = " ";
        name = " ";
        status = " ";
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
        return "Time = " + time.toString() + "   IN = " + tc_in.toString() + "   OUT = " + tc_out.toString() + "   DUR = " + duration.toString() + "   ";
    }
    String strContents(String str, String teg_start, String teg_stop){
         return str.substring((str.indexOf(teg_start) + teg_start.length()), str.lastIndexOf(teg_stop));
    }
    
}
