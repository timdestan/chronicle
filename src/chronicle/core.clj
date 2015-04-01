(ns chronicle.core
  (:require [chronicle.lastfm :as lastfm]
            [clojure.data.json :as json]
            [clojure.java.io :refer [resource]])
  (:gen-class))

(defn read-json
  "Reads a json object using keywords for keys"
  [json-str]
  (json/read-str json-str :key-fn keyword))

(defn parse-api-key [json]
  (-> json
      read-json
      :apiKey))

(defn load-api-key [path]
  (-> resource
      slurp
      parse-api-key))

(defn parse-user-info
  "Parse user info from Last.fm"
  [json]
  (let [result (:user json)
        name (:name result)
        play-count (Integer/parseInt (:playcount result))]
    {:name name :playcount play-count}))

(defn prune-track
  [track]
  (select-keys track [:name :mbid :artist :album :date]))

(defn write-tracks
  [filename tracks]
  (spit filename
        (pr-str (apply list tracks))))

(defn -main []
  (->> "lastfm/credentials.json"
       resource
       slurp
       parse-api-key
       (lastfm/import-all-tracks "tj6186")
       (map prune-track)
       (write-tracks "resources/data/tj6186.edn")))
