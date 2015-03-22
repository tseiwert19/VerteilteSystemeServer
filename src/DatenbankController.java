import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Diese Klasse enthaelt alle Funktionen fuer die Datenbank
 * 
 * @author michael
 *
 */
public class DatenbankController

{
	public static final String GERMAN = "deutsch";
	public static final String ENGLISH = "englisch";
	public static final String FRENCH = "franzoesisch";
	public static final int GREEN = 0;
	public static final int YELLOW = 0;
	public static final int RED = 0;

	private Connection connection;
	private static final String DB_PATH = "server.sqlite";

	/**
	 * Konstruktor Stellt die Verbindung zur Datenbank her und erstellt die
	 * Datenbank(Falls sie noch nicht existiert)
	 */
	public DatenbankController() {
		connectToDb();
	}

	/**
	 * Stellt eine Verbindung zur Datenbank her
	 */
	private void connectToDb() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
		} catch (ClassNotFoundException e) {
			System.err.println("Fehler beim Laden des JDBC-Treibers");
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("Fehler bei Verbindung zur Datenbank!");
			e.printStackTrace();
		}

	}

	public void createDatabase() {
		createTable(GERMAN);
		createTable(FRENCH);
		createTable(ENGLISH);
	}

	private void createTable(String language) {
		connectToDb();
		try {
			Statement statement = connection.createStatement();
			statement
					.executeUpdate("CREATE TABLE IF NOT EXISTS "
							+ language
							+ "(id INT PRIMARY KEY NOT NULL, videoname VARCHAR(50),  ampel INT NOT NULL, geraet VARCHAR, beschreibung VARCHAR, schwierigkeitsgrad VARCHAR, elementgruppe VARCHAR, video BLOB);  ");
			statement.close();
		} catch (SQLException e) {
			System.err.println("Fehler beim Erstellen der Datenbank!");
			e.printStackTrace();
		}
	}

	/**
	 * Fuegt ein Video in die Datenbank
	 * 
	 * @param video
	 *            Video, das eingefügt wird
	 * @param sprache
	 *            bestimmt in welche Tabelle eingefügt wird
	 */
	public void addVideo(int id, String name, int ampel, String geraet,
			String beschreibung, String schwierigkeitsgrad,
			String elementgruppe, File video, String sprache) {

		connectToDb();
		try {
			connection.setAutoCommit(false);

			Statement stmt = connection.createStatement();
			// VIDEO NOCH NICHT BEACHTET!!!!
			String sql = "INSERT INTO "
					+ sprache
					+ " (id, videoname, ampel, geraet, beschreibung, schwierigkeitsgrad, elementgruppe) "
					+ "VALUES (" + id + " , '" + name + "', " + ampel + ", '"
					+ geraet + "','" + beschreibung + "', '"
					+ schwierigkeitsgrad + "', '" + elementgruppe + "' );";

			stmt.executeUpdate(sql);

			stmt.close();
			connection.commit();

			connection.close();
		} catch (SQLException e) {
			System.err.println("Fehler beim Einfügen in die Datenbank!");
			e.printStackTrace();
		}
	}

	/**
	 * Liefert alle Eintraege aus der Datenbank
	 * 
	 * @param Sprache
	 *            bestimmt aus welcher Tabelle die Einträge genommen werden
	 * @return Alle Eintraege
	 * @throws SQLException
	 */
	public ResultSet getAllEntries(String sprache) {
		return findDatasets("SELECT * FROM " + sprache + ";");
	}

	/**
	 * Liefert alle Eintraege einer Ampelfarbe
	 * 
	 * @param i
	 *            0=grün, 1 = gelb, 2 = rot
	 * @return Videos
	 */
	public ResultSet getAllByAmpel(int i, String sprache) {
		return findDatasets("SELECT * FROM" + sprache + " WHERE ampel = " + i);
	}

	/**
	 * Sucht alle Videos die den uebergebenen Teilstring enthalten
	 * 
	 * @param name
	 *            Nach diesem Teilstring wird gesucht
	 * @return Liste der gefunden Videos
	 */
	public ResultSet getAllByName(String name) {
		System.out.println("GetAllByName()-Aufruf");
		ResultSet results = findDatasets("SELECT * FROM " + GERMAN
				+ " WHERE videoname LIKE '%" + name + "%'");
		try {
			if (!results.next()) {
				results.close();
				System.out.println("FRENCH ZWEIG");
				results = findDatasets("SELECT * FROM " + FRENCH
						+ " WHERE videoname LIKE '%" + name + "'");
				if (!results.next()) {
					System.out.println("ENGLISH ZWEIG");
					results.close();
					results = findDatasets("SELECT * FROM " + ENGLISH
							+ " WHERE videoname LIKE '%" + name + "'");
				}
			} else {
				results.close();
				results = findDatasets("SELECT * FROM " + GERMAN
						+ " WHERE videoname LIKE '%" + name + "%'");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * Liefert alle Eintraege aus der Datenbank, die dem SQL-Statement
	 * entsprechen
	 * 
	 * @param sql
	 * @return
	 */
	private ResultSet findDatasets(String sql) {
		ResultSet alleVideos = null;
		connectToDb();
		try {
			alleVideos = connection.createStatement().executeQuery(sql);
		} catch (SQLException e) {
			System.err.println("Fehler bei Datenbankabfrage!");
			e.printStackTrace();
		}
		return alleVideos;
	}

	// Muss noch angepasst werden, falls es benötigt wird!!!!!!!!!!!!!!!!
	// /**
	// * Loescht ein Video aus der Datenbank
	// *
	// * @param id
	// * Video mit dieser Id wird geloescht
	// */
	// public void deleteVideo(int id) {
	// PreparedStatement statement = null;
	// try {
	// connectToDb();
	// statement = connection
	// .prepareStatement("DELETE FROM videos WHERE id = " + id);
	// statement.executeUpdate();
	// statement.close();
	// } catch (SQLException e) {
	// System.err.println("Fehler beim Loeschen!");
	// e.printStackTrace();
	// }
	// }

	/**
	 * Liefert den Eintrag mit dem gesuchten PrimaryKey
	 * 
	 * @param primaryKey
	 *            Pimaerschluessel des Eintrags
	 * @param sprache
	 *            Bestimmt Tabelle in der gesucht wird
	 * @return ResultSet mit id=PrimaryKey
	 * @throws SQLException
	 */
	public ResultSet getEntry(int primaryKey, String sprache) {
		return findDatasets("SELECT * FROM " + sprache + " WHERE id = '"
				+ primaryKey + "'");
	}

	/**
	 * Aktualisiert einen Datensatz (AKTUALISIERT NUR NAME + AMPEL!!!!)
	 * 
	 */
	public void updateTranslation(int id, String newName, String sprache) {
		connectToDb();

		Statement stmt = null;
		try {

			connection.setAutoCommit(false);

			stmt = connection.createStatement();
			String sql = "UPDATE " + sprache + " SET videoname = '" + newName
					+ "', ampel = '" + YELLOW + "' WHERE id = '" + id + "';";
			stmt.executeUpdate(sql);
			connection.commit();

			stmt.close();
			connection.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Operation done successfully");

	}

}
