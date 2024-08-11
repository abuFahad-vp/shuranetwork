package com.khilafat.web3

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import java.security.MessageDigest

@Serializable
data class Block(
    val index: Int,
    val timestamp: String,
    val data: String,
    val previousHash: String,
    @Transient
    private var _hash: String = ""
) {
    @SerialName("hash")
    val hash: String = _hash.ifEmpty { calculateHash() }
    fun calculateHash(): String {
        val hashString = "$index$timestamp$data$previousHash"
        return hashString.sha256().also { _hash = it }
    }

    private fun String.sha256(): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(this.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    override fun toString(): String {
        return "{\"index\":$index,\"timestamp\":\"$timestamp\",\"data\":\"$data\",\"previousHash\":\"$previousHash\",\"hash\":\"$hash\"}"
    }

    companion object {
        fun fromString(str: String): Block {
            return Json.decodeFromString<Block>(str).apply { calculateHash() }
        }
    }
}