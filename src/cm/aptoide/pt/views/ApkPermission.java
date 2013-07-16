package cm.aptoide.pt.views;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 15-07-2013
 * Time: 15:52
 * To change this template use File | Settings | File Templates.
 */
public class ApkPermission {

    private String name;
    private String description;

    public ApkPermission(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
