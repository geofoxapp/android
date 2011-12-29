package com.geofoxapp.android;

import android.app.Application;

public class GeofoxApplication extends Application{
	
	
	private String userEmail;
	private String userPass;

	private String fname;
	private String lname;
	private int phoneNum;
	
	public FoxServerManager fsm;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		userEmail = userPass = "";
		
		fsm = new FoxServerManager(userEmail, userPass);
		
	}
	
	public String getUserEmail()
	{
		return userEmail;
	}
	
	
	public boolean isUserLoggedIn()
	{
		if ((userEmail == "") || (userPass == ""))
			return false;
		else
			return fsm.loginRefreshAuthKey();
	}
	
	public boolean setLoginInfo(String email, String pass)
	{
		if (email == "" || pass == "")
			return false;
		
		if(fsm.setLoginInfo(email, pass))
		{
			userEmail = email;
			userPass = pass;
			return true;
		}
		else
			return false;
	}
	
	
	@Override
	public void onTerminate()
	{
		super.onTerminate();
	}
	
}
