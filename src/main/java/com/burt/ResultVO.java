package com.burt;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class ResultVO {

    @Excel(name = "Serial Number", orderNum = "0", width = 15)
    private String serialNumber;
    @Excel(name = "Conf File", orderNum = "1", width = 30)
    private String confFile;
    @Excel(name = "Item", orderNum = "2", width = 50)
    private String item;
    @Excel(name = "Dev Value", orderNum = "3", width = 50)
    private String devValue;
    @Excel(name = "Stage Value", orderNum = "4", width = 50)
    private String stageValue;
    @Excel(name = "Prod Value", orderNum = "5", width = 50)
    private String prodValue;
    @Excel(name = "Is Same", orderNum = "6", width = 10)
    private String isSame;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getConfFile() {
        return confFile;
    }

    public void setConfFile(String confFile) {
        this.confFile = confFile;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDevValue() {
        return devValue;
    }

    public void setDevValue(String devValue) {
        this.devValue = devValue;
    }

    public String getStageValue() {
        return stageValue;
    }

    public void setStageValue(String stageValue) {
        this.stageValue = stageValue;
    }

    public String getProdValue() {
        return prodValue;
    }

    public void setProdValue(String prodValue) {
        this.prodValue = prodValue;
    }

    public String getIsSame() {
        return isSame;
    }

    public void setIsSame(String isSame) {
        this.isSame = isSame;
    }

}
