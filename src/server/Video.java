package server;

import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Klasse Video Repraesentiert einen Datensatz aus der Datenbank. Enthaelt alle
 * wichtigen Daten zu einem Video.
 * 
 * @author michael
 *
 */
public class Video implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9198271636225073247L;
	private int id;
	private String name;
	private String pfad;
	private String geraet;
	private String beschreibung;
	private String schwierigkeitsgrad;
	private String elementgruppe;
	private byte[] videoDatei;
	private int ampel;

	private static final String videoLocationPrefix = "/";

	public Video(int id, String name, String pfad, String geraet,
			String beschreibung, String schwierigkeitsgrad, String elementgruppe) {
		// System.err.println("Video [id=" + id + ", name=" + name + ", pfad=" +
		// pfad + ", beschreibung=" + beschreibung + ", schwierigkeitsgrad=" +
		// schwierigkeitsgrad + ", elementgruppe=" + elementgruppe + "]");
		this.id = id;
		this.name = name;
		
		this.pfad = pfad;
		this.geraet = geraet;
		this.beschreibung = beschreibung;
		this.schwierigkeitsgrad = schwierigkeitsgrad;
		this.elementgruppe = elementgruppe;
		this.ampel = 0;
		this.videoDatei = null;
	}

	public Video(int id, String name, String pfad, String geraet,
			String beschreibung, String schwierigkeitsgrad,
			String elementgruppe, int ampel) {
		this(id, name, pfad, geraet, beschreibung, schwierigkeitsgrad,
				elementgruppe);
		this.ampel = ampel;
	}

	public Video(int id, String name, String pfad, String geraet,
			String beschreibung, String schwierigkeitsgrad,
			String elementgruppe, int ampel, byte[] videoDatei) {
		this(id, name, pfad, geraet, beschreibung, schwierigkeitsgrad,
				elementgruppe, ampel);
		this.videoDatei = videoDatei;
	}

	public int getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getVideoDatei() {
		return videoDatei;
	}

	public void setVideoDatei(byte[] videoDatei) {
		this.videoDatei = videoDatei;
	}

	public int getAmpel() {
		return ampel;
	}

	public void setAmpel(int ampel) {
		this.ampel = ampel;
	}

	public String getName() {
		return name;
	}

	public String getPfad() {
		return pfad;
	}

	public String getGeraet() {
		return geraet;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public String getSchwierigkeitsgrad() {
		return schwierigkeitsgrad;
	}

	public String getElementgruppe() {
		return elementgruppe;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((beschreibung == null) ? 0 : beschreibung.hashCode());
		result = prime * result
				+ ((elementgruppe == null) ? 0 : elementgruppe.hashCode());
		result = prime * result + ((geraet == null) ? 0 : geraet.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pfad == null) ? 0 : pfad.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Video other = (Video) obj;
		if (beschreibung == null) {
			if (other.beschreibung != null)
				return false;
		} else if (!beschreibung.equals(other.beschreibung))
			return false;
		if (elementgruppe == null) {
			if (other.elementgruppe != null)
				return false;
		} else if (!elementgruppe.equals(other.elementgruppe))
			return false;
		if (geraet == null) {
			if (other.geraet != null)
				return false;
		} else if (!geraet.equals(other.geraet))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pfad == null) {
			if (other.pfad != null)
				return false;
		} else if (!pfad.equals(other.pfad))
			return false;
		if (schwierigkeitsgrad != other.schwierigkeitsgrad)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Video [id=" + id + ", name=" + name + ", pfad=" + pfad
				+ ", beschreibung=" + beschreibung + ", schwierigkeitsgrad="
				+ schwierigkeitsgrad + ", elementgruppe=" + elementgruppe + "]";
	}

	private String detectAbsolutePath(String pfad) {
		if (pfad == null || pfad.isEmpty()) {
			System.err.println("Video: No path for video in database!");
			return null;
		}
		pfad = videoLocationPrefix + pfad;
		pfad = pfad.replaceFirst("[.]wmv$", ".mkv");
		URL urlOfVideoFile = getClass().getResource(pfad);
		if (urlOfVideoFile == null) {
			System.err.println("Video: Video " + pfad + " not found!");
			return null;
		}
		URI uriOfVideoFile;
		try {
			uriOfVideoFile = urlOfVideoFile.toURI();
		} catch (Exception e) {
			System.err.println("Video: URL of video " + pfad
					+ " couldn't be converted to URI!");
			return null;
		}
		File videoPathFileObject;
		try {
			videoPathFileObject = new File(uriOfVideoFile);
		} catch (Exception e) {
			System.err.println("Video: URI of video " + pfad
					+ " couldn't be converted to File!");
			return null;
		}

		// String pfadFertig = videoPathFileObject.getPath();
		String pfadFertig = "file://" + urlOfVideoFile.getPath();
		pfadFertig = pfadFertig.replace("bin", "src");
		return pfadFertig;
	}

	public static byte[] convertVideoToBytes(String path) {
		ByteArrayOutputStream bos = null;
		File videoFile = new File(path);
		int fileLength = (int) videoFile.length();
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(videoFile);
			bos = new ByteArrayOutputStream();
			byte[] buf = new byte[fileLength];

			for (int readNum; (readNum = fis.read(buf)) != -1;) {
				bos.write(buf, 0, readNum);
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

		return bos.toByteArray();
	}
	
	
	public void createVideoFile(){
		InputStream input = new ByteArrayInputStream(videoDatei);
		OutputStream output = null;
		try {
			output =  new FileOutputStream("src/videos/"+ name);
			pfad = "src/videos/"+ name;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte data[] = new byte[4096];
		int count;
		try {
			while ((count = input.read(data)) != -1) {
				output.write(data, 0, count);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
