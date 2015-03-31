(ns chronicle.core-test
  (:use expectations)
  (:require [chronicle.core :refer :all]
            [clojure.data.json :as json]
            [clojure.java.io :refer [resource]]))

(expect "imakey"
  (-> "resources/lastfm/credentials.sample.json"
      slurp
      parse-api-key))

(expect {:name "RJ" :playcount 105928}
  (-> "resources/lastfm/user.getInfo.json"
      slurp
      read-json
      parse-user-info))
