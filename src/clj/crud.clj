(ns crud
  (:use compojure.core)
  (:use [hiccup page core element form])
  (:use [korma.core :exclude [create-entity]])
  (:use inspect)
  (:require [ring.util.response :as response]))

;;
;; Conversion functions
;;
(defmulti as-value (fn [value type] type) :default :default)

(defmethod as-value Integer [value type]
  (Integer. value))

(defmethod as-value :default [value type]
  value)

;;
;; Korma extension
;;
(defn entity-field-types
  [ent type-map]
  (assoc ent :field-types type-map))

;;
;; Views / Pages
;;
(defn table-header [fields]
  [:tr
   (map #(vector :th (name %)) fields)])

(defn table-row [base-url fields pk entity]
  [:tr
   (map #(vector :td (link-to (str base-url "/" (pk entity)) (% entity))) fields)])

(defn table-data [base-url fields pk entities]
  (map (partial table-row base-url fields pk) entities))

(defn list-view [base-url query]
  (let [view-name (-> query :ent :name)
        all-entities (select query)
        all-fields (inspect-fields query)
        pk (-> query :ent :pk)]
    (html5 [:h1 (str view-name "-list")]
           [:table
            (table-header all-fields)
            (table-data base-url all-fields pk all-entities)]
           [:p (link-to (str base-url "/new") (str "Create " view-name))])))

(defn form-field [[key value]]
  (list (label (name key) (name key))
        (text-field (name key) value)
        [:br]))

(defn detail-view [base-url query id]
  (let [view-name (-> query :ent :name)
        entity-symbol (-> query :ent :name)
        primary-key (-> query :ent :pk)
        primary-key-type (-> query :ent :field-types primary-key)
        entity (first (select entity-symbol (where {(-> query :ent :pk) (as-value id primary-key-type)})))]
    (html5 [:h1 (str view-name "-details")]
           (form-to [:post (str base-url "/" id)]
                    (map form-field entity)
                    (submit-button "Update"))
           (form-to [:delete (str base-url "/" id)]
                    (submit-button "Delete"))
           (link-to base-url "Back"))))

(defn update-entity [base-url query id params]
  (let [entity-types (-> query :ent :field-types)
        key-value (into {} (map (fn [[key text]] [key (as-value text (key entity-types))]) params))
        primary-key (-> query :ent :pk)
        primary-key-type (-> query :ent :field-types primary-key)
        primary-key-value (as-value id primary-key-type)
        entity-symbol (-> query :ent :name)]
    (update entity-symbol
          (set-fields key-value)
          (where {primary-key primary-key-value}))
    (response/redirect base-url)))

(defn empty-map [keys]
  (reduce #(merge %1 {%2 nil}) {} keys))

(defn create-view [base-url query]
  (let [view-name (-> query :ent :name)
        all-fields (empty-map (inspect-fields query))]
    (html5 [:h1 (str view-name "-create")]
           (form-to [:put base-url]
                    (map form-field all-fields)
                    (submit-button "Create")
                    (link-to base-url "Back")))))

(defn create-entity [base-url query params]
  (let [entity-types (-> query :ent :field-types)
        all-fields (inspect-fields query)
        all-params (select-keys params all-fields)
        key-value (into {} (map (fn [[key text]] [key (as-value text (key entity-types))]) all-params))
        entity-symbol (-> query :ent :name)]
    (insert entity-symbol
            (values key-value))
    (response/redirect base-url)))

(defn delete-entity [base-url query id]
  (let [entity-symbol (-> query :ent :name)
        primary-key (-> query :ent :pk)
        primary-key-type (-> query :ent :field-types primary-key)
        primary-key-value (as-value id primary-key-type)]
    (delete entity-symbol
            (where {primary-key primary-key-value}))
    (response/redirect base-url)))

;;
;; CRUD
;;
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
