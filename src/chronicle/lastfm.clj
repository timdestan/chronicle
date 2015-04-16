(ns chronicle.lastfm
  (:require [clj-http.client :as client]
            [chronicle.json :as json])
  (:gen-class))

(defn load-api-key
  [path]
  (-> path
      json/read-json-resource
      :apiKey))

(defn execute-query
  "Executes a single query against the Last.FM API"
  [query-params]
  (:body
   (client/get
    "http://ws.audioscrobbler.com/2.0/"
    {:query-params query-params
     :as :json})))

(defn basic-query-params
  [user-name api-key, method]
  {
    :api_key api-key
    :format "json"
    :user user-name
    :method method
   })

(defn get-user-info
  [user-name api-key]
  (execute-query (basic-query-params user-name api-key "user.getInfo")))

;; Their limit, not mine
(def max-tracks-per-page 200)

(defn pages-needed-for
  "Number of requests it will take to get all a user's tracks"
  [track-count]
  (let [divides-evenly (= (mod track-count max-tracks-per-page) 0)
        quotient (quot track-count max-tracks-per-page)]
    (if divides-evenly quotient (+ quotient 1))))

(defn one-to-n
  [n]
  "Numbers from 1 to N"
  (map inc (take n (range))))

(defn parse-user-info
  "Parse user info from Last.fm"
  [json]
  (let [result (:user json)
        name (:name result)
        play-count (Integer/parseInt (:playcount result))]
    {:name name :playcount play-count}))

(defn get-page-numbers
  "Gets page numbers needed to retrieve all the user's tracks"
  [user-name api-key]
  (->> (get-user-info user-name api-key)
       parse-user-info
       :playcount
       pages-needed-for
       one-to-n))

(defn get-one-page-of-tracks
  [user-name api-key page-number]
  (let [page-params {:page page-number :limit max-tracks-per-page}
        all-params (merge (basic-query-params user-name api-key "user.getRecentTracks") page-params)]
    (execute-query all-params)))

(defn prune-track
  [track]
  (select-keys track [:name :mbid :artist :album :date]))

(defn write-tracks
  [filename tracks]
  (spit filename
        (json/to-string (apply list tracks))))

(defn import-all-tracks
  "Imports all the tracks for the given user and writes them as json to the
   provided output file name."
  [user-name api-key out-file-name]
  (let [page-numbers (get-page-numbers user-name api-key)
        results (map #(get-one-page-of-tracks user-name api-key %) page-numbers)]
    (write-tracks
      out-file-name
      (map prune-track (mapcat :track (map :recenttracks results))))))
