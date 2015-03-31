(ns chronicle.core-test
  (:use expectations)
  (:require [chronicle.core :refer :all]))

(expect "imakey"
        (load-api-key "resources/lastfm/credentials.sample.json"))

(expect {:name "RJ" :playcount 105928}
        (parse-user-info (slurp "resources/lastfm/user.getInfo.json")))
