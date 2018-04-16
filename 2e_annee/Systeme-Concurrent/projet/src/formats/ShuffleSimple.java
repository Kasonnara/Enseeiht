package formats;

import java.io.Serializable;

import formats.ShuffleFunction;

public class ShuffleSimple implements ShuffleFunction , Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final  String alphabet="êàéabcdefghijklmnopqrstuvwxyz";

	@Override
	public int shuffle(String key,int maxOutput) {
		int longInterv=alphabet.length()/maxOutput;
		int numReduce=0;
		char firstChar=java.lang.Character.toLowerCase(key.charAt(0));
		int i=alphabet.indexOf(firstChar);
		for(int j=0;j<maxOutput;j++) {
			if(i<longInterv*(j+1)) {
				numReduce=j;
				break;
			}
		}
		if(i>longInterv*maxOutput) {
			numReduce=maxOutput-1;
		}
		return numReduce;
	}
	
	


}
