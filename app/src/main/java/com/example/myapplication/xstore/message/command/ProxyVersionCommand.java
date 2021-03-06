package com.example.myapplication.xstore.message.command;

import org.json.simple.JSONObject;

import com.example.myapplication.xstore.message.error.APICommandConstructionException;

public class ProxyVersionCommand extends BaseCommand {

    public ProxyVersionCommand() throws APICommandConstructionException {
        super(new JSONObject());
    }

    @Override
    public String getCommandName() {
        return "getDateOfBuild";
    }

    @Override
    public String[] getRequiredArguments() throws APICommandConstructionException {
        return new String[]{};
    }
}