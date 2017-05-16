package multiThreadSorter;

public class Message {
	final Integer number;
	final QueueNo from;
	final QueueNo too;

	public Message(Integer number, QueueNo too, QueueNo from) {
		this.number = number;
		this.from = from;
		this.too = too;
	}

	public final Integer getNumber() {
		return number;
	}

	public final QueueNo getFrom() {
		return from;
	}

	public final QueueNo getToo() {
		return too;
	}

	public static Message getEndMessageFrom(QueueNo queue) {
		return new Message(-2, new QueueNo(-1, -1), queue);
	}

	public static Message getStartMessageFrom(QueueNo queue) {
		return new Message(-1, new QueueNo(-1, -1), queue);
	}

	public static Message getQueueSortEndedMessageFrom(QueueNo queNo) {
		return new Message(-3, new QueueNo(-1, -1), queNo);
	}

	public boolean isControllMessage() {
		return (number < 0);

	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o.getClass() != this.getClass()) {
			return false;
		}
		Message obj = (Message) o;
		return (this.from.equals(obj.from) && this.too.equals(obj.too) && this.number == obj.number);
	}
}
