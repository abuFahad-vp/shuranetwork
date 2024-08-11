package com.khilafat.utils

import java.nio.ByteBuffer

fun intToBytes(value: Int): ByteArray {
    return ByteBuffer.allocate(4).putInt(value).array()
}

fun byteToInt(value: ByteArray): Int {
    return ByteBuffer.wrap(value).int
}
