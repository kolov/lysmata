(ns lysmata.core
  (:require
    [compojure.core :refer (GET defroutes)]
    [compojure.handler :as handler]
    [ring.util.response :as resp]
    [ring.middleware.reload :refer [wrap-reload]]
    [org.httpkit.server :refer :all]
    [org.httpkit.timer :refer :all]
    [clojure.java.io :as io]
    [lysmata.mpeg :as m])
  )

(def clients (atom #{}))

(defroutes routes
           (GET "/" [] (io/resource "public/index.html"))
           (GET "/pic" req
                (with-channel req channel
                              (on-close channel (fn [status] (println "channel closed, " status)))
                              (m/stream channel "/Users/assen/me.jpg")))
           )


(def app (-> routes
             (wrap-reload)
             handler/site))

(defonce stop-fn (atom nil))

(defn boot-server []
  (when @stop-fn (println "Stopping runing server") (@stop-fn))
  (reset! stop-fn (run-server #'app {:port 3000 :join? false}))
  (println "Started server, stop with (stop-server)"))

(defn stop-server [] (@stop-fn) (reset! stop-fn nil))

