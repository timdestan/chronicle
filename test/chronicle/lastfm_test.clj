(ns chronicle.lastfm-test
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

(expect [{:a 1} {:b 2}]
        (json/from-string (json/to-string [{:a 1} {:b 2}])))
