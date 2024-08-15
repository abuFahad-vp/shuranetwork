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
    val (maxSize, maxAddr,maxHash) = maxChainSizeAndAddr(blockchain, allPeers)
    println("maxsize = $maxSize, maxAddr = $maxAddr, maxHash = $maxHash")
    if (maxSize > blockchain.size()){
        println("Syncing with longest db....")
        syncTheDB(blockchain,maxAddr,blockchain.size())
//            syncTheDB()
    }
}

suspend fun maxChainSizeAndAddr(blockchain: Blockchain, peers: List<String>): Triple<Int, String,String> {
    try {
        val client = HttpClient(CIO)
        var size = blockchain.size()
        var truePeer = ""
        var maxHash = ""
        for (peer in peers) {
            println("peer = $peer")
            try {
                val responseSizeRaw = client.get("http://$peer/size")
                if (responseSizeRaw.status.isSuccess()) {
                    val responseSize = responseSizeRaw.bodyAsText().toInt()
                    if (size in 1..<responseSize) {
                        val responseHash = client.get("http://$peer/block/hash/${size-1}")
                        if (responseHash.status.isSuccess()) {
                            maxHash = responseHash.bodyAsText()
                        }
                        size = responseSize
                        peer.also {
                            truePeer = it
                        }
                    }
                }
            }catch (e:Exception){
                println("cannot request to the address: $peer.")
            }
        }
        return Triple(size, truePeer, maxHash)
    } catch(e:Exception) {
        e.printStackTrace()
    }
    return Triple(0,"","")
}