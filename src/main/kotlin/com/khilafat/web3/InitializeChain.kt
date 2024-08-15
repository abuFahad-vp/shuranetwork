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
    val (maxSize, maxAddr) = maxChainSizeAndAddr(blockchain, allPeers)
    if (maxSize > blockchain.size()){
        println("maxsize = $maxSize, maxAddr = $maxAddr")
            syncTheDB(blockchain,maxAddr,blockchain.size())
//            syncTheDB()
        println("Is it here")
    }
}

suspend fun maxChainSizeAndAddr(blockchain: Blockchain, peers: List<String>): Pair<Int, String> {
    try {
        val client = HttpClient(CIO)
        var size = blockchain.size()
        var truePeer = ""
        for (peer in peers) {
            println("peer = $peer")
            try {
                val response = client.get("http://$peer/size")
                if (response.status.isSuccess()) {
                    val recievedSize = response.bodyAsText().toInt()
                    if (size < recievedSize) {
                        size = recievedSize
                        peer.also { truePeer = it }
                    }
                }
            }catch (e:Exception){
                println("cannot request to the address: $peer.")
            }
        }
        return Pair(size, truePeer)
    } catch(e:Exception) {
        e.printStackTrace()
    }
    return Pair(0,"")
}