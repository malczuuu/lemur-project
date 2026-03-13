package io.github.malczuuu.lemur.contract

/**
 * Fields of classes implementing this interface must be deliberately nullable. This way it is possible to first
 * deserialize JSON into object and then validate it using Bean Validation. If fields were non-nullable, deserialization
 * would fail before validation, and there wouldn't be a proper error messages.
 *
 * Classes implementing this interface must include following statement in their KDoc:
 * ```
 * /**
 *  * See [TransportMessage] for explanation of why fields are nullable.
 *  */
 * ```
 */
interface TransportMessage
