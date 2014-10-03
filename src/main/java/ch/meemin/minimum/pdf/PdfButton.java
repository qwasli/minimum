package ch.meemin.minimum.pdf;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

public class PdfButton extends Button {
	private static final long serialVersionUID = -2225636470702920255L;

	public enum Style {
		NORMAL,
		SMALL,
		LINK;
	}

	public PdfButton(FileDownloader fd) {
		super();
		setIcon(FontAwesome.FILE_PDF_O);
		setStyleName(ValoTheme.BUTTON_SMALL);
		fd.extend(this);
	}

	public PdfButton(FileDownloader fd, String caption, Style style) {
		super(caption);
		switch (style) {
		case LINK:
			setStyleName(ValoTheme.BUTTON_LINK);
			break;
		case SMALL:
			setStyleName(ValoTheme.BUTTON_SMALL);
			break;
		default:
		}
		fd.extend(this);
	}
}
