package de.einnik.yamler.core.exception

/**
 * An Exception that is thrown when the converter does not match what we
 * except it to do
 *
 * @author EinNik
 * @since 3.0.0-SNAPSHOT
 */
class InvalidConverterException @JvmOverloads constructor(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)