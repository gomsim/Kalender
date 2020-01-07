public class Year {

    private int year;
    private Month[] months = new Month[12];

    public Year(int year, Theme theme){
        this.year = year;
        initiateMonths(theme);
    }

    private void initiateMonths(Theme theme){
        for (int i = 0; i < months.length; i++){
            months[i] = new Month(i, this, theme);
        }
    }

    public int getAsInt(){
        return year;
    }
    public Month getMonth(int month){
        return months[month];
    }
    public Month getPreviousMonth(Month month){
        return months[month.getAsInt()-1];
    }
    public Month getNextMonth(Month month){
        return months[month.getAsInt()+1];
    }

    public boolean isLeap(){
        return year%4 == 0 && (year%100 != 0 || (year%100 == 0 && year%400 == 0));
    }
    public String toString(){
        return "" + year;
    }
}
