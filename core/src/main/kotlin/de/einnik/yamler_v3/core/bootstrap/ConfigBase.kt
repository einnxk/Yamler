package de.einnik.yamler_v3.core.bootstrap

import de.einnik.yamler_v3.core.annotations.PreserveStatic
import de.einnik.yamler_v3.core.annotations.SerializeOptions
import de.einnik.yamler_v3.core.converter.Converter
import de.einnik.yamler_v3.core.converter.InternalConverter
import de.einnik.yamler_v3.core.enums.ConfigMode
import org.jetbrains.annotations.NotNull
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Modifier

open class ConfigBase {

    @Transient
    protected var configFile: File? = null
    @Transient
    protected var configHeader: Array<String>? = null
    @Transient
    protected var configMode: ConfigMode = ConfigMode.DEFAULT
    @Transient
    protected var skipFailedObject: Boolean = false
    @Transient
    protected val internalConverter: InternalConverter = InternalConverter()

    fun addConverter(@NotNull converter: Class<out Converter>) {
        internalConverter.addConverter(converter)
    }

    fun doSkip(@NotNull field: Field) : Boolean {
        if (Modifier.isTransient(field.modifiers) || Modifier.isFinal(field.modifiers)) {
            return true
        }

        if (Modifier.isStatic(field.modifiers)) {
            if (field.isAnnotationPresent(PreserveStatic::class.java)) {
                return true
            }

            val preserveStatic: PreserveStatic = field.getAnnotation(PreserveStatic::class.java)
            return !(preserveStatic.value)
        }

        return false
    }

    fun serializeConfigurationFromAnnotation() {
        if (!javaClass.isAnnotationPresent(SerializeOptions::class.java)) {
            return
        }

        val options: SerializeOptions = javaClass.getAnnotation(SerializeOptions::class.java)
        configHeader = options.configHeader
        configMode = options.configMode
        skipFailedObject = options.skipFailedObjects
    }
}