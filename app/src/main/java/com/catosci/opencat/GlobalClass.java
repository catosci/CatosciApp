package com.catosci.opencat;

import android.app.Application;

enum RobotTypes {
    OPENCAT_BLE,
    SMARTCAR_BLE,
    SMARTCAR_WIFI,
}

public class GlobalClass extends Application{

    private RobotTypes RobotType=RobotTypes.SMARTCAR_BLE;;
    private String SensorMessage="";
    private String  remoteIP ="192.168.1.27";

    public String getSensorMessage() { return SensorMessage; }
    public void setSensorMessage(String aName) { SensorMessage = aName; }

    public RobotTypes getRobotType() { return RobotType; }
    public void setRobotType(RobotTypes aRobotType) { RobotType = aRobotType; }

    public String getRemoteIP() { return remoteIP; }
    public void setRemoteIP(String aRemoteIP) { remoteIP = aRemoteIP; }
}
