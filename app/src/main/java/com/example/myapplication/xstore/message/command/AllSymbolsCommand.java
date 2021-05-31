package com.example.myapplication.xstore.message.command;

import org.json.simple.JSONObject;

import com.example.myapplication.xstore.message.error.APICommandConstructionException;

public class AllSymbolsCommand extends BaseCommand {

    public AllSymbolsCommand() throws APICommandConstructionException {
        super(new JSONObject());
    }

    @Override
    public String getCommandName() {
        return "getAllSymbols";
    }

    @Override
    public String[] getRequiredArguments() throws APICommandConstructionException {
        return new String[]{};
    }
}
