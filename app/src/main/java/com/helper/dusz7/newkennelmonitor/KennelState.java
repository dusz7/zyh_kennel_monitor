package com.helper.dusz7.newkennelmonitor;

import cn.bmob.v3.BmobObject;

/**
 * Created by dusz7 on 20180415.
 */

public class KennelState extends BmobObject {

    public static final int STATE_NORMAL = 0;
    public static final int STATE_OPERATION = 1;
    public static final int STATE_ERROR = 2;

    private String kennelId;
    private int kennelState;

    private float temperature1;
    private float temperature2;
    private float temperature3;
    private float temperature4;
    private float temperature5;
    private float temperature6;

    private float humidity1;
    private float humidity2;
    private float humidity3;
    private float humidity4;
    private float humidity5;
    private float humidity6;

    public String getKennelId() {
        return kennelId;
    }

    public void setKennelId(String kennelId) {
        this.kennelId = kennelId;
    }

    public int getKennelState() {
        return kennelState;
    }

    public void setKennelState(int kennelState) {
        this.kennelState = kennelState;
    }

    public float getTemperature1() {
        return temperature1;
    }

    public void setTemperature1(float temperature1) {
        this.temperature1 = temperature1;
    }

    public float getTemperature2() {
        return temperature2;
    }

    public void setTemperature2(float temperature2) {
        this.temperature2 = temperature2;
    }

    public float getTemperature3() {
        return temperature3;
    }

    public void setTemperature3(float temperature3) {
        this.temperature3 = temperature3;
    }

    public float getTemperature4() {
        return temperature4;
    }

    public void setTemperature4(float temperature4) {
        this.temperature4 = temperature4;
    }

    public float getTemperature5() {
        return temperature5;
    }

    public void setTemperature5(float temperature5) {
        this.temperature5 = temperature5;
    }

    public float getTemperature6() {
        return temperature6;
    }

    public void setTemperature6(float temperature6) {
        this.temperature6 = temperature6;
    }

    public float getHumidity1() {
        return humidity1;
    }

    public void setHumidity1(float humidity1) {
        this.humidity1 = humidity1;
    }

    public float getHumidity2() {
        return humidity2;
    }

    public void setHumidity2(float humidity2) {
        this.humidity2 = humidity2;
    }

    public float getHumidity3() {
        return humidity3;
    }

    public void setHumidity3(float humidity3) {
        this.humidity3 = humidity3;
    }

    public float getHumidity4() {
        return humidity4;
    }

    public void setHumidity4(float humidity4) {
        this.humidity4 = humidity4;
    }

    public float getHumidity5() {
        return humidity5;
    }

    public void setHumidity5(float humidity5) {
        this.humidity5 = humidity5;
    }

    public float getHumidity6() {
        return humidity6;
    }

    public void setHumidity6(float humidity6) {
        this.humidity6 = humidity6;
    }
}
