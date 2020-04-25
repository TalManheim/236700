import java.security.MessageDigest
import Bencoding
import java.security.NoSuchAlgorithmException
import il.ac.technion.cs.softwaredesign.storage.read
import il.ac.technion.cs.softwaredesign.storage.write

var DELETED_ENTRY : ByteArray  = "this entry deleted entry".toByteArray()

fun SHA1(convertme: ByteArray?): String {
    val md = MessageDigest.getInstance("SHA-1")
    return md.digest(convertme).joinToString(separator = ""){"%02x".format(it)}
}


class Cache{
    val torrentHash = hashMapOf<String,ByteArray>()

    fun add(key: String, value: ByteArray):Unit {
        torrentHash[key] = value
    }

    fun get(key: String):ByteArray{
        return torrentHash[key]!!
    }

    fun conatins(key: String):Boolean {
        return torrentHash.containsKey(key)
    }

    fun remove(key: String):Unit {
        torrentHash.remove(key)
    }
}

fun isValidMetaInfo (metaInfo : Any?):Boolean{ // todo: check the type of the values
    return true

   /* if(!(metaInfo is HashMap<*,*>)) return false

    val validKeys = arrayListOf<String>("info", "announce", "announce-list", "creation date", "comment", "created by",
            "encoding")
    var containsInfo = false
    var containsAnnounce = false
    for (key in metaInfo.keys){
        when (key){
            "info" ->       { containsInfo = true }
            "announce" ->   { containsAnnounce = true }
             else ->        { if (!validKeys.contains(key)) return false }
        }
    }
    if(containsInfo && containsAnnounce) return true
    return false */
}

fun getInfoHash (torrent : ByteArray) : String {
    val info = (Bencoding.decodeFlatDictionary(torrent))["info"]
    return SHA1(info)
}

fun isTorrentExists(infoHash : String) : Boolean {
    val inMemory : ByteArray? = read(infoHash.toByteArray())
    if ( inMemory!=null && !inMemory.equals(DELETED_ENTRY)){
        return true
    }

    return false
}

fun loadToMemory(infoHash: String, torrent: ByteArray){
    write(key = infoHash.toByteArray(), value = torrent)
}

fun removeFromMemory (infoHash: String){
    write(key = infoHash.toByteArray(),value = DELETED_ENTRY )
}

fun getTorrent (infoHash: String):ByteArray{
    val infoHashByteArray :ByteArray = infoHash.toByteArray()
    val inMemory : ByteArray? = read(infoHash.toByteArray())
    if ( inMemory!=null && !inMemory.equals(DELETED_ENTRY)){
        return inMemory
    }

    throw IllegalArgumentException()

}