package de.einnik.yamler_v3.core.converter

import de.einnik.yamler_v3.core.YamlConfig
import de.einnik.yamler_v3.core.annotations.PreserveStatic
import de.einnik.yamler_v3.core.exception.InvalidConverterException
import de.einnik.yamler_v3.core.section.ConfigSection
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType

open class InternalConverter {

    private val converters: LinkedHashSet<Converter> = linkedSetOf()
    private val customConverters: MutableList<Class<out Converter>> = mutableListOf()

    init {
        try {
            addConverter(MapConverter::class.java)
        } catch (e: Exception) {
            throw InvalidConverterException("Failed to init default converters: ", e)
        }
    }

    fun addConverter(converter: Class<out Converter>) {
        try {
            val instance: Converter = converter.getDeclaredConstructor(InternalConverter::class.java).newInstance(this)
            converters.add(instance)
        } catch (e: Exception) {
            throw InvalidConverterException("Failed to init converter ${converter.name}: ", e)
        }
    }

    fun getConverter(type: Class<*>?): Converter? {
        return converters.firstOrNull { it.supports(type) }
    }

    fun customConverters(): List<Class<out Converter>> {
        return customConverters.toList()
    }

    fun addCustomConverter(converter: Class<out Converter>) {
        addConverter(converter)
        customConverters.add(converter)
    }

    @Throws(Exception::class)
    fun fromConfig(config: YamlConfig, field: Field, root: ConfigSection, path: String) {
        val obj = field.get(config)

        val converter = if (obj != null) {
            getConverter(obj.javaClass) ?: getConverter(field.type)
        } else {
            getConverter(field.type)
        }

        val isStatic = Modifier.isStatic(field.modifiers)
        if (isStatic) {
            val preserveStatic = field.getAnnotation(PreserveStatic::class.java)
            if (preserveStatic == null || !preserveStatic.value) {
                return
            }
        }
        val genericType = field.genericType as? ParameterizedType
        val rawValue: Any? = root.get(path)

        val finalValue = if (converter != null) {
            val targetType = if (obj != null && getConverter(obj.javaClass) != null) obj.javaClass else field.type
            converter.fromConfig(targetType, rawValue, genericType)
        } else {
            rawValue
        }

        if (isStatic) {
            field.set(null, finalValue)
        } else {
            field.set(config, finalValue)
        }
    }

    @Throws(Exception::class)
    fun toConfig(config: YamlConfig, field: Field, root: ConfigSection, path: String) {
        val obj = field.get(config)

        if (obj == null) {
            root.set(path, null)
            return
        }

        val converter = getConverter(obj.javaClass) ?: getConverter(field.type)
        val genericType = field.genericType as? ParameterizedType

        if (converter != null) {
            val targetType = if (getConverter(obj.javaClass) != null) obj.javaClass else field.type
            root.set(path, converter.toConfig(targetType, obj, genericType))
        } else {
            root.set(path, obj)
        }
    }
}