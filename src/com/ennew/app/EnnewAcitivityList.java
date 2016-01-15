package com.ennew.app;

import java.util.LinkedList;
import java.util.List;

import android.R.integer;
import android.app.Activity;
/**
 * activity 集合
 * @author jianglihui
 *
 */
public class EnnewAcitivityList {
	public static List<Activity> activityList = new LinkedList<Activity>();

	private static EnnewAcitivityList instance;

	public static EnnewAcitivityList getInstance() {
		if (instance == null) {
			return new EnnewAcitivityList();
		}
		return instance;
	}
    
	public void addActivity(Activity activity) {
		//activity.getLocalClassName();
		activityList.add(activity);
	}

	public void removeActivity(Activity activity) {
		activityList.remove(activity);
	}

	public void delActivity(Activity activity) {
		activity.finish();
		// activityList.remove(activity);
	}

	public void exitAccount() {
		int size = activityList.size();
		for (int i = 0; i < size; i++) {
			if (activityList.get(i) != null) {
				activityList.get(i).finish();
			}
		}
		activityList.clear();
	}
	/**
	 * 退出多个Acitivity 
	 * */
	public void exitAccount(int num) {
		int size = activityList.size()-1;
		int index=size-num;
		for (int i = size; i > index; i--) {
			if (activityList.get(i) != null) {
				activityList.get(i).finish();
				activityList.remove(i);
			}
		}	 
	}
	public void exitAccountExceptItem(Activity item) {
		int size = activityList.size();
		for (int i = 0; i < size; i++) {
			Activity activity = activityList.get(i);
			if (activity != null) {
				if (!activity.equals(item)) {
					activityList.get(i).finish();
				}
			}
		}
		activityList.clear();
		activityList.add(item);
	}

	public void killall() {

		int size = activityList.size();
		for (int i = 0; i < size; i++) {
			if (activityList.get(i) != null) {
				activityList.get(i).finish();
			}
		}
		activityList.clear();
		// android.os.Process.killProcess(android.os.Process.myPid());
		// System.exit(0);
	}
}
