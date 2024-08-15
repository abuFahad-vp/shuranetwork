package com.khilafat.web3

import com.khilafat.utils.readAsLines
import org.iq80.leveldb.impl.Iq80DBFactory.*

class Peers {
    private var peers = mutableSetOf<String>()
    private var size = 0
    init {
        peers += readAsLines("peers_address.txt")
        println("Peers Address Available: ")
        println(peers)
    }

    fun allpeers(): List<String> {
        return peers.toList()
    }
}