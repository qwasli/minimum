package ch.meemin.minimum.pdf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.meemin.minimum.Minimum;
import ch.meemin.minimum.entities.Customer;

import com.lowagie.text.FontFactory;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinService;

public class PdfCreator {
	private static final Logger LOG = LoggerFactory.getLogger(PdfCreator.class);
	static {
		try {
			File basepath = VaadinService.getCurrent().getBaseDirectory();
			FontFactory.registerDirectory(new File(basepath, "/FONTS").getAbsolutePath());
		} catch (Exception e) {
			LOG.error("Problem with BaseFont", e);
		}
	}

	public static final String DEFAULTFONT = "dejavu sans";

	public static FileDownloader getCustomerInfoDownloader(Minimum minimum, Long customerID) {
		StreamResource streamResource = getCustomerInfoStream(minimum, customerID);
		return prepareStream(streamResource);
	}

	protected static FileDownloader prepareStream(StreamResource streamResource) {
		streamResource.setCacheTime(5000); // no cache (<=0) does not work with IE8
		streamResource.setMIMEType("application/pdf"); //$NON-NLS-1$
		FileDownloader fd = new FileDownloader(streamResource);
		return fd;
	}

	public static StreamResource getCustomerInfoStream(final Minimum minimum, final Long customerID) {
		StreamSource source = new StreamSource() {
			private static final long serialVersionUID = -8729914782417683L;

			@Override
			public InputStream getStream() {
				try {
					PipedInputStream in = new PipedInputStream();
					final PipedOutputStream out = new PipedOutputStream(in);

					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Customer customer = minimum.getCustomerProvider().getEntityManager().find(Customer.class, customerID);
								CustomerInfoPDF pdf = new CustomerInfoPDF(customer, minimum);
								pdf.writePDFProtocol(out);
							} catch (Throwable e) {
								LOG.warn("Problem crating PDF", e);
								try {
									out.close();
								} catch (IOException e1) {}
								throw new RuntimeException(e);
							}
						}
					}).start();

					return in;

				} catch (IOException e1) {
					e1.printStackTrace();
					return null;
				}

			}
		};
		return new StreamResource(source, customerID + ".pdf");
	}
}
