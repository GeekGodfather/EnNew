package com.ennew.utils;

import com.ennew.ui.fragment.MyAlertFragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;

/**
 * 
 * @author lilong
 * 
 */

public class DialogUtil {
	private static DialogFragment dialog;

	public static void showDialog(Activity activity, String message,
			boolean cancelable) {
		dismissDialog();
		dialog = MyAlertFragment.newInstance("1", "1");
		FragmentTransaction ft = activity.getFragmentManager()
				.beginTransaction();
		dialog.show(ft, "dialog");
	}

	public static void showProgressDialog(Activity activity, String message) {
		showDialog(activity, message, false);
	}

	public static void dismissDialog() {
		if (dialog != null && dialog.isVisible())
			dialog.dismiss();
		dialog = null;
	}
}
