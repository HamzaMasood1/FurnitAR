package com.razi.furnitar.Models;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceModel {
    public int id = 0;
    public String name = "";
    public String address = "";
    public String online = "";
    public String time = "";
    public double lat = 0.0;
    public double lon = 0.0;
    public int speed = 0;
    public int odometer = 0;
    public String ignition = "";
    public JSONObject device_data;
    public String icon_type = "";
    public String icon_color = "";
    public JSONObject icon;
    public DeviceModel() {

    }

    public DeviceModel(JSONObject obj) {

        try {
            this.id = obj.getInt("id");
            if (obj.getString("name") != null) { this.name = obj.getString("name");}
            if (obj.getString("online") != null) { this.online = obj.getString("online");}
            if (obj.getString("time") != null) { this.time = obj.getString("time");}
            if (obj.getString("address") != null) { this.address = obj.getString("address");}
            if (obj.getJSONObject("device_data") != null) { this.device_data = obj.getJSONObject("device_data");}
            this.lat = obj.getDouble("lat");
            this.lon = obj.getDouble("lng");
            this.speed = obj.getInt("speed");
            if (obj.getString("icon_type") != null) { this.icon_type = obj.getString("icon_type");}
            if (obj.getString("icon_color") != null) { this.icon_color = obj.getString("icon_color");}
            if (obj.getJSONObject("icon") != null) { this.icon = obj.getJSONObject("icon");}
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
