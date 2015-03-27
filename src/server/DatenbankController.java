package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * Diese Klasse enthaelt alle Funktionen fuer die Datenbank
 * 
 * @author michael
 *
 */
public class DatenbankController

{
	private Connection connection;
	private String serverLanguage;
	private static final String DB_PATH = "server.sqlite";
	private List<String> secondaryTablenames;

	final static Logger logger = Logger.getLogger(DatenbankController.class);

	/**
	 * Konstruktor Stellt die Verbindung zur Datenbank her und erstellt die
	 * Datenbank(Falls sie noch nicht existiert)
	 */
	public DatenbankController(String serverLanguage) {
		connectToDb();
		BasicConfigurator.configure();
		logger.info("DatenbankController: " + serverLanguage + " erstellt");
		this.serverLanguage = serverLanguage;
		secondaryTablenames = new ArrayList<String>();
		secondaryTablenames.add(Konstanten.LANGUAGE_ENGLISH);
		secondaryTablenames.add(Konstanten.LANGUAGE_GERMAN);
		secondaryTablenames.add(Konstanten.LANGUAGE_FRENCH);
		secondaryTablenames.remove(serverLanguage);
	}

	/**
	 * Stellt eine Verbindung zur Datenbank her
	 */
	private void connectToDb() {
		logger.info("DatenbankController: " + serverLanguage + " connectToDb()");
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
		} catch (ClassNotFoundException e) {
			logger.error("DatenbankController: " + serverLanguage
					+ " Fehler beim Laden des JDBC-Treibers");
			logger.error(e.getMessage());
		} catch (SQLException e) {
			logger.error("DatenbankController: " + serverLanguage
					+ " Fehler bei Verbindung zur Datenbank!");
			logger.error(e.getMessage());
		}

	}

	public void createDatabase() {
		logger.info("DatenbankController: " + serverLanguage
				+ " createDatabase()");
		createTable(Konstanten.LANGUAGE_GERMAN);
		createTable(Konstanten.LANGUAGE_FRENCH);
		createTable(Konstanten.LANGUAGE_ENGLISH);
	}

	private void createTable(String language) {
		logger.info("DatenbankController: " + serverLanguage + " createTable("
				+ language + ")");
		connectToDb();
		try {
			Statement statement = connection.createStatement();
			statement
					.executeUpdate("CREATE TABLE IF NOT EXISTS "
							+ language
							+ "(id INT PRIMARY KEY NOT NULL, videoname VARCHAR(50),  ampel INT NOT NULL, geraet VARCHAR, beschreibung VARCHAR, schwierigkeitsgrad VARCHAR, elementgruppe VARCHAR, video BLOB);  ");
			statement.close();
		} catch (SQLException e) {
			logger.error("DatenbankController: " + serverLanguage
					+ " Fehler beim Erstellen der Datenbank!");
			logger.error(e.getMessage());
		}
	}

	/**
	 * Fuegt ein Video in die Datenbank
	 * 
	 * @id -1 für AUTOINCREMENT !
	 * 
	 * @param video
	 *            Video, das eingefügt wird
	 * @param sprache
	 *            bestimmt in welche Tabelle eingefügt wird
	 * @return rowId
	 */
	public int addVideo(int id, String name, int ampel, String geraet,
			String beschreibung, String schwierigkeitsgrad,
			String elementgruppe, byte[] video, String sprache) {
		logger.info("DatenbankController: " + serverLanguage + " addVideo()");
		connectToDb();
		try {
			connection.setAutoCommit(false);
			String sql = "INSERT INTO "
					+ sprache
					+ " (id, videoname, ampel, geraet, beschreibung, schwierigkeitsgrad, elementgruppe, video) "
					+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, ? );";
			PreparedStatement stmt = connection.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);

			if (id >= 0) {
				stmt.setInt(1, id);
			}
			stmt.setString(2, name);
			stmt.setInt(3, ampel);
			stmt.setString(4, geraet);
			stmt.setString(5, beschreibung);
			stmt.setString(6, schwierigkeitsgrad);
			stmt.setString(7, elementgruppe);
			stmt.setBytes(8, video);

			stmt.executeUpdate();
			ResultSet generatedKeys = stmt.getGeneratedKeys();
			generatedKeys.next();

			id = (int) generatedKeys.getLong(1);
			logger.info("DatenbankController: " + serverLanguage
					+ " addVideo() Id: " + id);
			stmt.close();
			connection.commit();

			connection.close();
		} catch (SQLException e) {
			logger.error("DatenbankController: " + serverLanguage
					+ " Fehler beim Einfügen in die Datenbank!");
			logger.error(e.getMessage());
		}
		return id;
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
		logger.info("DatenbankController: " + serverLanguage
				+ " getAllEntries(Sprache: " + sprache + ")");
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
		logger.info("DatenbankController: " + serverLanguage
				+ " getAllByAmpel(" + i + ", " + sprache + ")");
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
		logger.info("DatenbankController: " + serverLanguage
				+ " getAllByName( " + name + " )");
		ResultSet results = findDatasets("SELECT * FROM " + serverLanguage
				+ " WHERE videoname LIKE '%" + name + "%'");
		String lastLanguage = serverLanguage;

		for (String language : secondaryTablenames) {
			try {
				if (!results.next()) {
					results.close();
					results = findDatasets("SELECT * FROM " + language
							+ " WHERE videoname LIKE '%" + name + "%'");
				} else {
					results.close();
					return results = findDatasets("SELECT * FROM "
							+ lastLanguage + " WHERE videoname LIKE '%" + name
							+ "%'");
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
			lastLanguage = language;
		}
		// try {
		// if (!results.next()) {
		// results.close();
		// results = findDatasets("SELECT * FROM " + Konstanten.LANGUAGE_FRENCH
		// + " WHERE videoname LIKE '%" + name + "'");
		// if (!results.next()) {
		// results.close();
		// results = findDatasets("SELECT * FROM " + Konstanten.LANGUAGE_ENGLISH
		// + " WHERE videoname LIKE '%" + name + "'");
		// }
		// } else {
		// results.close();
		// results = findDatasets("SELECT * FROM " + serverLanguage
		// + " WHERE videoname LIKE '%" + name + "%'");
		// }
		//
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
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
		logger.info("DatenbankController: " + serverLanguage
				+ " findDatasets( " + sql + " )");
		ResultSet alleVideos = null;
		connectToDb();
		try {
			alleVideos = connection.createStatement().executeQuery(sql);
		} catch (SQLException e) {
			logger.error("DatenbankController: " + serverLanguage
					+ " Fehler bei Datenbankabfrage!");
			logger.error(e.getMessage());
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
		logger.info("DatenbankController: " + serverLanguage + " getEntry( "
				+ primaryKey + ", " + sprache + " )");
		return findDatasets("SELECT * FROM " + sprache + " WHERE id = '"
				+ primaryKey + "'");
	}

	/**
	 * Aktualisiert einen Datensatz (AKTUALISIERT NUR NAME + AMPEL!!!!)
	 * 
	 */
	public void updateTranslation(int id, String newName, int ampel,
			String sprache) {
		logger.info("DatenbankController: " + serverLanguage
				+ " updateTranslation( " + id + ", " + newName + ", " + ampel
				+ ", " + sprache + ")");
		connectToDb();

		Statement stmt = null;
		try {

			connection.setAutoCommit(false);

			stmt = connection.createStatement();
			String sql = "UPDATE " + sprache + " SET videoname = '" + newName
					+ "', ampel = '" + ampel + "' WHERE id = '" + id + "';";
			stmt.executeUpdate(sql);
			connection.commit();

			stmt.close();
			connection.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void updateVideo(int id, String name, int ampel, String geraet,
			String beschreibung, String schwierigkeitsgrad,
			String elementgruppe, byte[] video, String sprache) {
		logger.info("DatenbankController: " + serverLanguage + " updateVideo( "
				+ id + ", " + name + ", " + ampel + ", " + sprache + ")");
		connectToDb();

		try {

			connection.setAutoCommit(false);
			PreparedStatement stmt = null;

			stmt = connection
					.prepareStatement("UPDATE "
							+ sprache
							+ " SET videoname = ? ,ampel = ?, geraet = ?, beschreibung = ?, schwierigkeitsgrad = ?, elementgruppe = ?, video = ? WHERE id = '"
							+ id + "';");
			stmt.setString(1, name);
			stmt.setInt(2, ampel);
			stmt.setString(3, geraet);
			stmt.setString(4, beschreibung);
			stmt.setString(5, schwierigkeitsgrad);
			stmt.setString(6, elementgruppe);
			stmt.setBytes(7, video);
			stmt.executeUpdate();

			connection.commit();

			stmt.close();
			connection.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

}
