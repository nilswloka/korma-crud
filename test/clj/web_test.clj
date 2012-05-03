(ns web-test
  (:require [compojure.handler :as handler])
  (:use korma.db)
  (:use korma.core)
  (:use crud)
  (:use data-test))

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
  (defonce server (run-jetty #'handler {:port 9090 :join? false})))