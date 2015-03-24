import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface  IServer extends Remote {
	public List<Video> findVideo(String name, boolean searchOnOtherServers)throws RemoteException;
	public void insertNewTranslation(int id, String neueBezeichnung, String sprache) throws RemoteException;
	public void insertNewVideo(int id, String name, int ampel, String geraet, String beschreibung,String schwierigkeitsgrad, String elementgruppe, File video,  String sprache) throws RemoteException;
	public void reserveId(int id) throws RemoteException;
	public String getServerLanguage() throws RemoteException;
	public List<Video> getAllVideosByLanguage(String language) throws RemoteException;
	public void restoreDatabase() throws RemoteException;

}
