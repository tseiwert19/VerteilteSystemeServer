import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface  IServer extends Remote {
	public String insert(String neueBezeichnung) throws RemoteException ;
	public List<Video> findVideo(String name)throws RemoteException;
	public void insertNewTranslation(int id, String neueBezeichnung, String sprache) throws RemoteException;
	public void insertNewVideo(int id, String name, int ampel, String sprache) throws RemoteException;
}
