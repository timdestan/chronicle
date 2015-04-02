(ns chronicle.json
  (:require [clojure.data.json :as json]
            [clojure.java.io :refer [resource]])
  (:gen-class))

(defn read-json
  "Reads a json object using keywords for keys"
  [json-str]
  (json/read-str json-str :key-fn keyword))

(defn read-json-resource
  "Reads a json resource into memory"
  [resource-path]
  (-> resource-path
      resource
      slurp
      read-json))
