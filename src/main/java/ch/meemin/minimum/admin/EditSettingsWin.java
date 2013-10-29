package ch.meemin.minimum.admin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import ch.meemin.minimum.Minimum;
import ch.meemin.minimum.entities.settings.SettingImage;
import ch.meemin.minimum.entities.settings.SettingImage.Type;
import ch.meemin.minimum.entities.settings.Settings;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.utils.CommitClickListener;
import ch.meemin.minimum.utils.DiscardClickListener;
import ch.meemin.minimum.utils.EntityFieldGroup;
import ch.meemin.minimum.utils.FormLayout;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Buffered.SourceException;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

/**
 * This is the window used to add new contacts to the 'address book'. It does not do proper validation - you can add
 * weird stuff.
 */
public class EditSettingsWin extends Window {
	private static final long serialVersionUID = 1L;
	private final JPAContainer<Settings> container;
	private final Lang lang;
	private Field<Boolean> useStudentField;
	private Field<Boolean> useBirthDayField;
	private Field<String> studentAgeLimit;
	private Field<?> ageLimit;
	private final Button okButton;

	private final FieldGroup form = new EntityFieldGroup<Settings>(Settings.class) {
		private static final long serialVersionUID = -1026191871445717584L;

		@Override
		public void commit() throws CommitException {
			super.commit();
			EntityItem<Settings> item = (EntityItem<Settings>) getItemDataSource();
			Settings c = item.getEntity();
			container.commit();
			((Minimum) getUI()).loadSettings();
			close();
		}

		@Override
		public void discard() throws SourceException {
			super.discard();
			close();
		};
	};

	public EditSettingsWin(UI ui) {
		super();

		setModal(true);
		Minimum minimum = (Minimum) ui;
		this.lang = minimum.getLang();
		container = new JPAContainer<Settings>(Settings.class);
		container.setEntityProvider(minimum.getSettingsProvider());
		setSizeUndefined();
		VerticalLayout vLayout = new VerticalLayout();
		vLayout.setSizeUndefined();
		TabSheet tabSheet = new TabSheet();
		tabSheet.setWidth(100, Unit.PERCENTAGE);
		vLayout.addComponent(tabSheet);
		vLayout.setExpandRatio(tabSheet, 1f);

		HorizontalLayout footer = new HorizontalLayout();
		footer.setWidth(100, Unit.PERCENTAGE);
		okButton = new Button(lang.getText("OK"), new CommitClickListener(form));
		okButton.setClickShortcut(KeyCode.ENTER);
		footer.addComponent(okButton);
		Button cancelButton = new Button(lang.getText("Cancel"), new DiscardClickListener(form));
		cancelButton.setClickShortcut(KeyCode.ESCAPE);
		footer.addComponent(cancelButton);
		vLayout.addComponent(footer);

		FormLayout formLayout = new FormLayout();
		formLayout.setSizeUndefined();
		formLayout.setDescription(lang.getText("SettingsDescription"));
		form.setFieldFactory(new FieldFactory());
		EntityItem<Settings> item = container.getItem(1L);
		form.setItemDataSource(item);
		formLayout.addComponent(form.buildAndBind(lang.getText("adminPassword"), "adminPassword"));

		formLayout.addComponent(form.buildAndBind(lang.getText("directLogin"), "directLogin"));
		formLayout.addComponent(form.buildAndBind(lang.getText("useSubscriptionID"), "useSubscriptionID"));
		formLayout.addComponent(form.buildAndBind(lang.getText("normalPrize"), "normalPrize"));
		StudentValueChangeListener svcl = new StudentValueChangeListener();
		useStudentField = (Field<Boolean>) form.buildAndBind(lang.getText("useStudentField"), "useStudentField");
		useStudentField.addValueChangeListener(svcl);
		formLayout.addComponent(useStudentField);
		formLayout.addComponent(form.buildAndBind(lang.getText("studentPrize"), "studentPrize"));
		useBirthDayField = (Field<Boolean>) form.buildAndBind(lang.getText("useBirthDayField"), "useBirthDayField");
		useBirthDayField.addValueChangeListener(svcl);
		formLayout.addComponent(useBirthDayField);
		ageLimit = form.buildAndBind(lang.getText("underAgeLimit"), "underAgeLimit");
		formLayout.addComponent(ageLimit);
		studentAgeLimit = (Field<String>) form.buildAndBind(lang.getText("studentAgeLimit"), "studentAgeLimit");
		studentAgeLimit.addValueChangeListener(svcl);
		formLayout.addComponent(studentAgeLimit);
		formLayout.addComponent(form.buildAndBind(lang.getText("underAgePrize"), "underAgePrize"));
		formLayout.addComponent(form.buildAndBind(lang.getText("useNewsletterField"), "useNewsletterField"));

		tabSheet.addTab(formLayout, lang.getText("Settings"));
		Field<Boolean> showPhotoOnCard = (Field<Boolean>) form.buildAndBind(lang.getText("showPhotoOnCard"),
				"showPhotoOnCard");
		Button downloadButton = new Button(lang.getText("downloadBackground"));
		StreamResource sr = getPDFStream();
		FileDownloader fileDownloader = new FileDownloader(sr);
		fileDownloader.extend(downloadButton);
		downloadButton.setStyleName(Reindeer.BUTTON_LINK);
		ImageUpload rec = new ImageUpload();
		Upload upload = new Upload(null, rec);
		upload.addSucceededListener(rec);

		VerticalLayout hl = new VerticalLayout(showPhotoOnCard, downloadButton, upload);
		tabSheet.addTab(hl, lang.getText("PDF"));

		tabSheet.addTab(form.buildAndBind(lang.getText("timeSubscriptions"), "timeSubscriptions",
				EditTimeSubscriptionsField.class));
		tabSheet.addTab(form.buildAndBind(lang.getText("prepaidSubscriptions"), "prepaidSubscriptions",
				EditPrepaidSubscriptionsField.class));
		form.setItemDataSource(item);

		setContent(vLayout);
		new ValidatePasswordWindow(minimum, this);
	}

	private class StudentValueChangeListener implements ValueChangeListener {
		@Override
		public void valueChange(ValueChangeEvent event) {
			Boolean useStudent = useStudentField.getValue();
			Boolean useBirthday = useBirthDayField.getValue();
			String studentLimit = studentAgeLimit.getValue();

			studentAgeLimit.setEnabled(useStudent);

			if (useStudent && studentLimit != null) {
				useBirthDayField.setValue(true);
				useBirthDayField.commit();
				useBirthDayField.setEnabled(false);
				useBirthday = true;
			} else
				useBirthDayField.setEnabled(true);

			ageLimit.setEnabled(useBirthday);

		}
	}

	private class FieldFactory extends DefaultFieldGroupFieldFactory {
		@Override
		public <T extends Field> T createField(Class<?> type, Class<T> fieldType) {
			if (TextArea.class.isAssignableFrom(fieldType)) {
				TextArea ta = new TextArea();
				ta.setNullRepresentation("");
				ta.addFocusListener(new FocusListener() {
					@Override
					public void focus(FocusEvent event) {
						okButton.removeClickShortcut();
					}
				});
				ta.addBlurListener(new BlurListener() {
					@Override
					public void blur(BlurEvent event) {
						okButton.setClickShortcut(KeyCode.ENTER);
					}
				});
				return (T) ta;
			}
			if (EditTimeSubscriptionsField.class.isAssignableFrom(fieldType)) {
				return (T) new EditTimeSubscriptionsField(lang);
			}
			if (EditPrepaidSubscriptionsField.class.isAssignableFrom(fieldType)) {
				return (T) new EditPrepaidSubscriptionsField(lang);
			}
			if (DateField.class.isAssignableFrom(fieldType)) {
				DateField df = new DateField();
				df.setResolution(Resolution.DAY);
				return (T) df;
			}
			return super.createField(type, fieldType);
		}
	}

	private StreamResource getPDFStream() {
		StreamResource.StreamSource source = new StreamResource.StreamSource() {

			@Override
			public InputStream getStream() {
				SettingImage oldBG = ((EntityItem<Settings>) form.getItemDataSource()).getEntity().imageByType(
						Type.PDF_BACKROUND);
				if (oldBG != null)
					return new ByteArrayInputStream(oldBG.getContent());
				else
					try {
						return new FileInputStream(VaadinService.getCurrent().getBaseDirectory().getAbsolutePath()
								+ "/pdfbackground.png");
					} catch (FileNotFoundException e) {
						return null;
					}
			}
		};
		StreamResource resource = new StreamResource(source, "bg.png");
		return resource;
	}

	private class ImageUpload implements Receiver, SucceededListener {
		ByteArrayOutputStream os;
		SettingImage image = new SettingImage();

		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			// Create upload stream
			image.setType(Type.PDF_BACKROUND);
			image.setMimeType(mimeType);
			os = new ByteArrayOutputStream();
			return os; // Return the output stream to write to
		}

		@Override
		public void uploadSucceeded(SucceededEvent event) {
			// Show the uploaded file in the image viewer
			image.setContent(os.toByteArray());
			try {
				os.close();
				Property imagesProp = form.getItemDataSource().getItemProperty("images");
				List<SettingImage> images = (List<SettingImage>) imagesProp.getValue();
				SettingImage oldBG = ((EntityItem<Settings>) form.getItemDataSource()).getEntity().imageByType(
						Type.PDF_BACKROUND);
				if (oldBG != null)
					images.remove(oldBG);
				images.add(image);
				imagesProp.setValue(images);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
