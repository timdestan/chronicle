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

(defn get-track-name-identifier
  "Get something we can use to 'uniquely' identify a track."
  [track]
  ; Use the MusicBrainz id if it's there, otherwise fall back to the name
  (if (:mbid track)
      (:mbid track)
      (:name track)))

(defn -main [] 
  (->> "data/tj6186.edn"
       load-tracks
       (take 10)
       (map get-track-name-identifier)
       (map println)
       doall))

(comment ;; Use this to re-get all the tracks.
  (let [api-key (lastfm/load-api-key "lastfm/credentials.json")]
       (lastfm/import-all-tracks "tj6186" api-key "resources/data/tj6186.edn")))