(ns chronicle.core
  (:require [chronicle.lastfm :as lastfm]
            [chronicle.json :as json]
            [clojure.java.io :as io]
            [clojure.string :refer [join]])
  (:gen-class))

;; Use my username for testing.
(def user-name "tj6186")

(defn file-path-for-user
  "File path to store the user's data. Relative to the resources/ folder."
  [user-name]
  (str "data/" user-name ".json"))

(def user-data-path (file-path-for-user user-name))

(defn load-tracks
  "Load saved tracks from the given resource."
  [path]
  (-> path
      io/resource
      slurp
      json/from-string))

(defn histogram
  "Makes a histogram of the given values. The keys are the observations
   themselves. The values are the counts."
  [observations]
  (->> observations
       frequencies
       (sort-by val >)))

(defn build-top-tracks-histogram
  [tracks]
  (->> tracks
       (map #(select-keys % [:artist :name :mbid]))
       histogram))

(defn build-top-artists-histogram
  [tracks]
  (->> tracks
       (map :artist)
       histogram))

(defn format-artist
  [artist]
  (:#text artist))

(defn format-track
  [track]
  (str
    (format-artist (:artist track))
    " - "
    (:name track)))

(defn formatted-histogram
  "Creates a formatted histogram.
  * histogram: A list of key-count pairs in decreasing order by count.
  * formatter: Function to format each observation into a string."
  [formatter histogram]
  (map #(str (formatter (key %))
             ", Count: "
             (val %))
       histogram))

(defn format-top-tracks
  [tracks]
  (let [top-tracks (build-top-tracks-histogram tracks)]
    (formatted-histogram format-track top-tracks)))

(defn format-top-artists
  [tracks]
  (let [top-artists (build-top-artists-histogram tracks)]
    (formatted-histogram format-artist top-artists)))

(defn count-unique-artists
  [tracks]
  (->> tracks (map :artist) set count))

(defn pull-all-data-from-lastfm []
  (let [api-key (lastfm/load-api-key "lastfm/credentials.json")]
    (lastfm/import-all-tracks
      user-name api-key (str "resources/" user-data-path))))

(defn sanity-checks []
  "Print out some stats that Last.fm shows so we can compare to their
   website as a test."
  (let [tracks (load-tracks user-data-path)]
    (dorun
      [(println "Number of scrobbles:")
       (println (count tracks))
       (println)
       (println "Number of artists:")
       (println (count-unique-artists tracks))
       (println)
       (println "Top Artists:")
       (doall (map println (take 50 (format-top-artists tracks))))
       (println)
       (println "Top Tracks:")
       (doall (map println (take 50 (format-top-tracks tracks))))])))

(defn -main [] (sanity-checks))
