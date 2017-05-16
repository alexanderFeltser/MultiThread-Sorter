package multiThreadSorter;

import java.util.ArrayList;
import java.util.LinkedList;

public class MessageQueue {
	final private QueueNo queueNo;
	LinkedList<Message> sendedMessageQueue;
	ArrayList<QueueListener> queueListeners;

	private final Object lock = new Object();

	public MessageQueue(QueueNo queueNo) {

		this.queueNo = queueNo;
		sendedMessageQueue = new LinkedList<>();
		queueListeners = new ArrayList<>();
	}

	public void addNewQueueListener(QueueListener listener) {
		queueListeners.add(listener);
	}

	public final Object getLock() {
		return lock;
	}

	public void notifyListeners() {

		lock.notifyAll();

	}

	public void sendNumber2Sorter(Message mess) {
		synchronized (lock) {
			sendedMessageQueue.addLast(mess);
			notifyListeners();
		}
	}

	public Message removeFromSendedNumbersQueue() {
		synchronized (lock) {
			Message m = sendedMessageQueue.getFirst();
			sendedMessageQueue.removeFirst();
			return m;
		}
	}

	public void wateForMessage() {
		synchronized (lock) {
			try {
				if (!isEmpty()) {
					return;
				}

				lock.wait();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}

	}

	public final QueueNo getQueueNo() {
		return queueNo;
	}

	public boolean isEmpty() {
		boolean test = false;
		synchronized (lock) {
			test = (sendedMessageQueue.size() == 0);
			return (test);
		}
	}
}
