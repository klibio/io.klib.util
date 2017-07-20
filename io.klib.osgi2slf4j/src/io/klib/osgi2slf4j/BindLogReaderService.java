package io.klib.osgi2slf4j;

import org.eclipse.equinox.log.ExtendedLogReaderService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * 
 */
@Component
public class BindLogReaderService {

	@Reference
	public void addExtendedLogReaderService(final ExtendedLogReaderService logReader) {
		Log listener = new Log();
		logReader.addLogListener(listener);
	}

}
