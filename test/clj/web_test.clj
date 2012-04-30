(ns web-test
  (:require [ring.adapter.jetty :as jetty])
  (:require [compojure.handler :as handler])
  (:use korma.db)
  (:use korma.core)
  (:use crud)
  (:use data))

(defentity things
  (table :things)
  (entity-fields :id :name :description :size)
  (entity-field-types {:id Integer :name String :description String :size Integer})
  (database my-database))

(def all-things (select* things))

(defcrud test-routes "/things" all-things)

(def handler (handler/site test-routes))



