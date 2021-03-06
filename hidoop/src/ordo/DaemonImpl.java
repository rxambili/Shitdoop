package ordo;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import map.MapReduce;
import map.Mapper;
import map.Reducer;
import formats.Format;

/**
 * Classe DaemonImpl implemente l'interface Daemon.
 * Cet classe doit etre lancee sur chaque machine distante.
 * @author Bonnet, Steux, Xambili
 *
 */
public class DaemonImpl extends UnicastRemoteObject implements Daemon {

	private ReduceInputWriterThread reduceInput;
	
	/**
	 * Constructeur de DaemonImpl.
	 * @throws RemoteException
	 */
	protected DaemonImpl() throws RemoteException {
		super();
	}

	/**
	 * Ouvre/cree les fichiers puis execute le map localement avant de fermer les fichiers.
	 */
	public void runMap(Mapper m, Format reader, Format writer, CallBack cb)
			throws RemoteException {

		reader.open(Format.OpenMode.R);
		writer.open(Format.OpenMode.W);
		m.map(reader, writer);
		reader.close();
		writer.close();

		// dire au client que c'est fini
		cb.addMachineFinished();
	}

	public void runReduce(Reducer r, Format reader, Format writer, CallBack cb) {
		reader.open(Format.OpenMode.R);
		writer.open(Format.OpenMode.W);
		r.reduce(reader, writer);
		reader.close();
		writer.close();
	}

	public void initReduceInputWriter() {
		this.reduceInput.init();
	}

	public ReduceInputWriterThread getReduceInput() {
		return this.reduceInput;
	}

	public void setReduceInput(ReduceInputWriterThread r) {
		this.reduceInput = r;
	}


	/**
	 * Methode principale de la classe DaemonImpl.
	 * Elle cree le RMI registry sur la machine s'il n'existe pas deje 
	 * puis enregistre une instance de DaemonImpl au registry.
	 * @param args args[0] correspond au numero du Daemon.
	 */
	public static void main(String args[]) {
		try {
			Registry registry = LocateRegistry.createRegistry(4000);
			System.out.println("Creation du registry sur le port 4000");
		} catch (RemoteException e1) {
			System.out.println("registry deja existant");
		}
		try {
			
			// recuperer hostname
			//String hostName = null;
		    //try {
		    //  final InetAddress addr = InetAddress.getLocalHost();
		    //  hostName = new String(addr.getHostName());
		    //} catch(final Exception e) {
		    //}
			
			Daemon daemon = new DaemonImpl();
			((DaemonImpl) daemon).setReduceInput(new ReduceInputWriterThread(4444, "Daemon" + args[0] ));
			((DaemonImpl) daemon).getReduceInput().start();
			Naming.rebind("//localhost:4000/Daemon" + args[0], daemon);
			boolean isClosed = false;
			while (!isClosed) {
				Scanner sc = new Scanner(System.in);
				System.out.println("Saisir Commande :");
				String str = sc.nextLine();
				if (str.equals("exit")) {
					((DaemonImpl) daemon).getReduceInput().fermer();
					isClosed = true;
					sc.close();
					System.exit(0);
				} else {
					usage();
				}
			}
		} catch (RemoteException | MalformedURLException e) {
			e.printStackTrace();
		}
			
	}

	private static void usage() {
		System.out.println("usage : exit | ...");		
	}
}

