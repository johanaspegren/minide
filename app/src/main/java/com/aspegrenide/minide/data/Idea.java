package com.aspegrenide.minide.data;

public class Idea {

    private String title;
    private String approach;
    private String benefit;
    private String contact;
    private String competition;
    private String uid;
    private boolean open;

    public Idea() {
    }

    @Override
    public String toString() {
        return "Idea{" +
                "title='" + title + '\'' +
                ", approach='" + approach + '\'' +
                ", benefit='" + benefit + '\'' +
                ", contact='" + contact + '\'' +
                ", competition='" + competition + '\'' +
                ", uid='" + uid + '\'' +
                ", open=" + open +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
