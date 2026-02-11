package log;

public final class Logger
{
    private static final LogWindowSource DEFAULT_LOG_SOURCE =
            new LogWindowSource(100);

    private Logger()
    {
    }

    public static void debug(String strMessage)
    {
        DEFAULT_LOG_SOURCE.append(LogLevel.Debug, strMessage);
    }
    
    public static void error(String strMessage)
    {
        DEFAULT_LOG_SOURCE.append(LogLevel.Error, strMessage);
    }

    public static LogWindowSource getDefaultLogSource()
    {
        return DEFAULT_LOG_SOURCE;
    }
}
