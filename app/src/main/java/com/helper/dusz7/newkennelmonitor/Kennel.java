package com.helper.dusz7.newkennelmonitor;

import org.litepal.crud.DataSupport;

/**
 * Created by dusz7 on 20180415.
 */

public class Kennel extends DataSupport {

    private String kennelId;
    private String nickname;

    public Kennel() {
    }

    public Kennel(String kennelId) {
        this.kennelId = kennelId;
    }

    public String getId() {
        return kennelId;
    }

    public void setId(String id) {
        this.kennelId = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
