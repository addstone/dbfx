package cn.navigational.dbfx.controls.tree.scheme

import cn.navigational.dbfx.SQLClientManager
import cn.navigational.dbfx.config.SCHEME_ICON
import cn.navigational.dbfx.controls.tree.folder.RoleFolder
import cn.navigational.dbfx.controls.tree.folder.SchemeFolder
import cn.navigational.dbfx.controls.tree.folder.TableFolder
import cn.navigational.dbfx.controls.tree.folder.ViewFolder
import cn.navigational.dbfx.controls.tree.impl.ProgressTreeItem
import cn.navigational.dbfx.i18n.I18N
import cn.navigational.dbfx.kit.enums.Clients
import cn.navigational.dbfx.tool.svg.SvgImageTranscoder
import javafx.scene.input.MouseEvent

class SchemeItem(private val scheme: String, private val uuid: String) : ProgressTreeItem() {
    init {
        this.text = scheme
        val info = SQLClientManager.manager.getDbInfo(uuid)
        if (info.database == scheme) {
            this.setSuffixTx(I18N.getString("label.current"))
        }
        this.prefixGra = SvgImageTranscoder.svgToImageView(SCHEME_ICON)
    }

    private fun initScheme() {
        val info = SQLClientManager.manager.getDbInfo(uuid)
        if (info.client == Clients.MYSQL) {
            val viewFolder = ViewFolder(uuid, scheme)
            val tableFolder = TableFolder(scheme, uuid)
            this.treeItem.children.addAll(tableFolder.treeItem, viewFolder.treeItem)
        }
        if (info.client == Clients.POSTGRESQL) {
            val schemeFolder = SchemeFolder(uuid, scheme)
            val roleFolder = RoleFolder(uuid, category = scheme)
            this.addChildren(schemeFolder, roleFolder)
        }
    }

    override fun onMouseClicked(event: MouseEvent) {
        if (event.clickCount > 1 && this.treeItem.children.isEmpty()) {
            initScheme()
        }
    }
}