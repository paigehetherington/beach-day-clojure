(ns beach-day-clojure.core
  (:require [ring.adapter.jetty :as j]
            [compojure.core :as c]
            [hiccup.core :as h]
            [ring.middleware.params :as p]
            [ring.util.response :as r])
  (:gen-class))

(defn -main []
  (println "Hello, World!"))
