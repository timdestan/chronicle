(ns chronicle.core
  (:require [clj-http.client :as client]
            [clojure.data.json :as json])
  (:gen-class))

(defn read-json
  "Reads a json object using keywords for keys"
  [json-str]
  (json/read-str json-str :key-fn keyword))

(defn load-api-key
  "Load an API key at the specified file path"
  [path]
  (let [credentials (slurp path)]
    (:apiKey (read-json credentials))))

(defn parse-user-info
  "Parse user info from Last.fm"
  [json]
  (let [response (read-json json)
        user (:user response)]
    {:name (:name user)
     :playcount (Integer/parseInt (:playcount user))}))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
