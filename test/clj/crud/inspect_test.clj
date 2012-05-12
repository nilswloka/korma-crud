(ns crud.inspect-test
  (:use midje.sweet)
  (:use korma.core)
  (:use korma.db)
  (:use crud.inspect)
  (:use [data-test :only [test-database]])
  (:use [crud :only [entity-field-types]]))

(defentity things
  (table :things)
  (entity-fields :id :name :description :size)
  (entity-field-types {:id Integer :name String :description String :size Integer})
  (database test-database))

(fact "inspect-fields should show all fields of entity in unqualified query"
  (inspect-fields {:ent ...ENTITY...}) => #{:id, :name, :description, :size}
  (provided
    (fields-as-keywords ...ENTITY...) => #{:id, :name, :description, :size}))

(fact "inspect-fields should show queried fields of entity"
  (inspect-fields {:options {:fields '(...FIELD1... ...FIELD2...)}}) => (just #{...FIELD1... ...FIELD2...}))

(fact "fields-as-keywords returns fields without schema converted into keywords"
  (fields-as-keywords {:table ...TABLE..., :fields '(...FIELD...)}) => #{:id}
  (provided
    (field-without-table ...TABLE... ...FIELD...) => :id))

(fact "field-without-table removes table name and converts to keyword"
  (field-without-table "TEST" "\"TEST\".\"id\"") => :id)

(fact "entity-name returns name from query"
  (entity-name {:ent {:name ...NAME...}}) => ...NAME...)

(fact "pk-name returns the name of the primary key from the query"
  (pk-name {:ent {:pk ...PK...}}) => ...PK...)

(fact "pk-type returns the type of the primary key from the query"
  (pk-type {:ent {:pk :k :field-types {:k ...PK-TYPE...}}}) => ...PK-TYPE...)

(fact "field-types returns the type map from the query"
  (field-types {:ent {:field-types ...TYPES...}}) => ...TYPES...)
