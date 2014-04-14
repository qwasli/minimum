package ch.meemin.minimum.admin;

import java.io.InputStream;

import ch.meemin.minimum.EclipseLinkBean;
import ch.meemin.minimum.Minimum;

import com.vaadin.server.StreamResource.StreamSource;

public class BackupStreamSource implements StreamSource {

	@Override
	public InputStream getStream() {
		Minimum min = Minimum.getCurrent();
		EclipseLinkBean elb = min.getEclipseLinkBean().get();
		return elb.createBackupStream();
	}

}
