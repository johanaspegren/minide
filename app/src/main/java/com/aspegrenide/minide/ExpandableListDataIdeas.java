package com.aspegrenide.minide;

import android.util.Log;

import com.aspegrenide.minide.data.Idea;
import com.aspegrenide.minide.data.Problem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataIdeas {

    public static HashMap<String, List<String>> getData(ArrayList<Idea> ideas) {

        HashMap<String, List<String>> expandableDetailList = new HashMap<String, List<String>>();

        List<String> retval = new ArrayList<String>();
        for(Idea p: ideas) {
            List<String> details = new ArrayList<String>();
            details.add("Klicka mig för att uppdatera ");
            details.add("Vad:\n" +  p.getApproach());
            expandableDetailList.put(p.getTitle(), details);
            Log.d("EXP_LIST", p.toString());
        }

        return expandableDetailList;
    }
}
