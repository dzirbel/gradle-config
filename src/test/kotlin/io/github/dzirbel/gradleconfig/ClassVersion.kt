package io.github.dzirbel.gradleconfig

import java.io.DataInputStream
import java.io.File

/** Reads the Java class version from this `.class` [File]'s metadata. */
fun File.classVersion(): Int {
    return DataInputStream(inputStream()).use {
        it.readInt()
        it.readUnsignedShort()
        it.readUnsignedShort()
    }
}
