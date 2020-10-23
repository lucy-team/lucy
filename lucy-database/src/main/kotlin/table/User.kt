package table

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = varchar("id", 10) // Column<String>
    val name = varchar("name", length = 50) // Column<String>

    override val primaryKey = PrimaryKey(id) // name is optional here
}