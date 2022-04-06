package com.test;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

// определяем псевдоним уровня класс
@XStreamAlias("dbinfo")
public class DbInfo {
    // определяем список как неявную коллекцию
    @XStreamImplicit
    private List<Well> wellList = new ArrayList<>();


    public List<Well> getWellList() {
        return wellList;
    }

    public void setWellList(List<Well> wellList) {
        this.wellList = wellList;
    }
}
