package com.brotherhood.o2o.bean;

import java.util.List;

/**
 * Created by laimo.li on 2016/1/5.
 */
public class MyGroupBean {
    private String CId;
    private String Cn;
    private int ChnType;
    private List<Member> members;
    private String avatar;


    public String getCId() {
        return CId;
    }

    public void setCId(String CId) {
        this.CId = CId;
    }

    public String getCn() {
        return Cn;
    }

    public void setCn(String cn) {
        Cn = cn;
    }

    public int getChnType() {
        return ChnType;
    }

    public void setChnType(int chnType) {
        ChnType = chnType;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }




}