package multiThreadSorter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

public class SorterThread implements Runnable, QueueListener {
	final MessageQueue postBoxIn;
	ArrayList<Integer> numbers;
	final int MAX_LENGTH;
	final MessageQueue postBoxOut;
	final QueueNo queueNo;
	final Controller controller;
	private Thread thread;
	// private int sendCounter;
	private LinkedList<Integer> sended2LeftSibling;
	private LinkedList<Integer> sended2RightSibling;
	static int temp;

	public SorterThread(int mUX_LENGTH, MessageQueue postBoxOut, QueueNo queueNo, Controller controller) {
		super();
		MAX_LENGTH = mUX_LENGTH;
		this.postBoxOut = postBoxOut;
		this.queueNo = queueNo;
		postBoxIn = new MessageQueue(queueNo);
		postBoxIn.addNewQueueListener(this);
		this.controller = controller;
		numbers = new ArrayList<>();

		sended2LeftSibling = new LinkedList<>();
		sended2RightSibling = new LinkedList<>();

	}

	public final ArrayList<Integer> getNumbers() {
		return numbers;
	}

	public final int getMUX_LENGTH() {
		return MAX_LENGTH;
	}

	public final MessageQueue getPostBoxOut() {
		return postBoxOut;
	}

	public final QueueNo getQueueNo() {
		return queueNo;
	}

	public final Controller getController() {
		return controller;
	}

	public final Thread getThread() {
		if (thread == null) {
			thread = new Thread(this);
		}
		return thread;
	}

	public void getnumbersToSort(ArrayList<Integer> numbers) {
		this.numbers = numbers;
	}

	public MessageQueue getPostBoxIn() {
		return postBoxIn;
	}

	private boolean sendMinimalNumber() {

		boolean isMinimalSended = false;
		if (controller.haveSmallerSibling(queueNo)) {
			Message message = new Message(numbers.get(0), controller.getSmallerSiblingNo(queueNo), queueNo);
			sended2LeftSibling.addLast(message.getNumber());
			numbers.remove(0);

			postBoxOut.sendNumber2Sorter(message);
			isMinimalSended = true;
			// System.out.println(message.from + " sends " + ": " +
			// message.number + "->" + message.too);
		}
		return isMinimalSended;
	}

	private boolean sendMaximalNumber() {
		boolean isMaximalSended = false;
		if (controller.haveRightSibling(queueNo)) {
			if (numbers.size() - 1 < 0) {
				throw new ArrayIndexOutOfBoundsException("Error sending maximal number to "
						+ controller.getGraterSiblingNo(queueNo) + "from " + queueNo);
			}

			Message message = new Message(numbers.get(numbers.size() - 1), controller.getGraterSiblingNo(queueNo),
					queueNo);
			sended2RightSibling.addLast(message.getNumber());
			numbers.remove(numbers.size() - 1);
			isMaximalSended = true;
			postBoxOut.sendNumber2Sorter(message);

		}

		return isMaximalSended;
	}

	private void sortNumbers() {
		numbers.sort(new Comparator<Object>() {
			@Override
			public int compare(Object i1, Object i2) {
				return ((Integer) i1 - (Integer) i2);
			}
		});
	}

	@Override
	public void run() {
		boolean isMaxNumberSended = false;
		boolean isMinNumberSended = false;
		Message message = null;
		boolean gotEndMessage = false;
		printNumbers("Starts :");
		sortNumbers();
		while (!gotEndMessage) {
			if (!isMinNumberSended) {
				isMinNumberSended = sendMinimalNumber();
			}
			if (!isMaxNumberSended) {
				isMaxNumberSended = sendMaximalNumber();
			}
			if (postBoxIn.isEmpty()) {
				postBoxIn.wateForMessage();
			}
			while (!postBoxIn.isEmpty()) {
				message = getMessageFromQueue();
				if (isMaxNumberSended && isMaximalReceived(message)) {
					isMaxNumberSended = false;
				}
				if (isMinNumberSended && isMinimalReceived(message)) {
					isMinNumberSended = false;
				}
				gotEndMessage = message.equals(Message.getEndMessageFrom(postBoxOut.getQueueNo()));
				if (!gotEndMessage) {
					numbers.add(message.number);
				}
			}
			sortNumbers();
		}

		// printNumbers("Ended sorting :");
	}

	private boolean isMaximalReceived(Message message) {
		if (queueNo.getX() == controller.MAX_X && queueNo.getY() == controller.MAX_Y) {
			return false;
		}
		if (message.from.equals(controller.getGraterSiblingNo(queueNo))) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isMinimalReceived(Message message) {
		if (queueNo.getX() == 1 && queueNo.getY() == 1) {
			return false;
		}
		if (message.from.equals(controller.getSmallerSiblingNo(queueNo))) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Message getMessageFromQueue() {
		return postBoxIn.removeFromSendedNumbersQueue();

	}

	public void fillNumbers() {
		Random random = new Random();
		for (int i = 0; i < MAX_LENGTH; i++) {
			numbers.add(random.nextInt(10000));

		}
	}

	public void printNumbers(String s) {
		synchronized (Lock.printLock) {
			System.out.println(s + "  " + queueNo);
			for (Integer number : numbers) {
				System.out.println(number);
			}
		}
	}

}
