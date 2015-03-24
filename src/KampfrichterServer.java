import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * repr�sentiert den Server f�r unser verteiltes Systeme Projekt
 * 
 * @author Thomas
 */
public class KampfrichterServer extends UnicastRemoteObject implements IServer {

	/**
	 * Liste mit allen Server-Adressen
	 */
	private List<String> server;

	/**
	 * Liste mit allen Sprachen
	 */
	private List<String> languages;

	private String serverLanguage;

	public static void main(String[] args) {
		try {
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		}

		catch (RemoteException ex) {
			System.out.println(ex.getMessage());
		}
		try {
			Naming.rebind("Server", new KampfrichterServer(
					Konstanten.LANGUAGE_GERMAN));
		} catch (MalformedURLException ex) {
			System.out.println(ex.getMessage());
		} catch (RemoteException ex) {
			System.out.println(ex.getMessage());
		}
	}

	protected KampfrichterServer(String serverLanguage) throws RemoteException,
			MalformedURLException {
		super();
		this.serverLanguage = serverLanguage;
		server = new ArrayList<String>();
		server.add(Konstanten.SERVER_DEUTSCHLAND);
		server.add(Konstanten.SERVER_FRANKREICH);
		// server.add(Konstanten.SERVER_ENGLAND);
		languages = new ArrayList<String>();
		languages.add(Konstanten.LANGUAGE_GERMAN);
		// languages.add(Konstanten.LANGUAGE_ENGLISH);
		languages.add(Konstanten.LANGUAGE_FRENCH);
	}

	private static final long serialVersionUID = 3701445934486704839L;

	/**
	 * f�gt eine, vom Client vorgeschlagene, �bersetzung ein
	 */
	public void insertNewTranslation(int id, String neueBezeichnung,
			String sprache, boolean insertOnOtherServers)
			throws RemoteException {
		DatenbankController dbController = new DatenbankController(
				serverLanguage);
		dbController.updateTranslation(id, neueBezeichnung, sprache);
		if (insertOnOtherServers) {
			updateTranslationOnOtherServers(id, neueBezeichnung, serverLanguage);
		}
	}

	private void updateTranslationOnOtherServers(int id,
			String neueBezeichnung, String sprache) {
		System.out.println("DEUTSCH: updateTranslationOnOtherServers()");
		for (String tmp : server) {
			try {
				IServer iserver = (IServer) Naming.lookup(tmp);
				if (!serverLanguage.equals(iserver.getServerLanguage())) {
					iserver.insertNewTranslation(id, neueBezeichnung, sprache,
							false);
				}
			} catch (MalformedURLException | RemoteException
					| NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * findet video aus der Server datenbank
	 */
	public List<Video> findVideo(String name, boolean searchOnOtherServers)
			throws RemoteException {
		VideoParser parser = new VideoParser(serverLanguage);
		List<Video> videos = parser.mappeVideosVonName(name);
		// TODO testen
		if (videos.size() == 0 && searchOnOtherServers) {
			videos = findVideoOnOtherServers(name);
		}
		return videos;
	}

	/**
	 * f�gt ein neues Video eines anderen Servers oder Clients ein
	 */
	public void insertNewVideo(String name, int ampel, String geraet,
			String beschreibung, String schwierigkeitsgrad,
			String elementgruppe, File video, String sprache)
			throws RemoteException {
		DatenbankController dbController = new DatenbankController(
				serverLanguage);
		int id = dbController.addVideo(-1, name, ampel, geraet, beschreibung,
				schwierigkeitsgrad, elementgruppe, video, sprache);

		// Nach einem Insert sollten andere Server die ID reservieren
		// TODO testen
		if (sprache.equals(serverLanguage)) {
			informServerAboutReservation(id);
		}

	}

	/**
	 * Server reserviert ID in der Datenbank
	 */
	public void reserveId(int id) throws RemoteException {
		DatenbankController dbController = new DatenbankController(
				serverLanguage);
		dbController.addVideo(id, Konstanten.RESERVED_ID, Konstanten.RED, "",
				"", "", "", null, serverLanguage);

	}

	/**
	 * Informiert alle anderen Server darüber, dass eine ID reserviert werden
	 * muss
	 * 
	 * @param id
	 *            zu reservierende ID
	 */
	private void informServerAboutReservation(int id) {
		for (String tmp : server) {
			try {
				IServer iserver = (IServer) Naming.lookup(tmp);
				if (!serverLanguage.equals(iserver.getServerLanguage())) {
					System.out.println("DEUTSCH:  call Server: " + tmp
							+ " reserveId()");
					iserver.reserveId(id);

				}

			} catch (MalformedURLException | RemoteException
					| NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Der Server sucht Video auf anderen Servern
	 */
	/**
	 * @param name
	 * @return
	 */
	private List<Video> findVideoOnOtherServers(String name) {
		List<Video> videos = new ArrayList<Video>();
		for (String tmp : server) {
			try {
				IServer iserver = (IServer) Naming.lookup(tmp);
				if (!serverLanguage.equals(iserver.getServerLanguage())) {
					videos = iserver.findVideo(name, false);
					if (videos.size() != 0) {
						String sprache = iserver.getServerLanguage();
						// Fügt Videos in eigene Datenbank
						for (Video video : videos) {
							insertNewVideo(video.getName(), video.getAmpel(),
									video.getGeraet(), video.getBeschreibung(),
									video.getSchwierigkeitsgrad(),
									video.getElementgruppe(), null, sprache);
						}
						return videos;
					}

				}
			} catch (MalformedURLException | RemoteException
					| NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return videos;
	}

	public String getServerLanguage() throws RemoteException {
		return serverLanguage;
	}

	/**
	 * Holt alle Videos einer Sprache von den anderen Servern
	 */
	private List<Video> restoreVideos() throws RemoteException {
		List<Video> alleVideos = new ArrayList<Video>();
		List<Video> videos;
		for (String tmp : server) {
			try {
				IServer iserver = (IServer) Naming.lookup(tmp);
				if (!serverLanguage.equals(iserver.getServerLanguage())) {
					videos = iserver.getAllVideosByLanguage(serverLanguage);
					for (Video video : videos) {
						if (!alleVideos.contains(video)) {
							alleVideos.add(video);
						}
					}
				}

			} catch (MalformedURLException | RemoteException
					| NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return alleVideos;
	}

	/**
	 * Gibt alle Videos einer Sprache zurück
	 */
	public List<Video> getAllVideosByLanguage(String language)
			throws RemoteException {
		VideoParser parser = new VideoParser(serverLanguage);
		return parser.mappeVideosVonSprache(language);
	}

	/**
	 * Der Server stellt seine Datenbank, mit Hilfe der anderen Servern, wieder
	 * her.
	 * 
	 * @throws RemoteException
	 */
	public void restoreDatabase() throws RemoteException {
		List<Video> videos = restoreVideos();
		DatenbankController dbController = new DatenbankController(
				serverLanguage);
		dbController.createDatabase();
		for (Video video : videos) {
			dbController.addVideo(video.getId(), video.getName(),
					video.getAmpel(), video.getGeraet(),
					video.getBeschreibung(), video.getSchwierigkeitsgrad(),
					video.getElementgruppe(), null, serverLanguage);
		}
	}

}
