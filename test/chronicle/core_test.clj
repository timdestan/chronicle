(ns chronicle.core-test
  (:require [expectations :refer :all]
            [chronicle.json :as json]
            [chronicle.lastfm :as lastfm]))

(expect "imakey"
  (-> "lastfm/credentials.sample.json"
      lastfm/load-api-key))

(expect {:name "RJ" :playcount 105928}
  (-> "lastfm/user.getInfo.json"
      json/read-json-resource
      lastfm/parse-user-info))
