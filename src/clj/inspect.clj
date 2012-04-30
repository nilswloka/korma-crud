(ns inspect)

(defn field-without-table [table field]
  (let [field-without-quotes (.replaceAll field "\"" "")
        field-name (subs field-without-quotes (inc (count table)))]
    (keyword field-name)))

(defn fields-as-keywords [{:keys [table fields]}]
  (let [fields-without-table (map (partial field-without-table table) fields)]
    (into #{} fields-without-table)))

(defn inspect-fields [query]
  (if-let [selected-fields (-> query :options :fields)]
    (into #{} selected-fields)
    (-> query :ent fields-as-keywords)))

