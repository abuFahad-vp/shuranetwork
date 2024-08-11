package com.khilafat.plugins

import com.khilafat.web3.Block
import com.khilafat.web3.Blockchain
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.websocket.*
import kotlinx.coroutines.*

fun Application.configureListeners(blockchain: Blockchain) = runBlocking {
    val client = HttpClient(CIO) {
        install(WebSockets)
    }
    try {
        delay(1000)
        client.webSocket(method = HttpMethod.Get, host = "localhost", port = 8080, path = "/ws") {
            val messageOutputRoutine = launch {outgoingMessages(blockchain)}
//            val userInputRoutine = launch {inputMessage("Hello myi dr friend")}
//            userInputRoutine.join()
            messageOutputRoutine.join()
        }
    } catch (e: Exception){
        println("Server is not started yet")
    }
    client.close()
}

suspend fun DefaultWebSocketSession.outgoingMessages(blockchain: Blockchain) {
    try {
        for (frame in incoming) {
            frame as? Frame.Text ?: continue
            val block = Block.fromString(frame.readText())
            if (blockchain.addBlock(block)) {
                println("Successfully added $block")
            }else {
                println("Failed to add $block")
            }
        }
    } catch (e: Exception) {
        println("Error while receiving: ${e.localizedMessage}")
        return
    }
}

suspend fun DefaultWebSocketSession.inputMessage(msg: String) {
    while (true) {
        try {
            send(msg)
            delay(1000)
        }catch (e: Exception){
            println("Error while sending: " + e.localizedMessage)
            return
        }
    }
}