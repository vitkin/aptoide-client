package cm.aptoide.pt;

public class ServerLatest extends Server {

	public ServerLatest(Server server){
		super();
        url = server.url;
		id=server.id;
	}

}
