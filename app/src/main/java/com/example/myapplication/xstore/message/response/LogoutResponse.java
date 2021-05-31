package com.example.myapplication.xstore.message.response;

import com.example.myapplication.xstore.message.error.APIReplyParseException;

public class LogoutResponse extends BaseResponse {

    public LogoutResponse(String body) throws APIReplyParseException, APIErrorResponse {
        super(body);
    }

    @Override
    public String toString() {
        return "LogoutResponse{" + '}';
    }
}
