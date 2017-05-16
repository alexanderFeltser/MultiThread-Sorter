package multiThreadSorter;

public class QueuePair {
	private final QueueNo leftQueu;
	private final QueueNo rightQueue;
	private Integer leftNo;
	private Integer rightNo;

	private QueuePair(QueueNo leftQueu, QueueNo rightQueue) {
		this.leftQueu = leftQueu;
		this.rightQueue = rightQueue;
	}

	public final int getLeftNo() {
		return leftNo;
	}

	public final void setLeftNo(int leftNo) {
		this.leftNo = leftNo;
	}

	@Override
	public String toString() {
		return leftQueu + " " + leftNo + " <-> " + rightQueue + " " + rightNo;
	}

	public final int getRigthNo() {
		return rightNo;
	}

	public final void setRigthNo(int rigthNo) {
		this.rightNo = rigthNo;
	}

	@Override
	public int hashCode() {
		return leftQueu.hashCode() * 5 + rightQueue.hashCode() * 7;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o.getClass() != this.getClass()) {
			return false;
		}
		QueuePair obj = (QueuePair) o;
		return (this.leftQueu.equals(obj.leftQueu) && this.rightQueue.equals(obj.rightQueue));
	}

	public boolean havePair() {
		return (leftNo != null && rightNo != null);
	}

	public boolean needToSwitch() {
		return havePair() && leftNo >= rightNo;
	}

	public Message messageToLeftQueue() {
		int number = leftNo;
		leftNo = null;
		return new Message(number, leftQueu, rightQueue);
	}

	public Message messageToRightQueue() {
		int number = rightNo;
		rightNo = null;

		return new Message(number, rightQueue, leftQueu);
	}

	public void switchNumbers() {
		Integer temp;
		temp = getLeftNo();
		leftNo = getRigthNo();
		rightNo = temp;

	}

	public void putMessage(Message message) {
		if (message.from.equals(leftQueu)) {
			rightNo = message.number;
		} else if (message.from.equals(rightQueue)) {
			leftNo = message.number;
		} else {
			throw new IllegalArgumentException("Trying to add Message to Wrong QueuePar");
		}
	}

	public static QueuePair getQueueParFromMessage(Message message) {
		if (message.from.getX() - message.too.getX() == 1 && message.from.getY() - message.too.getY() == 0) {
			return new QueuePair(message.too, message.from);
		} else if (message.too.getX() - message.from.getX() == 1 && message.from.getY() - message.too.getY() == 0) {
			return new QueuePair(message.from, message.too);
		} else if (message.from.getY() - message.too.getY() == 1) {
			return new QueuePair(message.too, message.from);

		} else if (message.too.getY() - message.from.getY() == 1) {
			return new QueuePair(message.from, message.too);
		} else {
			throw new IllegalArgumentException("Trying to add Message to Wrong QueuePar");
		}
	}
}
