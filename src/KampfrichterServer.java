import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * repräsentiert den Server für unser verteiltes Systeme Projekt
 * 
 * @author Thomas
 */
public class KampfrichterServer extends UnicastRemoteObject 
implements IServer{

	public static void main(String[] args)
	  {
	    try {
	      LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
	    }
	    
	    catch (RemoteException ex) {
	      System.out.println(ex.getMessage());
	    }
	    try {
	      Naming.rebind("Server", new KampfrichterServer());
	    }
	    catch (MalformedURLException ex) {
	      System.out.println(ex.getMessage());
	    }
	    catch (RemoteException ex) {
	      System.out.println(ex.getMessage());
	    }
	  }
	
	protected KampfrichterServer() throws RemoteException {
		super();
	}

	private static final long serialVersionUID = 3701445934486704839L;

	/*
	 * ---------------------------------wichtig ----------------------
	 */
	/**
	 * fügt eine, vom Client vorgeschlagene, Übersetzung ein
	 */
	public String insert(String neueBezeichnung) {
		return "hallo Welt";
	}
	
	/**
	 * fügt eine, vom Client vorgeschlagene, Übersetzung ein
	 */
	public void insertNewTranslation(String VideoName, String neueBezeichnung) {
	}

	/**
	 * findet video aus der Server datenbank
	 */
	private void findVideo(String name) {
		// wichtig
	}

	/**
	 * Der Server sendet ein Video zu dem anfragenden Client.
	 */
	public void sendVideo(String videoName) {
		findVideo(videoName);
		// sende Video
	}

	/**
	 * fügt ein neues Video eines anderen Servers oder Clients ein
	 */
	public void insertNewVideo(String videoName) {
		// sehr wichtig
	}

	
}
