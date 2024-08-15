package com.khilafat.web3

import com.khilafat.utils.intToBytes
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import org.iq80.leveldb.*
import org.iq80.leveldb.impl.Iq80DBFactory.*
import java.io.*

class Blockchain {
    private val db: DB
    private var currentIndex = 0
    private var size = 0
    init {
        // opening the db and updating the currentIndex to last index and if there's no key, putting genesis block
        val options = Options()
        options.createIfMissing(true)
        db = factory.open(File("amanah.db"), options)
        db.iterator().use {
            try {
                it.seekToFirst()
                while (it.hasNext()) {
                    it.next()
                    size++
                }
                if (size > 0) {
                    currentIndex = size - 1
                }
            }catch(e:Exception){
                throw e
            }
        }
    }

    fun addGenesisBlock() {
        val block = createGenesisBlock()
        addBlock(block)
    }

    fun getIterator(): DBIterator {
        return db.iterator()
    }

    fun size(): Int {
        return size
    }

    fun addBlock(block: Block): Boolean {
        if (block.index + 1 == currentIndex) {
            if (block.previousHash == getLatestBlock().hash) {
                val batch = db.createWriteBatch()
                batch.use { batch ->
                    batch.put(intToBytes(currentIndex + 1), bytes(block.toString()))
                    db.write(batch)
                }
                currentIndex++
                size++
                return true
            }
        }
        return false
    }

    fun getAllBlocks(): List<Block> {
        return List(currentIndex + 1) { index ->
            getByIndex(index)
        }
    }

    fun closeTheDb() {
        db.close()
    }

    fun getHashByIndex(index: Int): String {
        return getByIndex(index).hash
    }

    fun getLatestHash(): String {
        return getHashByIndex(currentIndex)
    }

    fun getByIndex(index: Int): Block {
        if (index in 0..currentIndex) {
            val value = asString(db.get(intToBytes(index)))
            val block = Block.fromString(value)
            return block
        }
        throw IndexOutOfBoundsException()
    }

    fun getLatestBlock(): Block {
        return getByIndex(currentIndex)
    }

    fun addBlockFromData(data: String): Block {
        val previousBlock = getLatestBlock()
        val newBlock = Block(currentIndex + 1, timeStamp(), data, previousBlock.hash)
        val batch = db.createWriteBatch()
        batch.use { batch ->
            batch.put(intToBytes(currentIndex + 1), bytes(newBlock.toString()))
            db.write(batch)
        }
        currentIndex++
        size++
        return newBlock
    }

    fun isChainValid(): Boolean {
        for (i in 1..currentIndex) {
            val currentBlock = getByIndex(i)
            val previousBlock = getByIndex(i-1)
            if (currentBlock.hash != currentBlock.calculateHash()) {
                return false
            }
            if (currentBlock.previousHash != previousBlock.hash) {
                return false
            }
        }
        return true
    }

    private fun createGenesisBlock(): Block {
        return Block(0,timeStamp(),"Genesis Block", "0")
    }

    private fun timeStamp(): String {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        return now.format(formatter)
    }
}