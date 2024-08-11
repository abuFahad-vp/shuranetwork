package com.khilafat.web3

import com.khilafat.utils.intToBytes
import org.iq80.leveldb.DB
import org.iq80.leveldb.Options
import org.iq80.leveldb.impl.Iq80DBFactory.*
import java.io.File

class Peers {
    private var ikhwandb: DB
    private var peers = mutableSetOf<String>()
    private var size = 0
    init {
        val options = Options()
        options.createIfMissing(true)
        ikhwandb = try {
            factory.open(File("ikhwandb.db"),options)
        }catch (e: Exception){
            factory.open(File("ikhwandb_test.db"),options)
        }

        ikhwandb.iterator().use {
            try {
                it.seekToFirst()
                while (it.hasNext()) {
                    val value = asString( it.peekNext().value)
                    peers.add(value)
                    it.next()
                    size++
                }
            }catch(e:Exception){
                throw e
            }
        }
    }

    fun allpeers(): List<String> {
        return peers.toList()
    }

    fun closeTheDb() {
        ikhwandb.close()
    }
}