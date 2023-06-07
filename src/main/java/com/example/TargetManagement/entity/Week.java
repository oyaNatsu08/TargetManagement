package com.example.TargetManagement.entity;

import java.util.List;

public class Week {

    Boolean every = false;
    Boolean mon = false;
    Boolean tues = false;
    Boolean wednes = false;
    Boolean thurs = false;
    Boolean fri = false;
    Boolean satur = false;
    Boolean sun = false;

    public Week(List<String> weeks) {

        for (String week : weeks) {

            if (week.equals("every")) {
                every = true;
            }
            if (week.equals("mon")) {
                mon = true;
            }
            if (week.equals("tues")) {
                tues = true;
            }
            if (week.equals("wednes")) {
                wednes = true;
            }
            if (week.equals("thurs")) {
                thurs = true;
            }
            if (week.equals("fri")) {
                fri = true;
            }if (week.equals("satur")) {
                satur = true;
            }if (week.equals("sun")) {
                sun = true;
            }

        }

    }

    public Boolean getEvery() {
        return every;
    }

    public Boolean getMon() {
        return mon;
    }

    public Boolean getTues() {
        return tues;
    }

    public Boolean getWednes() {
        return wednes;
    }

    public Boolean getThurs() {
        return thurs;
    }

    public Boolean getFri() {
        return fri;
    }

    public Boolean getSatur() {
        return satur;
    }

    public Boolean getSun() {
        return sun;
    }
}
