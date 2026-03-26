package log;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Очередь сообщений лога
 */
public class LogQueue implements Iterable<LogEntry> {
	/**
	 * Запись в очереди логов
	 */
	private class LogQueueNode {
		/**
		 * Запись с информацией
		 */
		private final LogEntry entry;
		
		/**
		 * Ссылка на следующую запись
		 */
		private volatile LogQueueNode m_next;
		
		/**
		 * Создать запись в очереди логов
		 */
		private LogQueueNode(LogEntry logEntry) {
			entry = logEntry;
			m_next = null;
		}
		
		/**
		 * Получить запись лога
		 */
		private LogEntry getLogEntry() {
			return entry;
		}
		
		/**
		 * Получить ссылку на следующую запись
		 */
		private synchronized LogQueueNode getNextNode() {
			return m_next;
		}
		
		/**
		 * Поменять ссылку на следующую запись
		 */
		private synchronized void setNextNode(LogQueueNode next) {
			m_next = next;
		}
	}

	
	/**
	 * Ссылка на первую запись
	 */
	private volatile LogQueueNode m_head;
	
	/**
	 * Ссылка на последнюю запись
	 */
	private volatile LogQueueNode m_tail;
	
	/**
	 * Максимальное число записей в очереди
	 */
	private final int m_maxLength;
	
	/**
	 * Текущее число записей в очереди
	 */
	private volatile int queueLength;
	
	/**
	 * Создать очередь сообщений лога
	 */
	public LogQueue(int maxLength) {
		m_head = null;
		m_tail = null;
		m_maxLength = (maxLength > 0 ? maxLength : 0);
		queueLength = 0;
	}

	/**
	 * Получить текущую длину очереди
	 */
	public synchronized int size() {
		return queueLength;
	}
	
	/**
	 * Добавить запись в очередь
	 */
	public synchronized void addLogQueueNode(LogEntry entry) {
		LogQueueNode newNode = new LogQueueNode(entry);
		++queueLength;
		if (m_head == null) {
			m_tail = m_head = newNode;
		} else {
			if (queueLength > m_maxLength) {
				removeLogQueueNode();
			}
			m_head.setNextNode(newNode);
			m_head = newNode;
		}
	}
	
	/**
	 * Удалить запись из очереди
	 */
	public synchronized void removeLogQueueNode() {
		if (m_tail != null) {
			--queueLength;
			if (m_tail == m_head) {
				m_tail = m_head = null;
			} else {
				LogQueueNode old = m_tail;
				m_tail = m_tail.getNextNode();
				old.setNextNode(null);
			}
		}
	}
	
	/**
	 * Получить записи из указанного диапазона до последнего невключительно
	 */
	public synchronized List<LogEntry> subList(int firstIdx, int lastIdx) {
		int first = (firstIdx > 0 ? firstIdx : 0);
		int last = (lastIdx < queueLength ? lastIdx : queueLength);
		int entriesCount = last - first;
		LogEntry[] entries = new LogEntry[entriesCount];
		LogQueueNode currentNode = m_tail;
		for (int i = 0; i < first; ++i) {
			currentNode = currentNode.getNextNode();
		}
		for (int i = 0; i < entriesCount; ++i) {
			entries[i] = currentNode.getLogEntry();
			currentNode = currentNode.getNextNode();
		}
		return List.of(entries);
	}
	
	/**
	 * Очистить очередь логов
	 */
	public synchronized void clear() {
		while (m_tail != null) {
			LogQueueNode old = m_tail;
			m_tail = old.getNextNode();
			old.setNextNode(null);
		}
		m_head = null;
	}

	/**
	 * Получить итератор очереди логов
	 */
	@Override
	public Iterator<LogEntry> iterator() {
		return new Iterator<LogEntry> () {
            private volatile LogQueueNode currentNode = m_tail;

            @Override
            public synchronized boolean hasNext() {
                return currentNode != null;
            }

            @Override
            public synchronized LogEntry next() {
            	if (!hasNext()) {
            		currentNode = m_tail;            		
            	}
            	if (hasNext()) {
            		LogEntry currentEntry = currentNode.getLogEntry();
            		currentNode = currentNode.getNextNode();
            		return currentEntry;            		
            	}
            	throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("no changes allowed");
            }
        };
	}
}
