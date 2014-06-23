package ch.meemin.minimum.admin;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import ch.meemin.minimum.Minimum;
import ch.meemin.minimum.entities.settings.Settings;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.utils.CommitClickListener;
import ch.meemin.minimum.utils.DiscardClickListener;
import ch.meemin.minimum.utils.EntityFieldGroup;
import ch.meemin.minimum.utils.FormLayout;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.EntityItemProperty;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Buffered.SourceException;
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
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This is the window used to add new contacts to the 'address book'. It does not do proper validation - you can add
 * weird stuff.
 */
public class EditSettingsWin extends Window implements ValueChangeListener {
	private static final long serialVersionUID = 1L;
	private final JPAContainer<Settings> container;
	private final Lang lang;
	private Field<String> studentAgeLimit;
	private List<Field<?>> ageLimits = new ArrayList<Field<?>>();
	private Tab general, basic, pdf, time, prepaid;
	private final Button okButton;
	private EditTimeSubscriptionsField tsField;
	private EditPrepaidSubscriptionsField psField;
	private Settings settings;

	private EnumMap<Flag, CheckBox> flagFields = new EnumMap<Settings.Flag, CheckBox>(Flag.class);

	private final FieldGroup form = new EntityFieldGroup<Settings>(Settings.class) {
		private static final long serialVersionUID = -1026191871445717584L;

		@Override
		public void commit() throws CommitException {
			super.commit();
			EntityItem<Settings> item = (EntityItem<Settings>) getItemDataSource();

			EntityItemProperty f = item.getItemProperty("flags");
			f.setValue(f.getValue()); // This set flags as changed
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

		FormLayout basicSettings = new FormLayout();
		basicSettings.setSizeUndefined();
		basicSettings.setDescription(lang.getText("SettingsDescription"));
		form.setFieldFactory(new FieldFactory());
		EntityItem<Settings> item = container.getItem(1L);
		form.setItemDataSource(item);
		settings = item.getEntity();
		basicSettings.addComponent(form.buildAndBind(lang.getText("adminPassword"), "adminPassword"));
		basicSettings.addComponent(createFlagField(settings, Flag.USE_BASIC_SUBSCRIPTION));
		basicSettings.addComponent(createFlagField(settings, Flag.USE_MASTER_SUBSCRIPTION));
		basicSettings.addComponent(createFlagField(settings, Flag.USE_TIME_SUBSCRIPTION));
		basicSettings.addComponent(createFlagField(settings, Flag.USE_PREPAID_SUBSCRIPTION));

		basicSettings.addComponent(createFlagField(settings, Flag.DIRECTLOGIN));
		basicSettings.addComponent(form.buildAndBind(lang.getText("minutesForWarning"), "minutesForWarning"));

		basicSettings.addComponent(createFlagField(settings, Flag.SUBSCRIPTIONIDONCARD));
		basicSettings.addComponent(createFlagField(settings, Flag.USE_STUDENT));
		basicSettings.addComponent(createFlagField(settings, Flag.REQUIREEMAIL));
		studentAgeLimit = (Field<String>) form.buildAndBind(lang.getText("studentAgeLimit"), "studentAgeLimit");
		studentAgeLimit.addValueChangeListener(this);
		basicSettings.addComponent(studentAgeLimit);

		basicSettings.addComponent(createFlagField(settings, Flag.USE_BIRTHDAY));
		Field<?> al = form.buildAndBind(lang.getText("underAgeLimit"), "underAgeLimit");
		ageLimits.add(al);
		basicSettings.addComponent(al);
		al = form.buildAndBind(lang.getText("childAgeLimit"), "childAgeLimit");
		ageLimits.add(al);
		basicSettings.addComponent(al);
		al = form.buildAndBind(lang.getText("seniorAgeLimit"), "seniorAgeLimit");
		ageLimits.add(al);
		basicSettings.addComponent(al);
		basicSettings.addComponent(createFlagField(settings, Flag.USE_NEWSLETTER));

		Button backup = new Button(lang.get("DownloadBackup"));
		FileDownloader fd = new FileDownloader(new StreamResource(new BackupStreamSource(), "backup.zip"));
		fd.extend(backup);
		basicSettings.addComponent(backup);

		general = tabSheet.addTab(basicSettings, lang.getText("BasicSettings"));

		FormLayout basicSubsSettings = new FormLayout();
		basicSubsSettings.addComponent(form.buildAndBind(lang.getText("normalPrize"), "normalPrize"));
		basicSubsSettings.addComponent(form.buildAndBind(lang.getText("studentPrize"), "studentPrize"));
		basicSubsSettings.addComponent(form.buildAndBind(lang.getText("childrenPrize"), "childrenPrize"));
		basicSubsSettings.addComponent(form.buildAndBind(lang.getText("underAgePrize"), "underAgePrize"));
		basicSubsSettings.addComponent(form.buildAndBind(lang.getText("seniorPrize"), "seniorPrize"));

		basic = tabSheet.addTab(basicSubsSettings, lang.getText("BasicSubscriptionSettings"));

		Field<Boolean> showPhotoOnCard = createFlagField(item.getEntity(), Flag.PHOTOONCARD);

		BackgroundsField bgField = form.buildAndBind(lang.getText("Backgrounds"), "images", BackgroundsField.class);

		Field<?> cH = form.buildAndBind(null, "cardHeight");
		cH.setWidth(30, Unit.PIXELS);
		Field<?> cW = form.buildAndBind(null, "cardWidth");
		cW.setWidth(30, Unit.PIXELS);
		HorizontalLayout hxw = new HorizontalLayout(cH, new Label("x"), cW, new Label(lang.getText("HxWinMM")));
		Field<?> cX = form.buildAndBind(null, "cardX");
		cX.setWidth(30, Unit.PIXELS);
		Field<?> cY = form.buildAndBind(null, "cardY");
		cY.setWidth(30, Unit.PIXELS);
		HorizontalLayout xy = new HorizontalLayout(cX, new Label("x"), cY, new Label(lang.getText("PosinMM")));
		xy.setSpacing(true);

		Field printBorder = createFlagField(settings, Flag.PRINTCARDBORDER);

		VerticalLayout hl = new VerticalLayout(showPhotoOnCard, bgField, hxw, xy, printBorder);

		bgField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				tsField.refresh();
				psField.refresh();
			}
		});

		pdf = tabSheet.addTab(hl, lang.getText("PDF"));

		tsField = form.buildAndBind(lang.getText("timeSubscriptions"), "timeSubscriptions",
				EditTimeSubscriptionsField.class);
		time = tabSheet.addTab(tsField);
		psField = form.buildAndBind(lang.getText("prepaidSubscriptions"), "prepaidSubscriptions",
				EditPrepaidSubscriptionsField.class);
		prepaid = tabSheet.addTab(psField);
		form.setItemDataSource(item);

		setContent(vLayout);
		valueChange(null);
		new ValidatePasswordWindow(minimum, this);
	}

	private CheckBox createFlagField(Settings settings, Flag flag) {
		CheckBox cb = new CheckBox(lang.getText(flag.name()), new FlagDataSource(settings, flag));
		cb.addValueChangeListener(this);
		cb.setDescription(lang.getText(flag.name() + "-desc"));
		this.flagFields.put(flag, cb);
		return cb;
	}

	@Override
	public void valueChange(ValueChangeEvent event) {

		Boolean useStudent = flagFields.get(Flag.USE_STUDENT).getValue();
		Boolean useBirthday = flagFields.get(Flag.USE_BIRTHDAY).getValue();
		String studentLimit = studentAgeLimit.getValue();

		studentAgeLimit.setEnabled(useStudent);

		if (useStudent && studentLimit != null) {
			flagFields.get(Flag.USE_BIRTHDAY).setEnabled(false);
			useBirthday = true;
		} else
			flagFields.get(Flag.USE_BIRTHDAY).setEnabled(true);
		for (Field al : ageLimits)
			al.setEnabled(useBirthday);

		prepaid.setEnabled(flagFields.get(Flag.USE_PREPAID_SUBSCRIPTION).getValue());
		basic.setEnabled(flagFields.get(Flag.USE_BASIC_SUBSCRIPTION).getValue());
		time.setEnabled(flagFields.get(Flag.USE_TIME_SUBSCRIPTION).getValue());

		psField.collapsColumns(settings);
		tsField.collapsColumns(settings);
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
				return (T) new EditTimeSubscriptionsField(lang, settings);
			}
			if (EditPrepaidSubscriptionsField.class.isAssignableFrom(fieldType)) {
				return (T) new EditPrepaidSubscriptionsField(lang, settings);
			}
			if (DateField.class.isAssignableFrom(fieldType)) {
				DateField df = new DateField();
				df.setResolution(Resolution.DAY);
				return (T) df;
			}
			if (BackgroundsField.class.isAssignableFrom(fieldType))
				return (T) new BackgroundsField(lang, settings);

			return super.createField(type, fieldType);
		}
	}

}
