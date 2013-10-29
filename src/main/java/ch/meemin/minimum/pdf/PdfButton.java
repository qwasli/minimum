package ch.meemin.minimum.pdf;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.Reindeer;

public class PdfButton extends Button {
	private static final long serialVersionUID = -2225636470702920255L;

	private static ThemeResource PDFICON = new ThemeResource("icons/16/document-pdf.png");

	public enum Style {
		NORMAL,
		SMALL,
		LINK;
	}

	public PdfButton(FileDownloader fd) {
		super();
		setIcon(PDFICON);
		setStyleName(Reindeer.BUTTON_SMALL);
		fd.extend(this);
	}

	public PdfButton(FileDownloader fd, String caption, Style style) {
		super(caption);
		setIcon(PDFICON);
		switch (style) {
		case LINK:
			setStyleName(Reindeer.BUTTON_LINK);
			break;
		case SMALL:
			setStyleName(Reindeer.BUTTON_SMALL);
			break;
		default:
		}
		fd.extend(this);
	}
}
