package com.yuelin.o2cabin.bean;

/**
 * Created by Administrator on 2018\5\4 0004.
 */

public class DiseasesBean {
    /**
     * id : 104
     * name : 进食障碍
     * sort : 1
     * price : 0
     * delete : 0
     * minutes : 60
     * scopesex : 3
     * paramscount : 0
     */

    private int id;
    private String name;
    private int sort;
    private int price;
    private int delete;
    private int minutes;
    private int scopesex;
    private int paramscount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDelete() {
        return delete;
    }

    public void setDelete(int delete) {
        this.delete = delete;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getScopesex() {
        return scopesex;
    }

    public void setScopesex(int scopesex) {
        this.scopesex = scopesex;
    }

    public int getParamscount() {
        return paramscount;
    }

    public void setParamscount(int paramscount) {
        this.paramscount = paramscount;
    }
}