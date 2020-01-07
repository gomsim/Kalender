import java.util.Calendar;

public class Month {

    private int month;
    private Year year;
    private Day[] days;

    public Month(int month, Year year, Theme theme){
        this.month = month;
        this.year = year;
        this.days = new Day[amountDays()];
        initiateDays(year, month, theme);
    }

    private void initiateDays(Year year, int month, Theme theme){
        for (int i = 0; i < days.length; i++){
            Calendar date = Calendar.getInstance();
            date.set(year.getAsInt(), month, i+1);
            days[i] = new Day(date, this, theme);
        }
    }
    public Day getDay(int date) throws ArrayIndexOutOfBoundsException{
        return days[date-1];
    }

    public int amountDays() throws IllegalArgumentException{
        switch(month){
            case 0:
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11:
                return 31;
            case 3:
            case 5:
            case 8:
            case 10:
                return 30;
            case 1:
                return year.isLeap()? 29:28;
            default:
                throw new IllegalArgumentException();
        }
    }

    public Day[] getDays(){
        return days;
    }

    public Year getYear() {
        return year;
    }
    public int getAsInt(){
        return month;
    }

    public String toString(){
        switch(month){
            case 0:
                return "Januari";
            case 1:
                return "Februari";
            case 2:
                return "Mars";
            case 3:
                return "April";
            case 4:
                return "Maj";
            case 5:
                return "Juni";
            case 6:
                return "Juli";
            case 7:
                return "Augusti";
            case 8:
                return "September";
            case 9:
                return "Oktober";
            case 10:
                return "November";
            case 11:
                return "December";
            default:
                throw new IllegalArgumentException();
        }
    }
}
