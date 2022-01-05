package com.aspegrenide.minide.data;

public class Idea extends Aspect {

    private String approach;
    private String benefit;
    private String contact;
    private String competition;
    private boolean open;

    public Idea() {

    }

    @Override
    public String toString() {
        return "Idea{" +
                "title='" + title + '\'' +
                ", uid='" + uid + '\'' +
                ", type='" + type + '\'' +
                ", linkedChallenges=" + linkedChallenges +
                ", linkedProblems=" + linkedProblems +
                ", linkedIdeas=" + linkedIdeas +
                ", linkedConcepts=" + linkedConcepts +
                ", approach='" + approach + '\'' +
                ", benefit='" + benefit + '\'' +
                ", contact='" + contact + '\'' +
                ", competition='" + competition + '\'' +
                ", open=" + open +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getApproach() {
        return approach;
    }

    public void setApproach(String approach) {
        this.approach = approach;
    }

    public String getBenefit() {
        return benefit;
    }

    public void setBenefit(String benefit) {
        this.benefit = benefit;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }


}
