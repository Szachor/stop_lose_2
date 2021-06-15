package com.example.myapplication.xstore2;
import org.json.JSONException;

class XtbMockServiceAsync extends XtbServiceAsync {
    public XtbMockServiceAsync() {
        super(new XtbMockService());
    }
}


class XtbMockService extends XtbService {
    public XtbMockService() {
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
