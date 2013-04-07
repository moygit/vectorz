package mikera.arrayz;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import mikera.vectorz.AVector;
import mikera.vectorz.IOp;
import mikera.vectorz.Op;
import mikera.vectorz.Tools;
import mikera.vectorz.impl.Vector0;

public class SliceArray<T extends INDArray> implements INDArray {
	private final int[] shape;
	private final T[] slices;
	
	private SliceArray(int[] shape, T[] slices) {
		this.shape=shape;
		this.slices=slices;
	}
	
	public static <T extends INDArray>  SliceArray<T> create(T... slices) {
		return new SliceArray<T>(Tools.consArray(slices.length,slices[0].getShape()),slices.clone());
	}
	
	public static <T extends INDArray>  SliceArray<T> create(List<T> slices) {
		int slen=slices.size();
		T[] arr=(T[]) Array.newInstance(slices.get(0).getClass(),slen);
		return new SliceArray<T>(Tools.consArray(slen,slices.get(0).getShape()),slices.toArray(arr));
	}
	
	@Override
	public int dimensionality() {
		return shape.length;
	}

	@Override
	public int[] getShape() {
		return shape;
	}

	@Override
	public double get(int... indexes) {
		int d=dimensionality();
		switch (d) {
			case 1: return slices[indexes[0]].get();
			case 2: return slices[indexes[0]].get(indexes[1]);
			case 3: return slices[indexes[0]].get(indexes[1],indexes[2]);
			default: return slices[indexes[0]].get(Arrays.copyOfRange(shape,1,d));
		}
	}

	@Override
	public AVector asVector() {
		AVector v=Vector0.INSTANCE;
		for (INDArray a:slices) {
			v=v.join(a.asVector());
		}
		return v;	
	}

	@Override
	public INDArray reshape(int... dimensions) {
		throw new UnsupportedOperationException();
	}

	@Override
	public INDArray slice(int majorSlice) {
		return slices[majorSlice];
	}

	@Override
	public long elementCount() {
		long c=1;
		for (int d:shape) {
			c*=d;
		}
		return c;
	}

	@Override
	public boolean isMutable() {
		for (INDArray a:slices) {
			if (a.isMutable()) return true;
		}
		return false;
	}

	@Override
	public boolean isFullyMutable() {
		for (INDArray a:slices) {
			if (!a.isFullyMutable()) return false;
		}
		return true;
	}

	@Override
	public boolean isElementConstrained() {
		for (INDArray a:slices) {
			if (a.isElementConstrained()) return true;
		}
		return false;
	}

	@Override
	public boolean isView() {
		return true;
	}

	@Override
	public void applyOp(Op op) {
		for (INDArray a:slices) {
			a.applyOp(op);
		}
	}

	@Override
	public void applyOp(IOp op) {
		for (INDArray a:slices) {
			a.applyOp(op);
		}
	}

	@Override
	public boolean equals(INDArray a) {
		if (!Arrays.equals(a.getShape(), this.getShape())) return false;
		for (int i=0; i<slices.length; i++) {
			if (!slices[i].equals(a.slice(i))) return false;
		}
		return true;
	}

	@Override
	public INDArray clone() {
		return exactClone();
	}

	@Override
	public SliceArray<T> exactClone() {
		T[] newSlices=slices.clone();
		for (int i=0; i<slices.length; i++) {
			newSlices[i]=(T) newSlices[i].exactClone();
		}
		return new SliceArray<T>(shape,newSlices);
	}

	public boolean equals(Object o) {
		if (!(o instanceof INDArray)) return false;
		return equals((INDArray)o);
	}

	@Override
	public int hashCode() {
		return asVector().hashCode();
	}

}
