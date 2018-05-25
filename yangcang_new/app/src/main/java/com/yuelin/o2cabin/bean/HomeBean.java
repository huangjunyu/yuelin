package com.yuelin.o2cabin.bean;

import java.util.List;

/**
 * Created by Administrator on 2018\5\4 0004.
 */

public class HomeBean {

    private int code;
    private String msg;
    private String updateversion;
    private String updateinfo;
    private String updateurl;
    private int duedate;
    private int locked;
    private String currenttime;
    private String loginqrcode;
    private String payurl;
    private List<DiseasesBean> diseases;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUpdateversion() {
        return updateversion;
    }

    public void setUpdateversion(String updateversion) {
        this.updateversion = updateversion;
    }

    public String getUpdateinfo() {
        return updateinfo;
    }

    public void setUpdateinfo(String updateinfo) {
        this.updateinfo = updateinfo;
    }

    public String getUpdateurl() {
        return updateurl;
    }

    public void setUpdateurl(String updateurl) {
        this.updateurl = updateurl;
    }

    public int getDuedate() {
        return duedate;
    }

    public void setDuedate(int duedate) {
        this.duedate = duedate;
    }

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public String getCurrenttime() {
        return currenttime;
    }

    public void setCurrenttime(String currenttime) {
        this.currenttime = currenttime;
    }

    public String getLoginqrcode() {
        return loginqrcode;
    }

    public void setLoginqrcode(String loginqrcode) {
        this.loginqrcode = loginqrcode;
    }

    public String getPayurl() {
        return payurl;
    }

    public void setPayurl(String payurl) {
        this.payurl = payurl;
    }

    public List<DiseasesBean> getDiseases() {
        return diseases;
    }

    public void setDiseases(List<DiseasesBean> diseases) {
        this.diseases = diseases;
    }

}
