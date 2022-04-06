package com.test;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

// определяем псевдоним "well" уровня класс
@XStreamAlias("well")
public class Well {

    @XStreamAsAttribute //определяем поле как атрибут
    @XStreamAlias("name") //определяем псевдоним уровня поле
    private String wellName;

    @XStreamAsAttribute
    @XStreamAlias("id")
    private int wellId;

    @XStreamImplicit //определяем список как неявную коллекцию
    private List<Equipment> equipmentList;

    public int getWelId() {
        return wellId;
    }

    public void setWelId(int welId) {
        this.wellId = welId;
    }

    public String getWellName() {
        return wellName;
    }

    public void setWellName(String wellName) {
        this.wellName = wellName;
    }


    public List<Equipment> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(List<Equipment> equipmentList) {
        this.equipmentList = equipmentList;
    }
}
