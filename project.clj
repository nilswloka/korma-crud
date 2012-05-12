(defproject korma-crud "0.0.1-SNAPSHOT"
  :description "Create CRUD views for Korma-queries"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [korma "0.3.0-beta7"]
                 [postgresql "8.4-702.jdbc4"]
                 [compojure "1.0.3"]
                 [ring "1.1.0"]
                 [hiccup "1.0.0"]]
  :plugins [[lein-cucumber "1.0.0"]
            [lein-swank "1.4.2"]
            [lein-midje "2.0.0-SNAPSHOT"]]
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :profiles {:dev {:dependencies [[midje "1.4.0-beta1"]]}})
