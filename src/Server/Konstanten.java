package Server;
/**
 * Enthält alle wichtigen Konstanten
 * @author michael
 *
 */
public class Konstanten {
	/**
	 * Konstanten für die Sprachen
	 */
	public static final String LANGUAGE_GERMAN = "deutsch";
	public static final String LANGUAGE_FRENCH = "franzoesisch";
	public static final String LANGUAGE_ENGLISH = "englisch";
	/**
	 * Adressen zu den Servern
	 * 
	 */
	public static final String SERVER_DEUTSCHLAND = "rmi://de-server:1099/Server";
	public static final String SERVER_FRANKREICH = "rmi://fr-server:1099/Server";
	public static final String SERVER_ENGLAND = "";
	/**
	 * Platzhalter für reservierte Videos
	 */
	public static final String RESERVED_ID = "RESERVED";
	/**
	 * Farben der Ampel
	 */
	public static final int GREEN = 0;
	public static final int YELLOW = 1;
	public static final int RED = 2;
}
