import java.rmi.Remote;
import java.rmi.RemoteException;

public interface  IServer extends Remote {
	public String insert(String neueBezeichnung) throws RemoteException ;
}
