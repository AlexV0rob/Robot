package log;

import java.util.ArrayList;
import java.util.Collections;

public class LogWindowSource
{
    private int queueLength;
    
    private final LogQueue messages;
    private final ArrayList<LogChangeListener> listeners;
    private volatile LogChangeListener[] activeListeners;
    
    public LogWindowSource(int iQueueLength) 
    {
        queueLength = iQueueLength;
        messages = new LogQueue(queueLength);
        listeners = new ArrayList<>();
    }
    
    public void registerListener(LogChangeListener listener)
    {
        synchronized(listeners)
        {
            listeners.add(listener);
            activeListeners = null;
        }
    }
    
    public void unregisterListener(LogChangeListener listener)
    {
        synchronized(listeners)
        {
            listeners.remove(listener);
            activeListeners = null;
        }
    }

	public void clearListeners() {
		synchronized(listeners) {
			listeners.clear();
			activeListeners = null;
		}
	}
    
    public void append(LogLevel logLevel, String strMessage)
    {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        messages.addLogQueueNode(entry);
        LogChangeListener [] activeListeners = this.activeListeners;
        if (activeListeners == null)
        {
            synchronized (listeners)
            {
                if (this.activeListeners == null)
                {
                    activeListeners = listeners.toArray(new LogChangeListener [0]);
                    this.activeListeners = activeListeners;
                }
            }
        }
        for (LogChangeListener listener : activeListeners)
        {
            listener.onLogChanged();
        }
    }
    
    public int size()
    {
        return messages.size();
    }

    public Iterable<LogEntry> range(int startFrom, int count)
    {
        if (startFrom < 0 || startFrom >= messages.size())
        {
            return Collections.emptyList();
        }
        int indexTo = Math.min(startFrom + count, messages.size());
        return messages.subList(startFrom, indexTo);
    }

    public Iterable<LogEntry> all()
    {
        return messages;
    }
}
