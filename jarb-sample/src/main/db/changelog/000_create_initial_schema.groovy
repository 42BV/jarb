databaseChangeLog() {
	changeSet(author: "jeroen@42.nl", id: "1") {
		comment("Create initial database schema.")
	}
}