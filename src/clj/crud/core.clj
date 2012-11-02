(ns crud.core
  (:use crud.inspect)
  (:use [hiccup page core element form])
  (:use [korma.core :exclude [create-entity]])
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
;; Views
;;
(defn empty-map [keys]
  (into {} (map #(vector % nil) keys)))

(defn pk-clause [query id]
  (let [primary-key (pk-name query)
        primary-key-value (as-value id (pk-type query))]
    {primary-key primary-key-value}))

(defn table-header [fields]
  [:tr
   (map #(vector :th (name %)) fields)])

(defn table-row [base-url fields pk entity]
  [:tr
   (map #(vector :td (link-to (str base-url "/" (pk entity)) (% entity))) fields)])

(defn table-data [base-url fields pk entities]
  (map (partial table-row base-url fields pk) entities))

(defn form-field [[key value]]
  (list (label (name key) (name key))
        (text-field (name key) value)
        [:br]))

(defn list-view [base-url query]
  (let [entity-name  (entity-name query)
        pk-name      (pk-name query)
        all-fields   (inspect-fields query)
        all-entities (select query)]
    (html5 [:h1 (str entity-name "-list")]
           [:table
            (table-header all-fields)
            (table-data base-url all-fields pk-name all-entities)]
           [:p (link-to (str base-url "/new") (str "Create " entity-name))])))

(defn detail-view [base-url query id]
  (let [entity-name (entity-name query)
        query       (select entity-name (where (pk-clause query id)))
        entity      (first query)]
    (html5 [:h1 (str entity-name "-details")]
           (form-to [:post (str base-url "/" id)]
                    (map form-field entity)
                    (submit-button "Update"))
           (form-to [:delete (str base-url "/" id)]
                    (submit-button "Delete"))
           (link-to base-url "Back"))))

(defn create-view [base-url query]
  (let [entity-name (entity-name query)
        all-fields  (empty-map (inspect-fields query))]
    (html5 [:h1 (str entity-name "-create")]
           (form-to [:put base-url]
                    (map form-field all-fields)
                    (submit-button "Create")
                    (link-to base-url "Back")))))

;;
;; Entity functions
;;

(defn convert-to-value [query params]
  (let [field-types (field-types query)
        convert     (fn [[key text]] [key (as-value text (field-types key))])]
    (into {} (map convert params))))

(defn create-entity [base-url query params]
  (let [entity-name (entity-name query)
        all-fields  (inspect-fields query)
        all-params  (select-keys params all-fields)
        key-value   (convert-to-value query all-params)]
    (insert entity-name
            (values key-value))
    (response/redirect base-url)))

(defn update-entity [base-url query id params]
  (let [entity-name (entity-name query)
        key-value   (convert-to-value query params)
        pk-clause   (pk-clause query id)]
    (update entity-name
          (set-fields key-value)
          (where pk-clause))
    (response/redirect base-url)))

(defn delete-entity [base-url query id]
  (let [entity-name (entity-name query)
        pk-clause   (pk-clause query id)]
    (delete entity-name
            (where pk-clause))
    (response/redirect base-url)))
