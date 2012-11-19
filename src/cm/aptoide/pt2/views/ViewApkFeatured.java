/**
 * 
 */
package cm.aptoide.pt2.views;

/**
 * @author rmateus
 *
 */
public class ViewApkFeatured extends ViewApk {

	
	private String featureGraphic;
	private boolean highlighted = false;
	

	public String getFeatureGraphic() {
		return featureGraphic;
	}

	public void setFeatureGraphic(String featureGraphic) {
		this.featureGraphic = featureGraphic;
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}
	
}
