/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt.sharing;

import cm.aptoide.com.facebook.android.DialogError;
import cm.aptoide.com.facebook.android.Facebook.DialogListener;
import cm.aptoide.com.facebook.android.FacebookError;

/**
 * Skeleton base class for RequestListeners, providing default error handling.
 * Applications should handle these error conditions.
 */
public abstract class DialogBaseShareListener implements DialogListener {

    @Override
    public void onFacebookError(FacebookError e) {
        e.printStackTrace();
    }

    @Override
    public void onError(DialogError e) {
        e.printStackTrace();
    }

    @Override
    public void onCancel() {
    }

}
