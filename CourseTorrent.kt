package il.ac.technion.cs.softwaredesign

import Cache
import Bencoding
import getInfoHash
import isTorrentExists
import loadToMemory
import isValidMetaInfo
import removeFromMemory
import getTorrent

/**
 * This is the class implementing CourseTorrent, a BitTorrent client.
 *
 * Currently specified:
 * + Parsing torrent metainfo files (".torrent" files)
 */
class CourseTorrent {

    /**
     * Load in the torrent metainfo file from [torrent]. The specification for these files can be found here:
     * [Metainfo File Structure](https://wiki.theory.org/index.php/BitTorrentSpecification#Metainfo_File_Structure).
     *
     * After loading a torrent, it will be available in the system, and queries on it will succeed.
     *
     * This is a *create* command.
     *
     * @throws IllegalArgumentException If [torrent] is not a valid metainfo file.
     * @throws IllegalStateException If the infohash of [torrent] is already loaded.
     * @return The infohash of the torrent, i.e., the SHA-1 of the `info` key of [torrent].
     */
    fun load(torrent:  ByteArray): String {

        val metaInfo = Bencoding.decodeValue(torrent)

        if (!isValidMetaInfo(metaInfo)){
            throw IllegalArgumentException()
        }

        val infohash : String = getInfoHash(torrent)
        if (isTorrentExists(infohash)){
            throw IllegalStateException()
        }

        loadToMemory(infohash, torrent)

        return infohash
    }


    /**
     * Remove the torrent identified by [infohash] from the system.
     *
     * This is a *delete* command.
     *
     * @throws IllegalArgumentException If [infohash] is not loaded.
     */
    fun unload(infohash: String): Unit {
        if (!isTorrentExists(infohash)){
            throw IllegalArgumentException()
        }
        removeFromMemory(infohash)
    }

    /**
     * Return the announce URLs for the loaded torrent identified by [infohash].
     *
     * See [BEP 12](http://bittorrent.org/beps/bep_0012.html) for more information. This method behaves as follows:
     * * If the "announce-list" key exists, it will be used as the source for announce URLs.
     * * If "announce-list" does not exist, "announce" will be used, and the URL it contains will be in tier 1.
     * * The announce URLs should *not* be shuffled.
     *
     * This is a *read* command.
     *
     * @throws IllegalArgumentException If [infohash] is not loaded.
     * @return Tier lists of announce URLs.
     */
    fun announces(infohash: String): List<List<String>>  {
       if (!isTorrentExists(infohash)){
            throw IllegalStateException()
        }
        val torrent = getTorrent(infohash)
        val metaInfo  = Bencoding.decodeValue(torrent)
        if(metaInfo is HashMap<*,*>){
            if(metaInfo.containsKey("announce-list")){
                return metaInfo["announce-list"] as List<List<String>>
            } else {
                return listOf(listOf(metaInfo["announce"])) as List<List<String>>
            }
        }
        throw IllegalArgumentException()
    }
}