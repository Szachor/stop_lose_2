package com.example.myapplication.xstore.message.command;

import org.json.simple.JSONObject;

import com.example.myapplication.xstore.message.error.APICommandConstructionException;

public class CalendarCommand extends BaseCommand {

    public CalendarCommand() throws APICommandConstructionException {
        super(new JSONObject());
    }

    @Override
    public String getCommandName() {
        return "getCalendar";
    }

    @Override
    public String[] getRequiredArguments() throws APICommandConstructionException {
        return new String[]{};
    }
}