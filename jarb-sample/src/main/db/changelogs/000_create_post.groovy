databaseChangeLog() {
	changeSet(author: "jeroen.van.schagen@42.nl", id: "1") {
		comment("Create post.")
		createTable(tableName: "posts") {
            column(autoIncrement: true, name: "id", type: "BIGINT") {
                constraints(nullable: false, primaryKey: true, primaryKeyName: "pk_posts_id")
            }
            column(name: "posted_on", type: "DATETIME") { constraints(nullable: false) }
            column(name: "author", type: "VARCHAR(255)") { constraints(nullable: false) }
            column(name: "title", type: "VARCHAR(255)") { constraints(nullable: false) }
        }
		addUniqueConstraint(tableName: "posts", columnNames: "title", constraintName: "uk_posts_title")
	}
}