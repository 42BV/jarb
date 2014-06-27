databaseChangeLog() {
    changeSet(author: "jeroen.van.schagen@42.nl", id: "1") {
		comment("Add message to post.")
		addColumn(tableName: "posts") {
            column(name: "message", type: "VARCHAR(255)")
        }
	}
}