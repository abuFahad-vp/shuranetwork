package com.khilafat.web3

import com.khilafat.plugins.syncTheDB
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

fun initializeChain(blockchain: Blockchain, peers: Peers) = runBlocking {
    val allPeers = peers.allpeers()
    if (blockchain.size() == 0 && allPeers.isEmpty()) {blockchain.initializeGenesis()}
    else {
        val (maxSize, maxAddr) = maxChainSizeAndAddr(allPeers)
        println("$maxSize, $maxAddr")
        if (blockchain.size() < maxSize) {
//            syncTheDB(blockchain,maxAddr,blockchain.size())
            syncTheDB()
        }
    }
}

suspend fun maxChainSizeAndAddr(peers: List<String>): Pair<Int, String> {
    try {
        val client = HttpClient(CIO)
        var size = 0
        var truePeer = ""
        for (peer in peers) {
            val response = client.get("http://$peer/size")
            if (response.status.isSuccess()) {
                val recievedSize = response.bodyAsText().toInt()
                if (size < recievedSize) {
                    size = recievedSize
                    peer.also { truePeer = it }
                }
            }
        }
        return Pair(size, truePeer)
    } catch(e:Exception) {
        e.printStackTrace()
    }
    return Pair(0,"")
}