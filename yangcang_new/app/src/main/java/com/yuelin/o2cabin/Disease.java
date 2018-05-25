package com.yuelin.o2cabin;

import android.util.Log;

import org.json.*;

public class Disease
{
    public int delete;
    public int id;
    public int minutes;
    public String name;
    public int price;
    public int sort;

    public String getSortLetter() {
        return sortLetter;
    }

    public void setSortLetter(String sortLetter) {
        this.sortLetter = sortLetter;
    }

    public String sortLetter;

    public boolean PaserFromJsonObject(final JSONObject jsonObject) {
        try {
            this.id = jsonObject.getInt("id");
            this.name = jsonObject.getString("name");
            this.sort = jsonObject.getInt("sort");
            this.price = jsonObject.getInt("price");
            if (this.price <= 0) {
                this.price = 1;
            }
            if (jsonObject.has("delete")) {
                this.delete = jsonObject.getInt("delete");
            }
            else {
                this.delete = 0;
            }
            if (jsonObject.has("minutes")) {
                this.minutes = jsonObject.getInt("minutes");
                Log.e("minutesssss",this.minutes + ".");
                return true;
            }
            this.minutes = 60;
            return true;
        }
        catch (Exception ex) {
            try {
                final StringBuilder sb = new StringBuilder();
                sb.append("PaserFromJsonObject: id= ");
                sb.append(this.id);
                sb.append(" name =");
                sb.append(this.name);
                sb.append(" params= ");
                sb.append(jsonObject.getString("params"));
                MyLog.d("Disease", sb.toString());
            }
            catch (Exception ex2) {}
            MyLog.log(ex);
            return false;
        }
    }

    public JSONObject ToJSONObject() {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", this.id);
            jsonObject.put("name", (Object)this.name);
            jsonObject.put("sort", this.sort);
            jsonObject.put("price", this.price);
            jsonObject.put("delete", this.delete);
            jsonObject.put("paramscount", 0);
            jsonObject.put("minutes", this.minutes);
            return jsonObject;
        }
        catch (Exception ex) {
            MyLog.log(ex);
            return null;
        }
    }
}
