/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt;

import java.text.SimpleDateFormat;

/**
 * @author rafael
 * @since summerinternship2011
 *
 */
public class Configs {

	public final static SimpleDateFormat TIME_STAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public final static String LINE_SEPARATOR = System.getProperty("line.separator");

	public static final String LOGIN_USER_ID 	= "useridLogin";
	public static final String LOGIN_PASSWORD 	= "passwordLogin";
	public static final String LOGIN_USER_LOGIN 	= "usernameLogin";
	public static final String LOGIN_USER_TOKEN = "usernameToken";
	public static final String LOGIN_USER_USERNAME = "userName";
    public static final String LOGIN_DEFAULT_REPO = "defaultRepo";

	public static final boolean SEARCH_GESTURE_ON			= false;
	public static final double 	MIN_SEARCH_GESTURE_CONFIANCE= 1.5d;
	public static final boolean INTERFACE_SILVER_TABS_ON	= false;
	public static final boolean INTERFACE_TABS_ON_BOTTOM 	= false;
	public static final boolean BACKGROUND_ON_TABS 			= false;

	public static final int SWIPE_SLIDE_TO_TAB_MIN_DISTANCE 		= 170;
	public static final int SWIPE_SLIDE_TO_TAB_THRESHOLD_VELOCITY 	= 250;

	public static final String[] GESTURE_SEARCH_NOT_REACT_TO = new String[] {"left", "right"};

	public static final String TERMINAL_INFO = android.os.Build.MODEL + "("+ android.os.Build.PRODUCT + ")"
	+";v"+android.os.Build.VERSION.RELEASE+";"+System.getProperty("os.arch");
	//TODO
	/**
	 * The minimum amount of items to have below your current scroll position, before loading more.
	 */
	public final static int VISIBLE_THRESHOLD_COMMENTS = 4;
	/**
	 * The number of comments to retrieve per fetch
	 */
	public final static int COMMNETS_TO_LOAD = 4;

	public final static boolean TASTE_ON = true;
	public final static boolean COMMENTS_ON = true;
	public final static boolean COMMENTS_ADD_ON = true; // For this option have any meaning COMMENTS_LIST_ON = true;
	public final static boolean TASTE_ADD_ON = true; 	// For this option have any meaning TASTE_LIST_ON = true;






	private Configs(){}

}
