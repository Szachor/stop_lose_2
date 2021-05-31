package com.example.myapplication.xstore.message.response;

import com.example.myapplication.xstore.message.error.APIReplyParseException;

public class PingResponse extends BaseResponse {

    public PingResponse(String body) throws APIReplyParseException, APIErrorResponse {
        super(body);
    }

	@Override
	public String toString() {
		return "PingResponse ["+ super.getStatus() + "]";
	}
}