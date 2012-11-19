/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt2;

import java.io.File;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class DatabaseContext extends ContextWrapper {
	private static final String DEBUG_CONTEXT = "DatabaseContext";

	public DatabaseContext(Context base) {
	    super(base);
	}

	@Override
	public File getDatabasePath(String name) 
	{
	    File sdcard = Environment.getExternalStorageDirectory();    
	    String dbfile = sdcard.getAbsolutePath() + File.separator+ ".aptoide" + File.separator + name;
	    if (!dbfile.endsWith(".db"))
	    {
	        dbfile += ".db" ;
	    }

	    File result = new File(dbfile);

	    if (!result.getParentFile().exists())
	    {
	        result.getParentFile().mkdirs();
	    }

	    if (Log.isLoggable(DEBUG_CONTEXT, Log.WARN))
	    {
	        Log.w(DEBUG_CONTEXT,
	                "getDatabasePath(" + name + ") = " + result.getAbsolutePath());
	    }

	    return result;
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) 
	{
	    SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
	    // SQLiteDatabase result = super.openOrCreateDatabase(name, mode, factory);
	    if (Log.isLoggable(DEBUG_CONTEXT, Log.WARN))
	    {
	        Log.w(DEBUG_CONTEXT,
	                "openOrCreateDatabase(" + name + ",,) = " + result.getPath());
	    }
	    return result;
	}

}
