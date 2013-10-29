package ch.meemin.minimum;

import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.ErrorMessage;
import com.vaadin.ui.AbstractComponent;

public class MyErrorHandler implements ErrorHandler {
	private static Logger LOG = LoggerFactory.getLogger(MyErrorHandler.class);

	@Override
	public void error(ErrorEvent event) {
		doDefault(event);
	}

	public static void doDefault(ErrorEvent event) {
		final Throwable t = event.getThrowable();
		if (t instanceof SocketException) {
			// Most likely client browser closed socket
			LOG.info("SocketException in CommunicationManager. Most likely client (browser) closed socket.");
			return;
		}

		// Finds the original source of the error/exception
		AbstractComponent component = DefaultErrorHandler.findAbstractComponent(event);
		if (component != null) {
			// Shows the error in AbstractComponent
			ErrorMessage errorMessage = AbstractErrorMessage.getErrorMessageForException(t);
			component.setComponentError(errorMessage);
		}

		// also print the error on console
		LOG.error("", t);
	}
}
