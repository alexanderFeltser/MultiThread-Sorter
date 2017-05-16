package multiThreadSorter;

public class QueueNo {
	final private int x;
	final private int y;

	public QueueNo(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public final int getX() {
		return x;
	}

	public final int getY() {
		return y;

	}

	@Override
	public int hashCode() {
		return x * 31 + y;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o.getClass() != this.getClass()) {
			return false;
		}

		QueueNo obj = (QueueNo) o;
		return (x == obj.getX() && y == obj.getY());
	}

	@Override
	public String toString() {
		return "QueueNo [ " + x + "," + y + "]";
	}

}
