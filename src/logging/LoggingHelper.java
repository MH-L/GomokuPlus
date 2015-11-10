package logging;

public class LoggingHelper {
	private static LoggingHelper instance;
	private String errorlogName;
	private String accesslogname;

	public LoggingHelper getInstance() {
		if (instance == null) {
			return new LoggingHelper();
		} else {
			return instance;
		}
	}

	private LoggingHelper() {

	}

	public void error() {

	}

	public void warning() {

	}

	public void info() {

	}
}
