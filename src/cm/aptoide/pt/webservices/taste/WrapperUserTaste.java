package cm.aptoide.pt.webservices.taste;




public class WrapperUserTaste{
	
	private EnumUserTaste userTaste;
	private int operatingThreads;
	public WrapperUserTaste(){userTaste=EnumUserTaste.NOTEVALUATED; operatingThreads=0;}
	public EnumUserTaste getValue(){ return userTaste; } 
	public void setValue(EnumUserTaste userTaste){ this.userTaste = userTaste; }
	public void incOperatingThreads(){ operatingThreads++; }
	public void decOperatingThreads(){ 
		if(operatingThreads!=0)
			operatingThreads--;
	}
	public int getOperatingThreads(){ return operatingThreads; }
	
}
