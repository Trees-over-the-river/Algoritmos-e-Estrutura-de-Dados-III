package utils.csv;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateParser {
    private static Map<String, String> months = new HashMap<String, String>(){{
        put("January","01"); 
        put("February","02");
        put("March","03");
        put("April","04");
        put("May","05");
        put("June","06");
        put("July","07");
        put("August","08");
        put("September","09");
        put("October","10");
        put("November","11");
        put("December","12");
    }};

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private String str;
    private Date date;
    
    public DateParser(String date) {
        this.str = date;
        parseDate(date);
    }

    private void parseDate(String s) {
        s = s.replace(",", "").trim();
        String[] arr = s.split(" ");

        try {
            if(arr.length <= 1)  {
                this.date = new Date(System.currentTimeMillis());
            } else {
                s = "";
                s += arr[1] + "-";
                s += months.get(arr[0]) + "-";
                s += arr[2];
        
                this.date = sdf.parse(s);
            }
        } catch(Exception e) {
            System.out.println(s);
            System.out.println(Arrays.toString(arr));
            
            System.err.println("The given string (" + s + ") does not match the pattern.");
            e.printStackTrace();
        }
    }

    public Date getDate() {
        return this.date;
    }

    public String getDateString() {
        return this.str;
    }

}
