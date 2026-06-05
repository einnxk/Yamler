package de.einnik.yamler_v3.core.mapper

import de.einnik.yamler_v3.core.YamlConfig
import de.einnik.yamler_v3.core.annotations.Path
import de.einnik.yamler_v3.core.enums.ConfigMode
import de.einnik.yamler_v3.core.section.ConfigSection
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.concurrent.ConcurrentHashMap

/**
 * In the configMapper we convert a class into a Map or load
 * a map into a class.
 *
 * @author EinNik
 * @since 3.0.0-SNAPSHOT
 */
open class ConfigMapper : BaseConfigMapper() {

    @Throws(Exception::class)
    fun saveToMap(clazz: Class<Any>) : Map<String, Any> {
        val map: MutableMap<String, Any> = ConcurrentHashMap()

        if (!(clazz.superclass.equals(YamlConfig::class.java))) {
            val superClassMap: Map<String, Any> = saveToMap(clazz.superclass)
            map.putAll(superClassMap)
        }

        for (field: Field in clazz.declaredFields) {
            if (doSkip(field)) continue

            var path: String

            path = when (configMode) {
                ConfigMode.PATH_BY_UNDERSCORE ->
                    field.name.replace("_", ".")
                ConfigMode.FIELD_IS_KEY ->
                    field.name
                ConfigMode.DEFAULT ->
                    field.name.replace("_", ".")
            }

            if (field.isAnnotationPresent(Path::class.java)) run {
                val annotationPath: Path = field.getAnnotation(Path::class.java)
                path = annotationPath.value
            }

            if (Modifier.isPrivate(field.modifiers)) {
                field.isAccessible = true
            }

            map[path] = field.get(this)
        }

        val mapConverter = requireNotNull(
            internalConverter.getConverter(Map::class.java)
        ) { "No Map converter registered" }

        @Suppress("UNCHECKED_CAST")
        return mapConverter.toConfig(
            HashMap::class.java,
            map,
            null
        ) as Map<String, Any>
    }

    @Throws(Exception::class)
    fun loadMap(section: Map<Any, Any>, clazz: Class<Any>)  {
        if (!(clazz.superclass.equals(YamlConfig::class.java))) {
            loadMap(section, clazz.superclass)
        }

        for (field: Field in clazz.declaredFields) {
            if (doSkip(field)) continue

            var path = if (configMode == ConfigMode.PATH_BY_UNDERSCORE) field.name
                .replace("_".toRegex(), ".") else field.name

            if (field.isAnnotationPresent(Path::class.java)) run {
                val annotationPath: Path = field.getAnnotation(Path::class.java)
                path = annotationPath.value
            }

            if (Modifier.isPrivate(field.modifiers)) {
                field.isAccessible = true
            }

            internalConverter.fromConfig(this as YamlConfig, field, ConfigSection.convertFromMap(section), path)
        }
    }
}