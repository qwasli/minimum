package ch.meemin.minimum.admin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.meemin.minimum.entities.settings.SettingImage;
import ch.meemin.minimum.entities.settings.SettingImage.Type;
import ch.meemin.minimum.entities.settings.Settings;
import ch.meemin.minimum.lang.Lang;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class BackgroundsField extends CustomField<List<SettingImage>> implements Receiver, SucceededListener {

	private SettingImage tmpImage;
	private ByteArrayOutputStream os;
	private Lang lang;
	private Settings settings;
	private HorizontalLayout preview = new HorizontalLayout();
	private Upload upload = new Upload(null, this);
	private TextField nameField = new TextField();
	private Button downloadButton = new Button();

	private VerticalLayout layout = new VerticalLayout(downloadButton, preview, new Label(), nameField, upload);

	public BackgroundsField(Lang lang, Settings settings) {
		this.settings = settings;
		this.lang = lang;

		downloadButton.setCaption(lang.getText("downloadDefaultBackground"));
		StreamResource sr = getStreamResource(null);
		FileDownloader fileDownloader = new FileDownloader(sr);
		fileDownloader.extend(downloadButton);
		downloadButton.setStyleName(Reindeer.BUTTON_LINK);
		preview.setHeight(200, Unit.PIXELS);
		nameField.setCaption(lang.getText("Add"));
		nameField.setNullRepresentation("");
		nameField.setImmediate(true);
		nameField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
				upload.setEnabled(!StringUtils.isBlank((CharSequence) event.getProperty().getValue()));
			}
		});
		nameField.setRequired(true);
		upload.setEnabled(false);
		upload.addSucceededListener(this);
	}

	@Override
	protected Component initContent() {
		return layout;
	}

	@Override
	protected void setInternalValue(List<SettingImage> newValue) {
		super.setInternalValue(newValue);
		preview.removeAllComponents();
		if (newValue != null)
			for (SettingImage img : newValue) {
				addImagePreview(img);
			}
	}

	private void addImagePreview(final SettingImage img) {
		Image image = new Image(img.getName(), getStreamResource(img));
		image.setHeight(100, Unit.PERCENTAGE);
		Button b = new Button(lang.getText("Remove"), new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				List<SettingImage> val = new ArrayList<>(BackgroundsField.this.getValue());
				val.remove(img);
				setValue(val);
				// BackgroundsField.super.setInternalValue(val);
				// preview.removeComponent(event.getButton().getParent());
			}
		});
		VerticalLayout vl = new VerticalLayout(image, b);
		vl.setExpandRatio(image, 1f);
		vl.setHeight(100, Unit.PERCENTAGE);

		preview.addComponent(vl);
	}

	private StreamResource getStreamResource(final SettingImage img) {
		StreamResource.StreamSource source = new StreamResource.StreamSource() {

			@Override
			public InputStream getStream() {
				try {
					if (img == null)
						return new FileInputStream(VaadinService.getCurrent().getBaseDirectory().getAbsolutePath()
								+ "/pdfbackground.png");
					return new ByteArrayInputStream(img.getContent());
				} catch (FileNotFoundException e) {
					return null;
				}
			}
		};
		StreamResource resource = new StreamResource(source, "bg.png");
		return resource;
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		// Create upload stream
		tmpImage = new SettingImage();
		tmpImage.setName(nameField.getValue());
		tmpImage.setType(Type.PDF_BACKROUND);
		tmpImage.setMimeType(mimeType);
		os = new ByteArrayOutputStream();
		return os; // Return the output stream to write to
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		// Show the uploaded file in the image viewer
		tmpImage.setContent(os.toByteArray());
		try {
			os.close();
			List<SettingImage> val = new ArrayList<SettingImage>(getValue());
			val.add(tmpImage);
			setValue(val);
			// addImagePreview(tmpImage);
			// super.setInternalValue(val);
			tmpImage = null;
			nameField.setValue(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Class<? extends List<SettingImage>> getType() {
		return (Class<? extends List<SettingImage>>) (new ArrayList<SettingImage>()).getClass();
	}
}
