package log;

public enum LogLevel
{
    Trace(0),
    Debug(1),
    Info(2),
    Warning(3),
    Error(4),
    Fatal(5);
    
    private final int level;
    
    LogLevel(int iLevel)
    {
        level = iLevel;
    }
    
    public int level()
    {
        return level;
    }
}

