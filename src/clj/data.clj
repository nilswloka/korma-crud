(ns data
  (:use korma.db)
  (:use korma.core))

(defdb my-database (postgres {:db "kcrud"
                              :user "kcrud"
                              :password "kcrud"
                              :host "localhost"
                              :port 5432}))
