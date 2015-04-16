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

(defn top-tracks
  [tracks]
  (->> tracks
       (map #(select-keys % [:artist :name :mbid]))
       histogram))

(defn top-artists
  [tracks]
  (->> tracks
       (map :artist tracks)
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
  * format-observation: Function to format each observation into a string."
  [format-observation histogram]
  (map #(str (format-observation (key %))
             ", Count: "
             (val %))
       histogram))

(defn load-and-format-histogram
  [build-histogram format-observation]
  (->> user-data-path
       load-tracks
       build-histogram
       (formatted-histogram format-observation)))

(defn format-top-tracks []
  (load-and-format-histogram top-tracks format-track))

(defn format-top-artists []
  (load-and-format-histogram top-artists format-artist))

(defn pull-all-data-from-lastfm []
  (let [api-key (lastfm/load-api-key "lastfm/credentials.json")]
       (lastfm/import-all-tracks user-name api-key (str "resources/" user-data-path))))

(defn print-some-stuff []
  (dorun
   [(println "Top Artists:")
    (doall (map println (take 50 (format-top-artists))))
    (println "\nTop Tracks:")
    (doall (map println (take 50 (format-top-tracks))))]))

(defn -main [] (print-some-stuff))
