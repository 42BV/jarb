databaseChangeLog() {
	changeSet(author: "jeroen@42.nl", id: "1") {
		comment("Change default value of persons")
		dropDefaultValue(tableName: "persons", columnName: "name")
		addDefaultValue(tableName: "persons", columnName: "name", defaultValue: "henk")
	}
}