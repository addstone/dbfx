package cn.navigational.dbfx.controls.table

import cn.navigational.dbfx.kit.config.NULL_TAG
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.scene.control.TableCell


class DataTableCell : TableCell<ObservableList<StringProperty>, String>() {
    companion object {
        /**
         * Null value
         */
        internal const val NULL_VALUE = "<null>"

        /**
         * Null value apply style
         */
        internal const val NULL_STYLE: String = "null-style"
    }

    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, isEmpty)
        val status = (empty || tableColumn == null || tableRow == null)
        if (status) {
            text = null
            return
        }
        var value = item
        if (item == NULL_TAG) {
            value = NULL_VALUE
            if (!styleClass.contains(NULL_STYLE)) {
                styleClass.add(NULL_STYLE)
            }
        } else {
            styleClass.remove(NULL_STYLE)
        }
        text = value
    }
}