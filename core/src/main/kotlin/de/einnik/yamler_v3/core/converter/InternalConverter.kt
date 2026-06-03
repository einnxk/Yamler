package de.einnik.yamler_v3.core.converter

import de.einnik.yamler_v3.core.exception.InvalidConverterException

class InternalConverter {

    private val converters: LinkedHashSet<Converter> = linkedSetOf()
    private val customConverters: MutableList<Converter> = mutableListOf()

    init {
        try {

        } catch (e: Exception) {
            throw InvalidConverterException("Failed to init default converters: ", e)
        }
    }

    fun addConverter(converter: Class<out Converter>) {
        try {
            val instance: Converter = converter.getDeclaredConstructor().newInstance()
            customConverters.add(instance)
        } catch (e: Exception) {
            throw InvalidConverterException("Failed to init converter ${converter.name}: ", e)
        }
    }
}