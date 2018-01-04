package implementations.util;

public class XORShiftRandom {

private long last;

public XORShiftRandom() {
    this(System.currentTimeMillis());
}
public void newSeed() {
	this.last = System.currentTimeMillis();
}

public XORShiftRandom(long seed) {
    this.last = seed;
}

public int nextInt(int delta,int hash, int ringSize) {
	
	int f = (Math.abs(hash) % ringSize);
	if (delta == 0) return f;
	int max = (2*delta)+1;
	int min = f - delta;
    last ^= (last << 21);
    last ^= (last >>> 35);
    last ^= (last << 4);
    int out = (int) last % max;  
    int h = (out < 0) ? (min-out) : (min+out); 
    return (h < 0) ? (ringSize+h) : (h%ringSize);
}

public int[] HostList(int delta,int hash, int ringSize){
	int f = (Math.abs(hash) % ringSize);
	int max = (2*delta)+1;
	int min = f - delta;
	int[] result = new int[max];
	for(int i=0;i < max;i++){
		result[i] = ((min+i) < 0) ? (ringSize+min+i) : ((min+i)%ringSize);
	}
	return result;
}
}
