(ns web-test
  (:require [compojure.handler :as handler])
  (:use crud)
  (:use data-test)
  (:use korma.db)
  (:use korma.core))

(defentity things
  (table :things)
  (entity-fields :id :name :description :size)
  (entity-field-types {:id Integer :name String :description String :size Integer})
  (database test-database))

(def all-things (select* things))

(defcrud test-routes "/things" all-things)

(def handler (handler/site test-routes))


(comment
  (use 'ring.adapter.jetty)
  (use 'web-test)
  (def server (run-jetty #'handler {:port 9090 :join? false})))