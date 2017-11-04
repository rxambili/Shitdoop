package formats;

import java.io.Serializable;
import java.io.File;

public class FormatImpl implements Format {

	private Format.Type type;
  	private Format.OpenMode openMode;
  	private long index;
  	private String fname;

  	public FormatImpl (Format.Type type, Format.OpenMode openMode, long index, String fname) {
    		this.type = type;
    		this.openMode = openMode;
    		this.index = index;
    		this.fname = fname;
  	}

 	public void open(OpenMode mode) {
		File fichier = new File(fname);
		fichier.setExecutable(false);
		if (mode == R) {
			// mode lecture
			fichier.setReadable(true);
			fichier.setWritable(false);
		} else {
			// mode ecriture
			fichier.setReadable(false);
			fichier.setWritable(true);
		}
  	}
	
	public KV read() {
		KV kv = new KV();
		kv.k = ""; // "getLine"
		kv.v = ""; // "getNumberOfLine"
		return kv;
	}
	
	public void write(KV record) {
		// A FAIRE
		
	}
	
	public void close() {
  		// A FAIRE
		
  	}
  
	public long getIndex() {
    		return this.index;
  	}
  
	public String getFname() {
   		return this.fname;
 	}
  
  	public void setFname(String fname) {
    		this.fname = fname;
  	}
}
