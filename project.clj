(defproject korma-crud "0.0.1-SNAPSHOT"
  :description "Create CRUD views for Korma-queries"
  :dependencies [[korma "0.3.0-beta7"]
                 [postgresql "8.4-702.jdbc4"]]
  :plugins [[lein-cucumber "1.0.0"]]
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :profiles {:dev {:dependencies [[midje "1.4.0-beta1"]]}})
