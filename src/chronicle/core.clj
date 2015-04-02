(ns chronicle.core
  (:require [chronicle.lastfm :as lastfm])
  (:gen-class))

(defn write-tracks
  [filename tracks]
  (spit filename
        (pr-str (apply list tracks))))

(defn -main []
  (->> "lastfm/credentials.json"
       lastfm/load-api-key
       (lastfm/import-all-tracks "tj6186")
       (write-tracks "resources/data/tj6186.edn")))
