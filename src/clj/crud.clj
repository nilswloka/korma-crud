(ns crud
  (:use crud.core)
  (:use compojure.core))

(defn entity-field-types
  [ent type-map]
  (assoc ent :field-types type-map))

(defmacro defcrud [name base-url query]
  `(defroutes ~name
     ;; Views
     (GET ~base-url [] (list-view ~base-url ~query))
     (GET (str ~base-url "/new") [] (create-view ~base-url ~query))
     (GET (str ~base-url "/:id") {{id# :id} :params} (detail-view ~base-url ~query id#))
     ;; Operations
     (PUT    ~base-url {params# :params} (create-entity ~base-url ~query params#))
     (POST   (str ~base-url "/:id") {{id# :id :as params#} :params} (update-entity ~base-url ~query id# params#))
     (DELETE (str ~base-url "/:id") {{id# :id} :params} (delete-entity ~base-url ~query id#))))
