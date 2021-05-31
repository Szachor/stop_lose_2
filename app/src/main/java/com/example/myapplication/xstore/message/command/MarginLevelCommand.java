package com.example.myapplication.xstore.message.command;

import org.json.simple.JSONObject;

import com.example.myapplication.xstore.message.error.APICommandConstructionException;

public class MarginLevelCommand extends BaseCommand {

    public MarginLevelCommand() throws APICommandConstructionException {
        super(new JSONObject());
    }

    @Override
    public String getCommandName() {
        return "getMarginLevel";
    }

    @Override
    public String[] getRequiredArguments() throws APICommandConstructionException {
        return new String[]{};
    }

    @Override
    public Long getTimeoutMillis() {
        return 200L;
    }
}
