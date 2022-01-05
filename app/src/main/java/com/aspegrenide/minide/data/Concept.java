package com.aspegrenide.minide.data;

public class Concept extends Aspect{


    public Concept() {

    }

    @Override
    public String toString() {
        return "Concept{" +
                "title='" + title + '\'' +
                ", uid='" + uid + '\'' +
                ", type='" + type + '\'' +
                ", linkedChallenges=" + linkedChallenges +
                ", linkedProblems=" + linkedProblems +
                ", linkedIdeas=" + linkedIdeas +
                ", linkedConcepts=" + linkedConcepts +
                '}';
    }
}
