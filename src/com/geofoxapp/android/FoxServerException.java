package com.geofoxapp.android;

import android.util.Log;

public class FoxServerException extends Exception {

	private String message;
	private int code;
	
	public FoxServerException(int errorCode, String msg)
	{
		code = errorCode;
		message = msg;
		Log.v("FoxServerException Created", "Code = " + code + " Msg = " + msg);
	}
	
	public String getMsg()
	{
		return message;
	}
	
	public int getCode()
	{
		return code;
	}
	
}
// My Error Codes
// 999 - Custom msg


/*		API Error Codes
SUCCESS STATES (0000-0099) [no problem here]
        0 - Success. No errors encountered.

NOTICE STATUS (0100-0199) [no problems, but with additional information]
        100 - I was supposed to tell you something, but I forgot.

SERVICE ERROR STATUS (0200-0299) [it's my fault]
        200 - An unknown service error has occurred.
        201 - An internal database error has occurred.

INPUT ERRORS (0300-0399) [it's your fault]
        300 - An unknown input error has occurred.
        301 - Missing a required parameter. Check your syntax.
        302 - Problem with one or more parameters. Check your data.
        303 - Invalid or missing authentication code. You need to run login again.
        304 - Invalid or missing developer code. Please contact support.
        305 - Invalid action. Check the documentation.
        306 - You're not allowed to perform this action.
        307 - Access is denied. Your use of this service has been restricted. Please contact support.
        308 - Bad password email combination.

THIRD PARTY PROBLEMS (0400-0499) [it's ____'s fault]
        400 - An unknown third party error has occurred.
*/