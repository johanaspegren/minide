package com.aspegrenide.minide.data;

public class Challenge extends Aspect{


    public Challenge() {

    }

    @Override
    public String toString() {
        return "Challenge{" +
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
