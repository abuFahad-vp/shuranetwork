package com.khilafat

import org.iq80.leveldb.Options
import org.iq80.leveldb.impl.Iq80DBFactory.bytes
import org.iq80.leveldb.impl.Iq80DBFactory.factory
import java.io.File

fun main() {
    val options = Options()
    options.createIfMissing(true)
    val ikhwandb = try {
        factory.open(File("ikhwandb.db"),options)
    }catch (e: Exception){
        factory.open(File("ikhwandb_test.db"),options)
    }
    ikhwandb.use { ikhwandb ->
        ikhwandb.put(bytes("0"), bytes("localhost:8080"))
    }
}