package com.zhaodongdb.common.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public final class FoundationContextHolder {

	public static Context context;
	private static Application mainApplication;

	public static Application getApplication() {
		return mainApplication;
	}

	public static void setApplication(Application application) {
		FoundationContextHolder.mainApplication = application;
	}

	public static void setContext(Context context) {
		FoundationContextHolder.context = context;
	}

	public static Context getContext() {
		return context;
	}

//	public static Activity getCurrentActivity() {
//		return FoundationLibConfig.getBaseInfoProvider().getCurrentActivity();
//	}
}
