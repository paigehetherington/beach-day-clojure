(ns beach-day-clojure.core
  (:require [ring.adapter.jetty :as j]
            [compojure.core :as c]
            [hiccup.core :as h]
            [ring.middleware.params :as p]
            [ring.util.response :as r])
  (:gen-class))

(defonce server (atom nil))

(defonce surfers (atom {})) ; HM

(c/defroutes app
  (c/GET "/" request
    (h/html [:html
             [:body
              [:form {:action "/add-surfer" :method "post"}
                [:input {:type "text" :placeholder "Name" :name "name" :width "200"}]
                [:select {:name "board-type"}
                  [:option {:disabled "disabled" :selected "selected"} "Board Type"]
                  [:option {:value "Shortboard"} "Shortboard"]
                  [:option {:value "Longboard"} "Longboard"]
                  [:option {:value "Funboard"} "Funboard"]]
                [:input {:type "text" :placeholder "Country" :name "country"}]
                [:input {:type "text" :placeholder "Home Break" :name "home-break"}]
                [:select {:name "ability-level"}
                  [:option {:disabled "disabled" :selected "selected"} "Ability Level"]
                  [:option {:value "Beginner"} "Beginner"]
                  [:option {:value "Intermediate"} "Intermediate"]
                  [:option {:value "Advanced"} "Advanced"]]
                [:input {:type "number" :placeholder "Number of Waves" :name "number-of-waves"}]
                [:button {:type "Submit"} "Add surfer"]]
              [:ol
               (map (fn [surfer]
                      [:li (:name surfer) " " (:board-type surfer) " " (:country surfer) " " (:home-break surfer) " " (:ability-level surfer) " " (:number-of-waves surfer)])
                  (vals @surfers))]]]))

  (c/POST "/add-surfer" request
    (let [name (get (:params request) "name")
          board-type (get (:params request) "board-type")
          country (get (:params request) "country")
          home-break (get (:params request) "home-break")
          number-of-waves (get (:params request) "number-of-waves")
          ability-level (get (:params request) "ability-level")
          surfer (hash-map :name name :board-type board-type :country country :home-break home-break :ability-level ability-level :number-of-waves number-of-waves)]
      (swap! surfers assoc name surfer) ; surfers is HM
      (r/redirect "/")))

  (c/GET "/get-surfer" request
    (let [name (get (:params request "name"))
          surfer (get @surfers name)])))

(defn -main []
  (when @server
    (.stop @server))
  (reset! server (j/run-jetty (p/wrap-params app) {:port 3000 :join? false})))