(ns chronicle.lastfm
  (:require [clj-http.client :as client])
  (:gen-class))

(defn request
  [params]
  (let
    [api-key (:api-key params)
     user-name (:user-name params)
     method (:method params)
     response
       (client/get
         "http://ws.audioscrobbler.com/2.0/"
        {:query-params
         {:api_key api-key
          :format "json"
          :user user-name
          :method "user.getInfo"}
         :as :json})]
    (:body response)))

(def method-names
  {
    :user-info "User.getInfo"
  })

(defn retrieve
  "Sends a request to the Last.FM JSON API and
   returns the response body."
  [method-key user-name api-key]
  (request
   {:api-key api-key
    :method (method-key method-names)
    :user-name user-name }))
