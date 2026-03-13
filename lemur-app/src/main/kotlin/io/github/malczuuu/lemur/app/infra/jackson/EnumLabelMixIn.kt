package io.github.malczuuu.lemur.app.infra.jackson

import com.fasterxml.jackson.annotation.JsonValue

interface EnumLabelMixIn {

    @get:JsonValue
    val label: String
}
