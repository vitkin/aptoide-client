package cm.aptoide.pt.download;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 08-07-2013
 * Time: 15:29
 * To change this template use File | Settings | File Templates.
 */
public class Utils {


     public static String formatEta(long eta){

        if (eta > 0) {
            long days = eta / (1000 * 60 * 60 * 24);
            eta -= days * 1000 * 60 * 60 * 24;
            long hours = eta / (1000 * 60 * 60);
            eta -= hours * 1000 * 60 * 60;
            long minutes = eta / (1000 * 60);
            eta -= minutes * 1000 * 60;
            long seconds = eta / 1000;

            String etaString = "";
            if (days > 0) {
                etaString += days +	"d ";
            }
            if (hours > 0) {
                etaString += hours + "h ";
            }
            if (minutes > 0) {
                etaString += minutes + "m ";
            }
            if (seconds > 0) {
                etaString += seconds + "s";
            }


            return etaString;
        }
        return "";
    }

}

