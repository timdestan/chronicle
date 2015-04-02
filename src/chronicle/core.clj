(ns chronicle.core
  (:require [chronicle.lastfm :as lastfm]
            [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:gen-class))

(defn load-tracks
  "Load saved tracks from disk"
  [path]
  (-> (io/resource path)
      slurp
      edn/read-string))


(defn most-played-tracks
  [tracks]
  (->> tracks
       (map #(select-keys % [:artist :name :mbid]))
       frequencies
       (sort-by val >)))

(defn -main [] 
  (->> "data/tj6186.edn"
       load-tracks
       most-played-tracks
       (take 15)
       (map
        #(str
          (:#text (:artist (key %)))
          " - "
          (:name (key %))
          ", Play Count: "
          (val %)))
       (map println)
       doall))

(comment ;; Use this to re-get all the tracks.
  (let [api-key (lastfm/load-api-key "lastfm/credentials.json")]
       (lastfm/import-all-tracks "tj6186" api-key "resources/data/tj6186.edn")))