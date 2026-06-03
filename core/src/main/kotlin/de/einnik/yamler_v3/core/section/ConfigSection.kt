package de.einnik.yamler_v3.core.section

/**
 * An ConfigSection is a model class representing the section of an
 * YAML file with their key value schema
 *
 * @author EinNik
 * @since 3.0.0-SNAPSHOT
 */
open class ConfigSection {

    private val fullPath: String
    protected val map: MutableMap<Any, Any?> = LinkedHashMap()

    constructor() {
        this.fullPath = ""
    }

    constructor(root: ConfigSection, key: String) {
        this.fullPath =
            if (root.fullPath.isNotEmpty()) "${root.fullPath}.$key"
            else key
    }

    fun create(path: String?): ConfigSection {
        require(path != null) { "Cannot create section at empty path" }

        var i1 = -1
        var i2: Int

        var section = this

        while (path.indexOf('.', i1 + 1).also {
                i2 = i1 + 1
                i1 = it
            } != -1) {

            val node = path.substring(i2, i1)
            val subSection = section.getConfigSection(node)

            section = subSection ?: section.create(node)
        }

        val key = path.substring(i2)

        if (section === this) {
            val result = ConfigSection(this, key)
            map[key] = result
            return result
        }

        return section.create(key)
    }

    private fun getConfigSection(node: String): ConfigSection? {
        val value = map[node]
        return value as? ConfigSection
    }

    fun set(path: String, value: Any?) {
        set(path, value, true)
    }

    fun set(
        path: String?,
        value: Any?,
        searchForSubNodes: Boolean
    ) {
        require(path.isNotEmpty()) {
            "Cannot set a value at empty path"
        }

        var i1 = -1
        var i2 = 0

        var section = this

        if (searchForSubNodes) {
            while (path.indexOf('.', i1 + 1).also {
                    i2 = i1 + 1
                    i1 = it
                } != -1) {

                val node = path.substring(i2, i1)
                val subSection = section.getConfigSection(node)

                section = subSection ?: section.create(node)
            }
        }

        val key = path.substring(i2)

        if (section === this) {
            if (value == null) {
                map.remove(key)
            } else {
                map[key] = value
            }
        } else {
            section.set(key, value)
        }
    }

    protected fun mapChildrenValues(
        output: MutableMap<Any, Any?>,
        section: ConfigSection?,
        deep: Boolean
    ) {
        if (section == null) return

        for ((key, value) in section.map) {
            if (value is ConfigSection) {
                val result: MutableMap<Any, Any?> = LinkedHashMap()

                output[key] = result

                if (deep) {
                    mapChildrenValues(result, value, true)
                }
            } else {
                output[key] = value
            }
        }
    }

    fun getValues(deep: Boolean): Map<Any, Any?> {
        val result: MutableMap<Any, Any?> = LinkedHashMap()
        mapChildrenValues(result, this, deep)
        return result
    }

    fun remove(path: String) {
        set(path, null)
    }

    fun has(path: String?): Boolean {
        require(path != null) {
            "Cannot remove a Value at empty path"
        }

        var i1 = -1
        var i2: Int

        var section = this

        while (path.indexOf('.', i1 + 1).also {
                i2 = i1 + 1
                i1 = it
            } != -1) {

            val node = path.substring(i2, i1)
            val subSection = section.getConfigSection(node)

            if (subSection == null) {
                return false
            }

            section = subSection
        }

        val key = path.substring(i2)

        return if (section === this) {
            map.containsKey(key)
        } else {
            section.has(key)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(path: String?): T? {
        require(path != null) {
            "Cannot remove a Value at empty path"
        }

        var i1 = -1
        var i2: Int

        var section = this

        while (path.indexOf('.', i1 + 1).also {
                i2 = i1 + 1
                i1 = it
            } != -1) {

            val node = path.substring(i2, i1)
            val subSection = section.getConfigSection(node)

            section = subSection ?: section.create(node)
        }

        val key = path.substring(i2)

        return if (section === this) {
            map[key] as T?
        } else {
            section.get(key)
        }
    }

    fun getRawMap(): Map<Any, Any?> = map

    companion object {

        fun convertFromMap(
            config: Map<*, *>
        ): ConfigSection {
            val configSection = ConfigSection()

            for ((key, value) in config) {
                if (key != null) {
                    configSection.map[key] = value
                }
            }

            return configSection
        }
    }
}