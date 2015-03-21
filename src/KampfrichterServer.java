import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * repr�sentiert den Server f�r unser verteiltes Systeme Projekt
 * 
 * @author Thomas
 */
public class KampfrichterServer extends UnicastRemoteObject implements IServer {

	public static void main(String[] args) {
		try {
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		}

		catch (RemoteException ex) {
			System.out.println(ex.getMessage());
		}
		try {
			Naming.rebind("Server", new KampfrichterServer());
		} catch (MalformedURLException ex) {
			System.out.println(ex.getMessage());
		} catch (RemoteException ex) {
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
	 * Test-Methode
	 */
	public String insert(String neueBezeichnung) {
		return "hallo Welt";
	}

	/**
	 * f�gt eine, vom Client vorgeschlagene, �bersetzung ein
	 */
	public void insertNewTranslation(int id, String neueBezeichnung, String sprache) throws RemoteException {
		DatenbankController dbController = new DatenbankController();
		dbController.updateTranslation(id, neueBezeichnung, sprache);
	}

	/**
	 * findet video aus der Server datenbank
	 */
	public List<Video> findVideo(String name) throws RemoteException {
		VideoParser parser = new VideoParser();
		return parser.mappeVideosVonName(name);

	}

	/**
	 * f�gt ein neues Video eines anderen Servers oder Clients ein
	 */
	public void insertNewVideo(int id, String name, int ampel, String sprache) throws RemoteException{
		DatenbankController dbController = new DatenbankController();
		dbController.addVideo(id, name, ampel, sprache);
	}

}
