package ch.meemin.minimum.customers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.teemu.webcam.Webcam;
import org.vaadin.teemu.webcam.Webcam.CaptureSucceededEvent;
import org.vaadin.teemu.webcam.Webcam.CaptureSucceededListener;

import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.Photo;
import ch.meemin.minimum.lang.Lang;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Image;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;

public class PhotoComponent extends CustomComponent implements Receiver, SucceededListener, CaptureSucceededListener,
		ClickListener {
	private static final Logger LOG = LoggerFactory.getLogger(PhotoComponent.class);

	ByteArrayOutputStream os;

	EntityItem<Customer> customerItem;
	Lang lang;
	Photo photo;

	Image image;
	Webcam webcam;
	Upload upload;
	Button replacePhoto = new Button(null, this);

	VerticalLayout layout = new VerticalLayout();

	public PhotoComponent(Lang lang) {
		this.lang = lang;
		setSizeUndefined();
		layout.setSizeUndefined();
		layout.setHeight(100, Unit.PERCENTAGE);
		layout.setMargin(new MarginInfo(false, false, false, true));
		layout.setSpacing(true);

		webcam = new Webcam();
		webcam.setHeight(100, Unit.PERCENTAGE);
		webcam.setReceiver(this);
		webcam.addCaptureSucceededListener(this);

		upload = new Upload(null, this);
		upload.addSucceededListener(this);

		setCompositionRoot(layout);
	}

	private void addImage(Image image) {
		if (this.image != null)
			layout.removeComponent(this.image);
		layout.removeComponent(webcam);
		this.image = image;
		this.image.setHeight(100, Unit.PERCENTAGE);
		layout.addComponentAsFirst(image);
		layout.setExpandRatio(this.image, 1f);
		replacePhoto.setCaption(lang.getText("replacePhoto"));
	}

	private void addWebcam() {
		if (this.image != null)
			layout.removeComponent(this.image);
		layout.addComponentAsFirst(webcam);
		replacePhoto.setCaption(lang.getText("Cancel"));
	}

	public void setCustomer(EntityItem<Customer> customerItem) {
		this.customerItem = customerItem;
		Customer customer = customerItem.getEntity();
		layout.removeAllComponents();
		Image image = customer.getImage();
		if (image != null) {
			addImage(image);
		} else {
			addWebcam();
		}
		layout.addComponent(replacePhoto);
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		// Create upload stream
		photo = new Photo();
		photo.setName(filename);
		photo.setMimeType(mimeType);
		os = new ByteArrayOutputStream();
		return os; // Return the output stream to write to
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		// Show the uploaded file in the image viewer
		processUploadedPhoto();
	}

	private void processUploadedPhoto() {
		photo.setContent(os.toByteArray());
		try {
			os.close();
			customerItem.getItemProperty("photo").setValue(photo);
			customerItem.commit();
			Image img = customerItem.getEntity().getImage();
			addImage(img);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void captureSucceeded(CaptureSucceededEvent event) {
		processUploadedPhoto();
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if (layout.getComponent(0).equals(webcam)) {
			Customer customer = customerItem.getEntity();
			if (customer.getImage() != null)
				addImage(customer.getImage());
			else {
				// Set Default image
				photo = new Photo();
				photo.setName("beer.jpg");
				photo.setMimeType("image/jpeg");
				String path = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/beer.jpg";
				try {
					photo.setContent(FileUtils.readFileToByteArray(new File(path)));
					customerItem.getItemProperty("photo").setValue(photo);
					customerItem.commit();
					Image img = customerItem.getEntity().getImage();
					addImage(img);
				} catch (IOException e) {
					LOG.warn("Could not set default Image", e);
				}
			}
		} else
			addWebcam();

	}
}
