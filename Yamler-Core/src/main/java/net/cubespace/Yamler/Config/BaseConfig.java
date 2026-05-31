package net.cubespace.Yamler.Config;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class BaseConfig {
	protected transient File CONFIG_FILE = null;
	protected transient String[] CONFIG_HEADER = null;
	protected transient ConfigMode CONFIG_MODE = ConfigMode.DEFAULT;
	protected transient boolean skipFailedObjects = false;

	protected transient InternalConverter converter = new InternalConverter();

	public void update(ConfigSection configSection) {

	}

	public void addConverter(Class addConverter) throws InvalidConverterException {
		converter.addCustomConverter(addConverter);
	}

	protected boolean doSkip(Field field) {
		if (Modifier.isTransient(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
			return true;
		}

		if (Modifier.isStatic(field.getModifiers())) {
			if (!field.isAnnotationPresent(PreserveStatic.class)) {
				return true;
			}

			PreserveStatic presStatic = field.getAnnotation(PreserveStatic.class);
			return !presStatic.value();
		}

		return false;
	}

	protected void configureFromSerializeOptionsAnnotation() {
		if (!getClass().isAnnotationPresent(SerializeOptions.class)) {
			return;
		}

		SerializeOptions options = getClass().getAnnotation(SerializeOptions.class);
		CONFIG_HEADER = options.configHeader();
		CONFIG_MODE = options.configMode();
		skipFailedObjects = options.skipFailedObjects();
	}
}
