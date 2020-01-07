public class NamedActivity extends Activity{

    public NamedActivity(String header, Category category, int startHour, int stopHour, int startMinute, int stopMinute, Day day, boolean subscription){
        super(header, category, startHour, stopHour, startMinute, stopMinute, day, subscription);
    }

    public String toString(){
        return getHeader() + "##BREAK@" + getCategory().getName() + "##BREAK@" +  getStartHour() + "##BREAK@" +  getStopHour() + "##BREAK@" +  getStartMinute() + "##BREAK@" +  getStopMinute() + "##BREAK@" +  getStartDay().toString("digit") + "##BREAK@" +  getStopDay().toString("digit");
    }
    public String toString(String arg){
        if (arg.equals("simple"))
            return getHeader();
        else
            return "wrong arg.";
    }
}
