(ns chronicle.core
  (:require [clj-http.client :as client]
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

(def last-fm-base-url "http://ws.audioscrobbler.com/2.0/")

(defn parse-user-info
  "Parse user info from Last.fm"
  [json]
  (let [result (:user json)
        name (:name result)
        play-count (Integer/parseInt (:playcount result))]
    {:name name :playcount play-count}))

(defn get-user-info-response
  [user-name api-key]
  (let [response
        (client/get last-fm-base-url
                    {:query-params
                      {
                       :api_key api-key
                       :format "json"
                       :user user-name
                       :method "user.getInfo"
                      }
                     :as :json})]
    (:body response)))

(defn get-user-info
  "Gets Last.fm user info for a user name"
  [user-name api-key]
  (parse-user-info (get-user-info-response user-name api-key)))

(defn -main []
  (let [path "lastfm/credentials.json"
        credentials (slurp (resource path))
        api-key (parse-api-key credentials)
        user (get-user-info "tj6186" api-key)]
  (println user)))
