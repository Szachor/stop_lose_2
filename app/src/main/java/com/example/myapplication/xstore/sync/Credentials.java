package com.example.myapplication.xstore.sync;

public class Credentials {

	private final String login;
	private final String password;
	private String appId;
	private String appName;

	public Credentials(String login, String password) {
		this.login = login;
		this.password = password;
	}

	public Credentials(String login, String password, String appName) {
		this(login, password);
		this.appName = appName;
	}

	public Credentials(String login, String password, String appId, String appName) {
		this(login, password, appName);
		this.appId = appId;
	}

	public String getAppId() {
		return appId;
	}

	public String getAppName() {
		return appName;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}	
}