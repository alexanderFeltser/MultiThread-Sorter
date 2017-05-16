package multiThreadSorter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Controller implements QueueListener, Runnable {
	final int MAX_X;
	final int MAX_Y;
	MessageQueue postBoxIn;
	final private HashMap<QueueNo, MessageQueue> sortersPostBoxes;

	final private HashMap<QueuePair, QueuePair> sendedNumbersPairs;
	Thread thread;
	HashSet<QueuePair> sortEndedPairsMmap;// contains number of Sorters that
											// possibly ended sorting

	public Controller(int mAX_X, int mAX_Y) {
		MAX_X = mAX_X;
		MAX_Y = mAX_Y;
		postBoxIn = new MessageQueue(new QueueNo(-1, -1));
		postBoxIn.addNewQueueListener(this);
		sortersPostBoxes = new HashMap<>();
		sortEndedPairsMmap = new HashSet<>();
		sendedNumbersPairs = new HashMap<>();
	}

	public boolean haveSmallerSibling(QueueNo queueNo) {

		return (queueNo.getX() > 1 || queueNo.getY() > 1);
	}

	public boolean haveRightSibling(QueueNo queueNo) {
		return (queueNo.getX() < MAX_X || queueNo.getY() < MAX_Y);
	}

	public QueueNo getSmallerSiblingNo(QueueNo queueNo) {
		if (queueNo.getX() > 1) {
			return new QueueNo(queueNo.getX() - 1, queueNo.getY());
		} else if (queueNo.getY() > 1) {
			return new QueueNo(MAX_X, queueNo.getY() - 1);
		} else {
			throw new IndexOutOfBoundsException(
					"Trying to get left sibling of (" + queueNo.getX() + "," + queueNo.getX() + ")");
		}
	}

	public QueueNo getGraterSiblingNo(QueueNo queueNo) {
		if (queueNo.getX() < MAX_X) {
			return new QueueNo(queueNo.getX() + 1, queueNo.getY());
		} else if (queueNo.getY() < MAX_Y) {
			return new QueueNo(1, queueNo.getY() + 1);
		} else {
			throw new IndexOutOfBoundsException(
					"Trying to get right sibling of (" + queueNo.getX() + "," + queueNo.getX() + ")");
		}
	}

	public boolean isSortStopped() {
		return postBoxIn.isEmpty();
	}

	void addSorterThreadMessageBox(QueueNo queueNo, MessageQueue queuePostBoxIn) {
		sortersPostBoxes.put(queueNo, queuePostBoxIn);
	}

	public final Thread getThread() {
		if (thread == null) {
			thread = new Thread(this);
		}
		return thread;
	}

	private boolean isSortEnded() {
		if (!postBoxIn.isEmpty()) {
			return false;
		}
		if (sortEndedPairsMmap.size() != MAX_X * MAX_Y - 1) {

			return false;
		}

		return true;
	}

	private void sendToAllThread(Message endMessege) {

		Collection<MessageQueue> allQueues = sortersPostBoxes.values();
		for (MessageQueue queue : allQueues) {
			queue.sendNumber2Sorter(endMessege);
		}

	}

	@Override
	public void run() {

		boolean isSortEnded = false;
		int cnt = 0;
		while (true) {
			if (postBoxIn.isEmpty()) {
				if (isSortEnded) {
					System.out.println("Controller sends an end message...");
					sendToAllThread(Message.getEndMessageFrom(postBoxIn.getQueueNo()));
					break;
				}

				postBoxIn.wateForMessage();
			}
			if (cnt > 700) {
				// printPairs(sendedNumbersPairs);
				cnt++;
			}
			cnt++;
			writeNumbersFromSorters();
			isSortEnded = isSortEnded();
			sendNumbersTosorters();

		}
		// sendToAllThread(Message.getEndMessage());
		System.out.println("Controller exits... ");
	}

	private void printPairs(HashMap<QueuePair, QueuePair> sendedNumbersPairs2) {
		Collection<QueuePair> qPairCollection = sendedNumbersPairs2.values();
		if (qPairCollection.size() > 0) {
			System.out.println("Pairs:");
		}
		for (QueuePair qPair : qPairCollection) {
			System.out.println(qPair);
		}

	}

	private void sendNumbersTosorters() {
		Message message;
		QueuePair qPairValue;
		ArrayList<QueuePair> removePairArray = new ArrayList<>();
		Set<QueuePair> qPairSet = sendedNumbersPairs.keySet();
		for (QueuePair qPair : qPairSet) {
			if (sendedNumbersPairs.get(qPair).havePair()) {
				qPairValue = sendedNumbersPairs.get(qPair);
				removePairArray.add(qPair);
				if (qPairValue.needToSwitch()) {
					sortEndedPairsMmap.add(qPair);
					qPairValue.switchNumbers();
				} else {
					sortEndedPairsMmap.clear();// contains number of Sorters
												// that
					// possibly ended sorting
				}

				message = qPairValue.messageToLeftQueue();
				sortersPostBoxes.get(message.getToo()).sendNumber2Sorter(message);
				message = qPairValue.messageToRightQueue();
				sortersPostBoxes.get(message.getToo()).sendNumber2Sorter(message);
			}

		}
		for (QueuePair qPaiRmv : removePairArray) {
			sendedNumbersPairs.remove(qPaiRmv);
		}
	}

	private void writeNumbersFromSorters() {
		Message message;
		QueuePair qPairValue;
		QueuePair qPair;

		while (!postBoxIn.isEmpty()) { // || !allPairsRedyToProcess() ||
										// sendedNumbersPairs.size() < MAX_X *
										// MAX_Y - 1)
			message = getMessageFromQueue();
			// System.out.println("writeNumbersFromSorters");
			// sortersPostBoxes.get(message.getToo()).sendNumber2Sorter(message);
			qPair = QueuePair.getQueueParFromMessage(message);
			if (sendedNumbersPairs.containsKey(qPair)) {
				qPairValue = sendedNumbersPairs.remove(qPair);
			} else {
				qPairValue = qPair;
			}
			qPairValue.putMessage(message);
			sendedNumbersPairs.put(qPair, qPairValue);
		}
	}

	@Override
	public Message getMessageFromQueue() {
		return postBoxIn.removeFromSendedNumbersQueue();
	}

	private MessageQueue getControllerPostBox() {

		return postBoxIn;
	}

	public static void main(String[] args) throws InterruptedException {
		int x = 20;
		int y = 10;
		int numbersInThread = 20;
		SorterThread sortThread;
		Controller controller = new Controller(x, y);
		QueueNo queueNo;

		List<SorterThread> queueList = new ArrayList<SorterThread>();
		System.out.format("Before Sort Matrix (%d,%d):", x, y);
		System.out.println();

		for (int j = 1; j <= y; j++) {
			for (int i = 1; i <= x; i++) {
				queueNo = new QueueNo(i, j);

				sortThread = new SorterThread(numbersInThread, controller.getControllerPostBox(), queueNo, controller);
				queueList.add(sortThread);
				sortThread.fillNumbers();
				controller.addSorterThreadMessageBox(queueNo, sortThread.getPostBoxIn());
				sortThread.getThread().start();
			}
		}

		controller.getThread().start();

		Thread.sleep(10000);
		System.out.println("==============================================================================");
		System.out.println("Print Result:");
		System.out.println("==============================================================================");
		for (SorterThread q : queueList) {
			q.printNumbers("Result:");
		}
	}

}
