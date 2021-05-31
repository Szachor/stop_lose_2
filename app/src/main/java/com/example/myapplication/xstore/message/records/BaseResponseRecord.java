package com.example.myapplication.xstore.message.records;

import org.json.simple.JSONObject;

public interface BaseResponseRecord {
    public abstract void setFieldsFromJSONObject(JSONObject ob);
}