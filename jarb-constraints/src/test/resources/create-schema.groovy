databaseChangeLog() {

	changeSet(author: "jeroen@42.nl", id: "1") {
		comment("Create meta-data testing tables.")

		createTable(tableName: "cars") {
			column(name: "id", type: "bigint", autoIncrement: true) {
				constraints(
					nullable: false,
					primaryKey: true,
					primaryKeyName: "pk_cars_id"
				)
			}
			column(name: "license_number", type: "varchar(6)") { constraints(nullable: false) }
			column(name: "price", type: "decimal(6,2)")
			column(name: "owner_id", type: "bigint")
			column(name: "active", type: "boolean")
		}
		
		addUniqueConstraint(tableName: "cars", columnNames: "license_number", constraintName: "uk_cars_license_number")

		createTable(tableName: "persons") {
			column(name: "id", type: "bigint", autoIncrement: true) {
				constraints(
					nullable: false,
					primaryKey: true,
					primaryKeyName: "pk_persons_id"
				)
			}
			column(name: "name", type: "varchar(255)") { constraints(nullable: false) }
			column(name: "age", type: "bigint")
			column(name: "street_and_number", type: "varchar(255)") { constraints(nullable: false) }
			column(name: "city", type: "varchar(255)") { constraints(nullable: false) }
		}
		
		addForeignKeyConstraint(constraintName: "fk_cars_owner", baseTableName: "cars", baseColumnNames: "owner_id", referencedTableName: "persons", referencedColumnNames: "id")
		
	}
	
	changeSet(author: "jeroen@42.nl", id: "2") {
		comment("Create validation testing tables.")
		
		createTable(tableName: "wines") {
			column(name: "id", type: "bigint", autoIncrement: true) {
				constraints(
					nullable: false,
					primaryKey: true,
					primaryKeyName: "pk_wines_id"
				)
			}
			column(name: "name", type: "varchar(6)") { constraints(nullable: false) }
			column(name: "price", type: "decimal(6,2)")
			column(name: "country_id", type: "bigint")
		}
		
		createTable(tableName: "countries") {
			column(name: "id", type: "bigint", autoIncrement: true) {
				constraints(
					nullable: false,
					primaryKey: true,
					primaryKeyName: "pk_countries_id"
				)
			}
			column(name: "name", type: "varchar(255)") { constraints(nullable: false) }
		}
	}

}
