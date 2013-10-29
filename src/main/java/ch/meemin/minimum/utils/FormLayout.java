package ch.meemin.minimum.utils;

import java.util.Iterator;

import lombok.Getter;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

public class FormLayout extends CustomComponent implements Layout, Layout.SpacingHandler {
	private static final long serialVersionUID = 4558807170297329882L;

	private VerticalLayout master = new VerticalLayout();
	private com.vaadin.ui.FormLayout formLayout;
	@Getter
	private Component header = null;
	@Getter
	private Component footer = null;

	public FormLayout() {
		formLayout = new com.vaadin.ui.FormLayout();
		formLayout.setSizeUndefined();
		master.setSizeUndefined();
		master.addComponent(formLayout);
		setSpacing(true);
		master.setMargin(true);
		setCompositionRoot(master);
	}

	public void setHeader(Component c) {
		if (c == null && header != null)
			master.removeComponent(header);
		if (c != null)
			master.addComponent(c, 0);
		this.header = c;
	}

	public void setFooter(Component c) {
		if (c == null && footer != null)
			master.removeComponent(footer);
		if (c != null)
			master.addComponent(c);
		this.footer = c;
	}

	public HorizontalLayout createDefaultFooter() {
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(master.isSpacing());
		setFooter(hl);
		return hl;
	}

	@Override
	public void setDescription(String description) {
		super.setDescription(description);
		setHeader(new Label(description));
		header.setWidth(260, Unit.PIXELS);
	}

	@Override
	public void addComponent(Component c) {
		formLayout.addComponent(c);
	}

	@Override
	public void addComponents(Component... components) {
		formLayout.addComponents(components);
	}

	@Override
	public void removeComponent(Component c) {
		formLayout.removeComponent(c);
	}

	@Override
	public void removeAllComponents() {
		formLayout.removeAllComponents();
	}

	@Override
	public void replaceComponent(Component oldComponent, Component newComponent) {
		formLayout.replaceComponent(oldComponent, newComponent);
	}

	@Override
	@Deprecated
	public Iterator<Component> getComponentIterator() {
		return formLayout.getComponentIterator();
	}

	@Override
	public void moveComponentsFrom(ComponentContainer source) {
		formLayout.moveComponentsFrom(source);

	}

	@Override
	@Deprecated
	public void addListener(ComponentAttachListener listener) {
		formLayout.addListener(listener);
	}

	@Override
	@Deprecated
	public void removeListener(ComponentAttachListener listener) {
		formLayout.removeListener(listener);
	}

	@Override
	@Deprecated
	public void addListener(ComponentDetachListener listener) {
		formLayout.addListener(listener);
	}

	@Override
	@Deprecated
	public void removeListener(ComponentDetachListener listener) {
		formLayout.removeListener(listener);
	}

	@Override
	public void addComponentAttachListener(ComponentAttachListener listener) {
		formLayout.addComponentAttachListener(listener);
	}

	@Override
	public void removeComponentAttachListener(ComponentAttachListener listener) {
		formLayout.removeComponentAttachListener(listener);
	}

	@Override
	public void addComponentDetachListener(ComponentDetachListener listener) {
		formLayout.addComponentDetachListener(listener);
	}

	@Override
	public void removeComponentDetachListener(ComponentDetachListener listener) {
		formLayout.removeComponentDetachListener(listener);
	}

	@Override
	public void setSpacing(boolean enabled) {
		master.setSpacing(enabled);
		formLayout.setSpacing(enabled);
	}

	@Override
	public boolean isSpacing() {
		return master.isSpacing();
	}
}
