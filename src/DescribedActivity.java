public class DescribedActivity extends Activity{

    private String description;

    public DescribedActivity(String header, Category category, String description, int startHour, int stopHour, int startMinute, int stopMinute, Day day, boolean subscription){
        super(header, category, startHour, stopHour, startMinute, stopMinute, day, subscription);
        this.description = description.replaceAll("\\r\\n|[\\r\\n]","\\\\n");
    }
    public String getDescription(){
        return description;
    }

    public String toString(){
        return getHeader() + "##BREAK@" + getCategory().getName() + "##BREAK@" +  getStartHour() + "##BREAK@" +  getStopHour() + "##BREAK@" +  getStartMinute() + "##BREAK@" +  getStopMinute() + "##BREAK@" +  getStartDay().toString("digit") + "##BREAK@" +  getStopDay().toString("digit") + "##BREAK@" + description;
    }
    public String toString(String arg){
        if (arg.equals("simple"))
            return getHeader();
        else
            return "wrong arg.";
    }
}
