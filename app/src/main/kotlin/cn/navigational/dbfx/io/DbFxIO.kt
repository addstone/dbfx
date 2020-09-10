package cn.navigational.dbfx.io

import cn.navigational.dbfx.DatabaseMetaManager
import cn.navigational.dbfx.SQLClientManager
import cn.navigational.dbfx.kit.config.PASSWORD
import cn.navigational.dbfx.model.DatabaseMeta
import cn.navigational.dbfx.model.DbInfo
import cn.navigational.dbfx.security.AseAlgorithm
import cn.navigational.dbfx.kit.utils.OssUtils
import cn.navigational.dbfx.kit.utils.StringUtils
import cn.navigational.dbfx.kit.utils.VertxUtils
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import java.io.File
import java.util.*

const val DEFAULT_KEY = "2EBC@#=="
const val S_DB_PATH = "config/s_db.json"

val APP_HOME = OssUtils.getUserHome() + File.separator + ".dbfx"

var dbInfoPath = "$APP_HOME/db_info.json"


/**
 *
 * Save DbInfo to local file
 *
 * @param info  db info
 *
 */
suspend fun saveDbInfo(info: DbInfo) {
    Objects.requireNonNull(info)
    val json = JsonObject.mapFrom(info)
    val password = json.getString(PASSWORD)
    if (StringUtils.isNotEmpty(password)) {
        val aseStr = AseAlgorithm.encrypt(password, DEFAULT_KEY)
        json.put(PASSWORD, aseStr)
    }
    val fs = VertxUtils.getFileSystem()
    val array = getDbFile()
    array.add(json)
    fs.writeFile(dbInfoPath, array.toBuffer())
}

/**
 *
 * Get db_info.json file content
 *
 */
private suspend fun getDbFile(): JsonArray {
    val fs = VertxUtils.getFileSystem()
    val array: JsonArray
    if (fs.existsBlocking(dbInfoPath)) {
        array = fs.readFile(dbInfoPath).await().toJsonArray()
    } else {
        array = JsonArray()
        fs.createFile(dbInfoPath).await()
        fs.writeFile(dbInfoPath, array.toBuffer())
    }
    return array
}

/**
 *
 * Check project need direction is exist
 *
 */
private suspend fun checkDir() {
    val fs = VertxUtils.getFileSystem()
    val exist = fs.exists(APP_HOME).await()
    if (exist) {
        return
    }
    fs.mkdirs(APP_HOME).await()
}

/**
 *
 * Load local cached database info
 *
 */
private suspend fun loadDbInfo() {
    val array = getDbFile()
    for (item in array.stream().map { it as JsonObject }) {
        val info = item.mapTo(DbInfo::class.java)
        if (StringUtils.isNotEmpty(info.password)) {
            val deStr = AseAlgorithm.decrypt(info.password, DEFAULT_KEY)
            info.password = deStr
        }
        SQLClientManager.addDbInfo(info)
    }
}

/**
 *
 * Load local s_db.json file into memcached
 *
 */
private suspend fun loadDbMetaData() {
    val fs = VertxUtils.getFileSystem()
    val array = fs.readFile(S_DB_PATH).await().toJsonArray()
    val items = arrayListOf<DatabaseMeta>()
    array.forEach {
        val json = it as JsonObject
        items.add(json.mapTo(DatabaseMeta::class.java))
    }
    DatabaseMetaManager.addDbMeta(items)
}

/**
 *
 *Init project io
 *
 */
suspend fun init() {
    checkDir()
    loadDbInfo()
    loadDbMetaData()
}

//fun main() {
//    VertxUtils.getFileSystem().mkdirBlocking(APP_HOME)
//}