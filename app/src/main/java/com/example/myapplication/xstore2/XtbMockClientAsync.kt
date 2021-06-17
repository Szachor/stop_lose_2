package com.example.myapplication.xstore2;
import org.json.JSONException;

class XtbMockClientAsync extends XtbClientAsync {
    public XtbMockClientAsync() {
        super(new XtbMockClient());
    }
}


class XtbMockClient extends XtbClient {
    public XtbMockClient() {
        super("Mock login", "Mock password");
    }

    @Override
    public Boolean connect() throws JSONException {
        if (isConnected()) {
            return true;
        }

        _webSocket = new XtbWebSocketMock(false);
        _streamingWebSocket = new XtbWebSocketMock(true);

        // Login should be moved from this place somewhere else...
        this.login();
        return true;
    }
}
