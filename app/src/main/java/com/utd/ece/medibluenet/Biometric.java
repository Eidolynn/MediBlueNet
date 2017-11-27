package com.utd.ece.medibluenet;

import java.io.Serializable;

/**
 * Created by nath4 on 11/18/2017.
 */

public class Biometric implements Serializable {

    private String biometric="", fullTime="", value="",
            fullDate="", time="", month="", day="",
            year="", hour="", minute="", second="";

    public Biometric(String biometric, String fullTime, String value){
        this.biometric = biometric;
        this.fullTime = fullTime;
        this.value = value;
    }//end public construct

    public void setBiometric(String biometric){this.biometric=biometric;}
    public void setFullTime(String fullTime){this.fullTime = fullTime; parseFullTime();}
    public void setValue(String value){this.value = value;}
    public String getBiometric(){return this.biometric;}
    public String getFullTime(){return this.fullTime;}
    public String getValue(){return this.value;}
    public String getUnitString() {return determineUnits(getBiometric());}
    public String getFullDate() {return fullDate;}
    public String getTime(){return time;}
    public String getMonth(){return month;}
    public String getDay(){return day;}
    public String getYear(){return year;}
    public String getHour(){return hour;}
    public String getMinute(){return minute;}
    public String getSecond(){return second;}

    private void parseFullTime(){
        //expecting fullTime to be set in the format :  "MM/DD/YYYY HH:MM:SS"
        String[] parts = this.fullTime.split(" ");
        this.fullDate = parts[0];
        this.time = parts[1];
        String[] times = this.time.split(":",3);
        this.hour = times[0];
        this.minute = times[1];
        this.second = times[2];
        String[] dates = this.fullDate.split("/",3);
        this.month= dates[0];
        this.day= dates[1];
        this.year= dates[2];
    }//end parseFullTime
    private String determineUnits(String biometric){
        String units="";
        switch(biometric){
            case "heart rate": units = "BPM";break;
            case "bloodPressure": units = "Sys / Dia (mm Hg)";break;
            case "temperature": units = "F";break;
            case "oxigen level": units = "O2%";break;
            case "body weight": units = "Lbs";break;
            case "glucose level": units = "mg/dl";break;
            case "pushButton": units = "push"; break;
            default: units = "Error - units not found";break;
        }//end swtich
        return units;
    }//end determineUnits

}//end Biometric Class
