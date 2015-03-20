/**
 * repräsentiert den Server für unser verteiltes Systeme Projekt
 * 
 * @author Thomas
 */
public class Server {

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

	/*
	 * ---------------------------------unwichtig ----------------------
	 */
	/**
	 * Der Server fordert ein Video von einem anderen Server an.
	 */
	private void requestVideo() {
		// zuerst einmal nicht wichtig
	}

	/**
	 * Der Server stellt seine Datenbank, mit Hilfe der anderen Servern, wieder
	 * her.
	 */
	private void restoreDatabase() {
		// zuerst einmal nicht wichtig
	}

	/**
	 * reserviert eine ID für ein neues Video und informiert alle anderen
	 * Server, dass diese ID belegt ist
	 */
	private void reserveID() {
		// muss abgeklärt werden
	}

	/**
	 * vergleicht die ID eines Videos mit der in der eigenen Datenbank
	 */
	private void compareID(int id) {
		// abklären
	}

	/**
	 * Der Server sendet eine Anfrage zu allen anderen Servern.
	 */
	private void sendRequestToAllOtherServers() {
		// zuerst einmal nicht wichtig
	}
}
