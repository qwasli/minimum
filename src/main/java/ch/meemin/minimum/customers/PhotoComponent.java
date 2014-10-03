package ch.meemin.minimum.customers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.vaadin.teemu.webcam.Webcam;
import org.vaadin.teemu.webcam.Webcam.CaptureSucceededEvent;
import org.vaadin.teemu.webcam.Webcam.CaptureSucceededListener;

import ch.meemin.minimum.CurrentSettings;
import ch.meemin.minimum.Minimum.SelectEvent;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.Photo;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.provider.SubscriptionProvider;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.cdi.UIScoped;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;

@UIScoped
public class PhotoComponent extends VerticalLayout implements Receiver, SucceededListener, CaptureSucceededListener,
		ClickListener {

	ByteArrayOutputStream os;
	@Inject
	private CurrentSettings currentSettings;
	@Inject
	private SubscriptionProvider subsProvider;

	@Inject
	private Containers containers;

	EntityItem<Customer> customerItem;

	@Inject
	Lang lang;
	Photo photo;

	Image image;
	Webcam webcam;
	Upload upload;
	Button replacePhoto = new Button(null, this);

	@PostConstruct
	public void init() {
		setSizeUndefined();
		setSizeUndefined();
		setHeight(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);
		setStyleName("photocomp");

		webcam = new Webcam();
		webcam.setHeight(100, Unit.PERCENTAGE);
		webcam.setReceiver(this);
		webcam.addCaptureSucceededListener(this);

		upload = new Upload(null, this);
		upload.addSucceededListener(this);

	}

	private void addImage(Image image) {
		if (this.image != null)
			removeComponent(this.image);
		removeComponent(webcam);
		this.image = image;
		this.image.setSizeUndefined();
		this.image.addStyleName("photo");
		addComponentAsFirst(image);
		setExpandRatio(this.image, 1f);
		replacePhoto.setCaption(lang.getText("replacePhoto"));
	}

	private void addWebcam() {
		if (this.image != null)
			removeComponent(this.image);
		addComponentAsFirst(webcam);
		replacePhoto.setCaption(lang.getText("Cancel"));
	}

	public void clear() {
		this.customerItem = null;
		this.removeAllComponents();
	}

	public void setCustomer(@Observes SelectEvent event) {
		if (event.isClear()) {
			clear();
			return;
		}
		Long id = event.getId();
		if (currentSettings.getSettings().is(Flag.SUBSCRIPTIONIDONCARD)) {
			Subscription sub = subsProvider.getSubscription(id);
			if (sub == null) {
				clear();
				return;
			} else
				id = sub.getCustomer().getId();
		}

		removeAllComponents();

		customerItem = containers.getCustContainer().getItem(id);
		Image image = customerItem.getEntity().getImage();
		if (image != null) {
			addImage(image);
		} else {
			Notification.show(lang.getText("NoPhotoWarning"), Notification.Type.WARNING_MESSAGE);
			addWebcam();
		}
		addComponent(replacePhoto);
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
		if (getComponent(0).equals(webcam)) {
			Customer customer = customerItem.getEntity();
			if (customer.getImage() != null)
				addImage(customer.getImage());
			else {
				// Set Default image
				photo = new Photo();
				photo.setName("beer.jpg");
				photo.setMimeType("image/jpeg");
				String path = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/beer.jpg";
				Image img = new Image(null, new FileResource(new File(path)));
				addImage(img);
			}
		} else
			addWebcam();

	}
}
