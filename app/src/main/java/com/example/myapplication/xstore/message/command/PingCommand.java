package com.example.myapplication.xstore.message.command;

import org.json.simple.JSONObject;

import com.example.myapplication.xstore.message.error.APICommandConstructionException;

public class PingCommand extends BaseCommand {

    public PingCommand() throws APICommandConstructionException {
        super(new JSONObject());
    }

    @Override
    public String getCommandName() {
        return "ping";
    }

    @Override
    public String[] getRequiredArguments() throws APICommandConstructionException {
        return new String[]{};
    }
}