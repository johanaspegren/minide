package com.aspegrenide.minide;

import android.util.Log;

import com.aspegrenide.minide.data.Problem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataProblems {

    public static HashMap<String, List<String>> getData(ArrayList<Problem> problems) {

        HashMap<String, List<String>> expandableDetailList = new HashMap<String, List<String>>();

        List<String> retval = new ArrayList<String>();
        for(Problem p: problems) {
            List<String> details = new ArrayList<String>();
            details.add("Klicka mig för att uppdatera ");
            details.add("Vad:\n" +  p.getWhat());
            expandableDetailList.put(p.getTitle(), details);
            Log.d("EXP_LIST", p.toString());
        }

        return expandableDetailList;
    }
}
