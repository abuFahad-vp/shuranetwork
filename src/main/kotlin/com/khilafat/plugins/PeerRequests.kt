package com.khilafat.plugins

import com.khilafat.utils.byteToInt
import com.khilafat.web3.Block
import com.khilafat.web3.Blockchain
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking

//fun main() {
//    syncTheDB()
//}

//fun syncTheDB() = runBlocking {
fun syncTheDB(blockchain: Blockchain, peerAddress: String, lastIndex: Int) = runBlocking {
    val client = HttpClient(CIO)
    try {
        val response: HttpResponse = client.get("http://$peerAddress/sync/$lastIndex")
//        val response: HttpResponse = client.get("http://localhost:8080/sync/0")
        val channel = response.bodyAsChannel()
        while (!channel.isClosedForRead) {
            val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
            while (!packet.isEmpty) {
                val blocksize = byteToInt(packet.readBytes(4))
//                println("block size = $blocksize")
                if (blocksize > 0) {
                    val bytes = packet.readBytes(blocksize)
                    val block = Block.fromString(bytes.toString(Charset.defaultCharset()))
                    blockchain.addBlock(block)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        client.close()
    }
}