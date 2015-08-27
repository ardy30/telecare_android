package com.ebti.mobile.dohtelecare.util;

import java.util.Stack;

import android.R.bool;
import android.app.Activity;
import android.util.Log;
/*
 * 管理Activity 的 Stack
 */
public class ScreenManager {
	
	private static final String TAG = "ScreenManager";
	
	private static Stack<Activity> activityStack;
	private static ScreenManager instance;
	
	private  ScreenManager(){
	}
	
	public static ScreenManager getScreenManager(){
		//Log.i(TAG, "getScreenManager()");
		if(instance==null){
			instance=new ScreenManager();
		}
		return instance;
	}
	
	public void popActivity(){
		//Log.i(TAG, "popActivity()");
		if(activityStack!=null && activityStack.size()>0){
			Activity activity = activityStack.lastElement();
			if(activity!=null){
				activity.finish();
				activityStack.remove(activity);
				activity=null;
			}
		}
	}
	
	public void popActivity(Activity activity){
		//Log.i(TAG, "popActivity(Activity activity)");
		if(activity!=null){
			activity.finish();
			activityStack.remove(activity);
			activity=null;
		}
	}
	
	public Activity currentActivity(){
		//Log.i(TAG, "currentActivity()");
		Activity activity = null;
		if(activityStack!=null && activityStack.size()>0){
			activity = activityStack.lastElement();
			Log.i(TAG, "activity : " + activity.getClass().getName());
		}
		return activity;
	}
	
	public Activity getFirstActivity(){
		//Log.i(TAG, "getFirstActivity()");
		Activity activity = activityStack.firstElement();
		return activity;
	}
	
	public boolean checkActivityStack(){
		if(activityStack==null){
			return false;
		}else{
			return true;
		}
	}
	
	public void pushActivity(Activity activity){
		//Log.i(TAG, "pushActivity(Activity activity)");
		if(activityStack==null){
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}
	
	public void popAllActivityExceptOne(Class cls){
		//Log.i(TAG, "popAllActivityExceptOne(Class cls), cls = " + cls);
		int i=0;
		while(true){
			//Log.i(TAG, "i : " + i);
			Activity activity = currentActivity();
			//Log.i(TAG, "activity : " + activity);
			if(activity==null){
				break;
			}
			if(activity.getClass().equals(cls) ){
				break;
			}
			popActivity(activity);
			i++;
		}
	}
}