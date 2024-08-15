package com.khilafat

import com.khilafat.plugins.*
import com.khilafat.web3.Blockchain
import com.khilafat.web3.Peers
import com.khilafat.web3.initializeChain
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.http.HttpClient

fun main(args: Array<String>) {
    val port = args.getOrElse(0) {"8080"}.toInt()
    PORT = port
    embeddedServer(Netty, port = PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val blockchain = Blockchain()
    val peerdb = Peers()
    initializeChain(blockchain, peerdb)
    println("Peers = ${peerdb.allpeers()}")
    environment.monitor.subscribe(ApplicationStopped) {
        blockchain.closeTheDb()
        println("The DB has beed closed")
    }
    configureSerialization()
    configureRouting(blockchain, peerdb)
}
