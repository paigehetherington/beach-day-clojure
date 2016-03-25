(ns beach-day-clojure.core
  (:require [ring.adapter.jetty :as j]
            [compojure.core :as c]
            [hiccup.core :as h]
            [ring.middleware.params :as p]
            ;[clojure.pprint :as pp]
            [ring.util.response :as r])
  (:gen-class))

(defonce server (atom nil))

(defonce surfers (atom {})) ; HM

(add-watch surfers :save-to-disk
  (fn [_ _ _ _]
    (spit "surfers.edn" (pr-str @surfers))))

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
                  [:option {:value "Funboard"} "Funboard"]
                  [:option {:value "Fish"} "Fish"]]
                [:input {:type "text" :placeholder "Country" :name "country"}]
                [:input {:type "text" :placeholder "Home Break" :name "home-break"}]
                [:select {:name "ability-level"}
                  [:option {:disabled "disabled" :selected "selected"} "Ability Level"]
                  [:option {:value "Beginner"} "Beginner"]
                  [:option {:value "Intermediate"} "Intermediate"]
                  [:option {:value "Advanced"} "Advanced"]]
                [:input {:type "number" :placeholder "Number of Waves" :name "number-of-waves"}]
                [:input {:type "text" :placeholder "Image URL" :name "image-url"}]
                [:button {:type "Submit"} "Add surfer"]]
              [:form {:action "/get-surfer" :method "get"}
                [:input {:type "text" :placeholder "Enter Name" :name "name"}]
                [:button {:type "submit"} "Search"]]
              [:table
               [:tr 
                [:th "Name"]
                [:th "Board Type"]
                [:th "Country"]
                [:th "Home Break"]
                [:th "Ability Level"]
                [:th "Number of Waves"]
                [:th "Photo"]]
                
               (map (fn [surfer]
                      [:tr
                       [:td (:name surfer)]
                       [:td (:board-type surfer)] 
                       [:td (:country surfer)] 
                       [:td (:home-break surfer)] 
                       [:td (:ability-level surfer)] 
                       [:td (:number-of-waves surfer)]
                       [:td [:img {:src (:image-url surfer) :width 100 :height 100}]]])
                  (vals @surfers))]]]))

  (c/POST "/add-surfer" request
    (let [name (get (:params request) "name")
          board-type (get (:params request) "board-type")
          country (get (:params request) "country")
          home-break (get (:params request) "home-break")
          number-of-waves (get (:params request) "number-of-waves")
          ability-level (get (:params request) "ability-level")
          image-url (get (:params request) "image-url")
          surfer (hash-map :name name :board-type board-type :country country :home-break home-break :ability-level ability-level :number-of-waves number-of-waves :image-url image-url)]
      (swap! surfers assoc name surfer) ; surfers is HM
      (r/redirect "/")))

  (c/GET "/get-surfer" request
    (let [name (get (:params request) "name")
          surfer (get @surfers name)]
      (h/html
        [:html
         [:body
          [:br] 
          (:name surfer)
          [:br] 
          (:board-type surfer) 
          [:br] 
          (:country surfer) 
          [:br] 
          (:home-break surfer) 
          [:br] 
          (:ability-level surfer) 
          [:br] 
          (:number-of-waves surfer)
          [:br] 
          [:img {:src (:image-url surfer) :width 100 :height 100}]]]))))
          

(defn -main []
    (try
      (let [surfers-str (slurp "surfers.edn")
            surfers-vec (read-string surfers-str)]
         (reset! surfers surfers-vec))
     (catch Exception _))
  (when @server
    (.stop @server))
  (reset! server (j/run-jetty (p/wrap-params app) {:port 3000 :join? false})))