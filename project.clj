(defproject chronicle "0.1.0-SNAPSHOT"
  :description "explore your music listening history"
  :url "http://github.com/timdestan/chronicle"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.6"]
                 [clj-http "1.1.0"]
                 [expectations "2.0.9"]]
  :main ^:skip-aot chronicle.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :plugins [[lein-autoexpect "1.4.2"]])
