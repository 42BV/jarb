databaseChangeLog() {
	changeSet(author: "jeroen@42.nl", id: "1") {
		comment("Creating a table for persons")
		createTable(tableName: "persons") {
		    column(autoIncrement: true, name: "id", type: "BIGINT") {
		        constraints(nullable: false, primaryKey: true)
		    }
		    column(name: "name", type: "VARCHAR(255)", defaultValue: "henk")
		}
	}
}