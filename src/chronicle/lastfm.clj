(ns chronicle.lastfm
  (:require [clj-http.client :as client])
  (:gen-class))

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

(defn get-page-numbers
  "Gets page numbers needed to retrieve all the user's tracks"
  [user-name api-key]
  (->> (get-user-info user-name api-key)
       :user
       :playcount
       Integer/parseInt
       pages-needed-for
       one-to-n))

(defn get-one-page-of-tracks
  [user-name api-key page-number]
  (let [page-params {:page page-number :limit max-tracks-per-page}
        all-params (merge (basic-query-params user-name api-key "user.getRecentTracks") page-params)]
    (execute-query all-params)))

(defn import-all-tracks
  [user-name api-key]
  (let [page-numbers (get-page-numbers user-name api-key)
        results (map #(get-one-page-of-tracks user-name api-key %) page-numbers)]
    (mapcat :track (map :recenttracks results))))

