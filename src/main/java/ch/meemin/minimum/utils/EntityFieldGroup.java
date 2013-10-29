package ch.meemin.minimum.utils;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

public class EntityFieldGroup<T> extends FieldGroup {

	private Class<T> clazz;

	public EntityFieldGroup(Class<T> clazz) {
		this.clazz = clazz;
	}

	public T getEntity() {
		EntityItem<T> item = (EntityItem<T>) getItemDataSource();
		return item.getEntity();
	}

	protected void installValiadator(Field<?> f) {
		if (!f.getValidators().isEmpty())
			return;
		BeanValidator validator = new BeanValidator(clazz, getPropertyId(f).toString());
		validator.setLocale(f.getUI().getLocale());
		f.addValidator(validator);
	};

	@Override
	protected void configureField(final Field<?> field) {
		super.configureField(field);
		if (field.getClass().isAssignableFrom(TextField.class)) {
			((TextField) field).setNullRepresentation("");
			((TextField) field).addBlurListener(new BlurListener() {
				@Override
				public void blur(BlurEvent event) {
					installValiadator(field);
				}
			});
		}
	}

	@Override
	public void commit() throws CommitException {
		EntityItem<T> item = (EntityItem<T>) getItemDataSource();
		// item.getContainer().commit();
		for (Field<?> f : getFields())
			installValiadator(f);
		super.commit();
		for (Field<?> f : getFields())
			f.removeAllValidators();
		item = (EntityItem<T>) getItemDataSource();
		item.getContainer().commit();
	}

	@Override
	public void discard() {
		super.discard();
		for (Field<?> f : getFields())
			f.removeAllValidators();
	}
}
