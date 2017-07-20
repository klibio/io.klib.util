package io.klib.osgi2slf4j;

import org.osgi.framework.Bundle;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * This OSGi log listener forwards entries into the SLF4J logging framework.
 */
public class Log implements LogListener {

	private static final Logger logger = LoggerFactory.getLogger(Log.class.getName());
	private Marker marker;

	@Override
	public void logged(final LogEntry entry) {

		String bundleName = "<not found>";
		Bundle bundle = entry.getBundle();
		if ((bundle != null) && (bundle.getSymbolicName() != null)) {
			bundleName = bundle.getSymbolicName();
		}

		logLogEntry(entry.getLevel(), getBundleMarker(entry), bundleName + " " + entry.getMessage(),
				entry.getException());
	}

	private Marker getBundleMarker(final LogEntry logEntry) {
		if ((logEntry.getBundle() != null) && (logEntry.getBundle().getSymbolicName() != null)) {
			marker = MarkerFactory.getMarker(logEntry.getBundle().getSymbolicName());
		} else {
			marker = MarkerFactory.getMarker(Marker.ANY_NON_NULL_MARKER);
		}
		if (!marker.contains(MarkerFactory.getMarker(Marker.ANY_MARKER))) {
			marker.add(MarkerFactory.getMarker(Marker.ANY_MARKER));
		}
		logger.debug(marker, "created marker: {} | LogEntry: {}", marker.toString(), logEntry.toString());
		return marker;
	}

	private void logLogEntry(final int level, final Marker markerIn, final String message, final Throwable e) {

		switch (level) {
		case LogService.LOG_WARNING:
			logger.warn(markerIn, message, e);
			break;
		case LogService.LOG_DEBUG:
			logger.debug(markerIn, message, e);
			break;
		case LogService.LOG_ERROR:
			logger.error(markerIn, message, e);
			break;
		default:
			logger.info(markerIn, message, e);
			break;
		}
	}
}
