package com.khilafat.plugins

import com.khilafat.web3.Blockchain
import com.khilafat.web3.Peers
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration

fun Application.configureSockets(webSocketSession: Peers,blockchain: Blockchain) {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/ws") { // websocketSession
//            val id = webSocketSession.addWebSocketSession(this)
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val receivedText = frame.readText()
//                        println("Received from $id: $receivedText")
                        outgoing.send(Frame.Text("Received: $receivedText"))
                    }
                }
            } finally {
//                webSocketSession.removeWebSocketSession(id)
            }
        }
    }
}
