package ch.meemin.minimum.admin;

import java.io.InputStream;

import lombok.AllArgsConstructor;
import ch.meemin.minimum.EclipseLinkBean;

import com.vaadin.server.StreamResource.StreamSource;

@AllArgsConstructor
public class BackupStreamSource implements StreamSource {

	private EclipseLinkBean eclipseLinkBean;

	@Override
	public InputStream getStream() {
		return eclipseLinkBean.createBackupStream();
	}

}
