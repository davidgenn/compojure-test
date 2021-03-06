(defproject test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.2"]
                 [ring/ring-defaults "0.1.2"]
                 [ring/ring-core "1.3.2"]
                 [ring/ring-json "0.3.1"]
                 [ring/ring-defaults "0.1.4"]
                 [clj-http "1.0.1"]
                 [com.taoensso/timbre "3.4.0"]
                 [clj-jgit "0.8.3"]]
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler test.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
