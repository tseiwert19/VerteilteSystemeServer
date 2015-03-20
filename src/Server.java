/**
 * repr�sentiert den Server f�r unser verteiltes Systeme Projekt
 * 
 * @author Thomas
 */
public class Server {

	/*
	 * ---------------------------------wichtig ----------------------
	 */
	
	/**
	 * f�gt eine, vom Client vorgeschlagene, �bersetzung ein
	 */
	private void insertNewTranslation(String VideoName, String neueBezeichnung) {
		// sehr wichtig
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
	private void sendVideo(String videoName) {
		// sehr wichtig
	}
	
	/**
	 * f�gt ein neues Video eines anderen Servers oder Clients ein
	 */
	private void insertNewVideo(String videoName) {
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
	 * reserviert eine ID f�r ein neues Video und informiert alle anderen
	 * Server, dass diese ID belegt ist
	 */
	private void reserveID() {
		// muss abgekl�rt werden
	}

	/**
	 * vergleicht die ID eines Videos mit der in der eigenen Datenbank
	 */
	private void compareID(int id) {
		// abkl�ren
	}

	/**
	 * Der Server sendet eine Anfrage zu allen anderen Servern.
	 */
	private void sendRequestToAllOtherServers() {
		// zuerst einmal nicht wichtig
	}
}
