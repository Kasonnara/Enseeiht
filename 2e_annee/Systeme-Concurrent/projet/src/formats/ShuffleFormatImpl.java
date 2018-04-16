package formats;

import java.io.Serializable;
import java.util.List;

public class ShuffleFormatImpl implements ShuffleFormat,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int numMap;
	List<Format> listRF;
	ShuffleFunction shFunction;
	private int courantF;
	private OpenMode mode;
	
	
	
	
	public ShuffleFormatImpl(List<Format> listRF, ShuffleFunction shFunction) {
		super();
//		this.numMap=numMap;
		this.listRF = listRF;
		this.courantF=0;
		this.shFunction = shFunction;
	}

	@Override
	public void open(OpenMode mode) {
		this.mode=mode;
		this.courantF=0;
		for(int i=0;i<listRF.size();i++) {
			listRF.get(i).open(mode);
		}
	}

	@Override
	public void close() {
	
		for(int i=0;i<listRF.size();i++) {
			listRF.get(i).close();
		}
	}

	

	@Override
	public long getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getFname() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFname(String fname) {
		// TODO Auto-generated method stub
	}

	@Override
	public KV read() {
		KV res=null;
		if(mode==Format.OpenMode.R) {
			while((res=listRF.get(courantF).read())==null && courantF!=listRF.size()-1 ) {
				this.courantF++;
			}
		}
		return res;
	}

	@Override
	public void write(KV record) {
		int i=shFunction.shuffle(record.k, listRF.size());
		listRF.get(i).write(record);
	}



}
