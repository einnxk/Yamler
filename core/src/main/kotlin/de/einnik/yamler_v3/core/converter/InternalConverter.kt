package de.einnik.yamler_v3.core.converter

import de.einnik.yamler_v3.core.exception.InvalidConverterException

class InternalConverter {

    private val converters: LinkedHashSet<Converter> = linkedSetOf()
    private val customConverters: MutableList<Class<out Converter>> = mutableListOf()

    init {
        try {

        } catch (e: Exception) {
            throw InvalidConverterException("Failed to init default converters: ", e)
        }
    }

    fun addConverter(converter: Class<out Converter>) {
        try {
            val instance: Converter = converter.getDeclaredConstructor().newInstance()
            converters.add(instance)
        } catch (e: Exception) {
            throw InvalidConverterException("Failed to init converter ${converter.name}: ", e)
        }
    }

    fun customConverters(): List<Class<out Converter>> {
        return customConverters
            .map { it.javaClass as Class<out Converter> }
            .toList()
    }

    fun addCustomConverter(converter: Class<out Converter>) {
        addConverter(converter)
        customConverters.add(converter)
    }
}