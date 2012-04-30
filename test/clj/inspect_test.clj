(ns inspect-test
  (:use midje.sweet)
  (:use korma.core)
  (:use korma.db)
  (:use inspect)
  (:use [data :only [my-database]]))

(defentity things
  (table :things)
  (entity-fields :id :name :description :size)
  (database my-database))

(fact "inspect-fields should show all fields of entity in unqualified query"
  (inspect-fields {:ent ...ENTITY...}) => #{:id, :name, :description, :size}
  (provided
    (fields-as-keywords ...ENTITY...) => #{:id, :name, :description, :size}))

;.;. Simplicity, carried to the extreme, becomes elegance. -- Jon Franklin
(fact "inspect-fields should show queried fields of entity"
  (inspect-fields {:options {:fields '(...FIELD1... ...FIELD2...)}}) => (just #{...FIELD1... ...FIELD2...}))

(fact "fields-as-keywords returns fields without schema converted into keywords"
  (fields-as-keywords {:table ...TABLE..., :fields '(...FIELD...)}) => #{:id}
  (provided
    (field-without-table ...TABLE... ...FIELD...) => :id))

(fact "field-without-table removes table name and converts to keyword"
  (field-without-table "TEST" "\"TEST\".\"id\"") => :id)
