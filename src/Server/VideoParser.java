package Server;




import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Mappt einen Datensatz aus der Datenbank auf ein Video Objekt.
 * @author michael
 *
 */
public class VideoParser
{
    private DatenbankController dbController;

    public VideoParser(String sprache)
    {
        dbController = new DatenbankController(sprache);
    }

    /**
     * Mappt alle Datensaetze aus der Datenbank, die mit name uebereinstimmen, auf Video-Objekte
     * @param name
     * @return Liste mit Videos
     */
    public ArrayList<Video> mappeVideosVonName(String name)
    {
        ResultSet ergebnis = dbController.getAllByName(name);
        return parseVideos(ergebnis);
    }
    
    public ArrayList<Video> mappeVideosVonSprache(String sprache){
    	ResultSet ergebnis = dbController.getAllEntries(sprache);
    	return parseVideos(ergebnis);
    }
    /**
     * Mappt alle Datensaetze aus der Datenbank, die mit i uebereinstimmen, auf Video-Objekte
     * @param i = Ampelfarbe (0=grün, 1=gelb, 2=rot)
     * @return Liste mit Videos
     */
    public ArrayList<Video> mappeVideosVonAmpel(int i, String sprache){
    	ResultSet ergebnis = dbController.getAllByAmpel(i, sprache);
    	return parseVideos(ergebnis);
    }
   
    /**
     * Mappt einen Datensatz auf ein Video-Objekt
     * @param id
     * @return Video
     */
    public Video mappeEinVideo(int id, String sprache)
    {
        Video video;
        ResultSet ergebnis = dbController.getEntry(id, sprache);
        ArrayList<Video> videoListe = parseVideos(ergebnis);
        if (videoListe.size() == 1)
        {
            video = videoListe.get(0);
        }
        else
        {
            video = null;
        }

        return video;
    }
    /**
     * Uebernimmt das Parsen eines ResultSets
     * @param ergebnis
     * @return Liste mit Videos
     */
    private ArrayList<Video> parseVideos(ResultSet ergebnis)
    {
        int id;
        String name;
        String pfad;
        String beschreibung;
        String geraet;
        String schwierigkeitsgrad;
        String elementgruppe;
        int ampel;
        Video video;
        ArrayList<Video> videos = new ArrayList<Video>();


        try
        {
            while (ergebnis.next())
            {
                id = ergebnis.getInt("id");
                name = ergebnis.getString("videoname");
                geraet = "";
                pfad = "";
                beschreibung = "";
                schwierigkeitsgrad = "";
                elementgruppe = "";
                ampel = ergebnis.getInt("ampel");

                video = new Video(id, name, pfad, geraet, beschreibung, schwierigkeitsgrad,
                        elementgruppe, ampel);
                videos.add(video);

            }
        }
        catch (SQLException e)
        {
            System.err.println("Fehler bei Datenbankabfrage!");
            e.printStackTrace();
        }
        return videos;
    }
   
}
