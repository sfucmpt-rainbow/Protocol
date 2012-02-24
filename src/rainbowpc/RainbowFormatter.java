package rainbowpc;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.text.MessageFormat;

public final class RainbowFormatter extends Formatter {
	private static final MessageFormat format =
		new MessageFormat("[{0}|{1}|{2,date,h:mm:ss}]: {3}" +
			System.getProperty("line.separator")
		);

    public String format(LogRecord record) {
		Object[] param = new Object[5];
		param[0] = record.getLoggerName();
		param[1] = record.getLevel();
		param[2] = new Date(record.getMillis());
		param[3] = record.getMessage();
		return format.format(param);
    }
}
