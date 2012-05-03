(ns data-test
  (:use korma.db)
  (:use korma.core))

(defdb test-database (postgres {:db "kcrud"
                                :user "kcrud"
                                :password "kcrud"
                                :host "localhost"
                                :port 5432}))
