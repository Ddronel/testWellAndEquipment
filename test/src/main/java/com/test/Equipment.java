package com.test;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

// определяем псевдоним "equipment" уровня класс
@XStreamAlias("equipment")
public class Equipment {

    @XStreamAsAttribute //определяем поле как атрибут
    @XStreamAlias("name") //определяем псевдоним уровня поле
    private String equipmentName;

    @XStreamAsAttribute
    @XStreamAlias("id")
    private int equipmentId;

    @XStreamOmitField //пропустить поле, чтобы оно не было частью xml файла
    private int equipmantWellId;

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public int getEquipmantWellId() {
        return equipmantWellId;
    }

    public void setEquipmantWellId(int equipmantWellId) {
        this.equipmantWellId = equipmantWellId;
    }
}
