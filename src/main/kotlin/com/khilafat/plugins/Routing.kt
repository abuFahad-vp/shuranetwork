package com.khilafat.plugins

import com.khilafat.utils.byteToInt
import com.khilafat.utils.intToBytes
import com.khilafat.web3.Blockchain
import com.khilafat.web3.Peers
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(blockchain: Blockchain, peerdb: Peers) {
    routing {
        get("/validate") {
            val isValid = blockchain.isChainValid()
            if (isValid) {
                call.respond(
                    "The blockchain is valid!"
                )
            }else {
                call.respond(
                    "The blockchain is *NOT* valid!"
                )
            }
        }

        get("/size") {
            call.respond("${blockchain.size()}")
        }

        get("/sync/{index}") {
            try {
                val index = call.parameters["index"]!!.toInt()
                println("Index = $index")
                call.respondOutputStream(ContentType.Application.OctetStream) {
                    val iterator = blockchain.getIterator()
                    iterator.seek(intToBytes(index))
                    iterator.use {
                        while (iterator.hasNext()) {
                            val key = byteToInt(iterator.peekNext().key)
                            if (key >= index) {
                                println("key = $key")
                                val blockData = iterator.peekNext().value
                                this.write(intToBytes(blockData.size))
                                this.write(blockData)
                            }
                            iterator.next()
                        }
                    }
                }
            }catch (e:Exception){
                call.respond(HttpStatusCode.BadRequest)
            }

        }

        get("/all") {
            call.respond(blockchain.getAllBlocks())
        }

        route("/block") {
            get("/{index}") {
                val parameters = call.parameters["index"] ?: "0"
                try {
                    val block = blockchain.getByIndex(parameters.toInt())
                    call.respond(
                        block
                    )
                }catch(e:Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            post {
                try {
                    val data = call.receive<String>()
                    val block = blockchain.addBlockFromData(data) // also added to the db
                    call.respond(block)
                } catch (e:Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}
