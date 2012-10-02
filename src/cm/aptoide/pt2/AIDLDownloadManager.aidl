/**
 * AIDLDownloadManager,		part of Aptoide
 * Copyright (C) 2012 Duarte Silveira
 * duarte.silveira@caixamagica.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package cm.aptoide.pt2;

import cm.aptoide.pt2.views.ViewDownload;

/**
 * AIDLDownloadManager, models Aptoide's download manager AIDL IPC callback API for ApplicationServiceManager
 *
 * @author dsilveira
 *
 */
interface AIDLDownloadManager{
	void updateDownloadStatus(in int id, in ViewDownload update);	
}
