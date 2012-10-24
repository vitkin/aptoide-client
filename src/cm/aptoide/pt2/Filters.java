package cm.aptoide.pt2;

public class Filters {

	public static enum Screens {
		notfound,small,normal,large,xlarge;
		
		static Screens lookup(String screen){
			try{
				return valueOf(screen);
			}catch (Exception e) {
				return notfound;
			}
			
			
		}
		
	}
	
	public static enum Ages {
		All,Mature;
		static Ages lookup(String age){
			try{
				return valueOf(age);
			}catch (Exception e) {
				return All;
			}
			
			
		}
	}
}
