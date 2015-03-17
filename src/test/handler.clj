(ns test.handler
  (:require [compojure.route :as route]
            [compojure.core :refer [GET defroutes]]
            [ring.util.response :refer [resource-response response]]
            [ring.middleware.json :as middleware]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [clj-http.client :as client]
            [taoensso.timbre :as timbre
             :refer (log  trace  debug  info  warn  error  fatal  report
                          logf tracef debugf infof warnf errorf fatalf reportf
                          spy logged-future with-log-level with-logging-config
                          sometimes)]))

(def cm (clj-http.conn-mgr/make-reusable-conn-manager {:timeout 1 :threads 3}))

(defn build-response [params urlparam inboundheaders]
  (info (str "Request to /content" params urlparam inboundheaders))
  (info (str "HEADERS: " inboundheaders) )
  (info (str "ACCEPT_ENCODING: " (get inboundheaders "accept-encoding")) )
  (let [acceptEncoding (get inboundheaders "accept-encoding")]
    (info acceptEncoding)
    (response
      (conj
        (client/get "http://www.ig.com/uk/ig-indices/ftse-100?json=true" {:connection-manager cm})
        params
        urlparam
        {:acceptEncoding acceptEncoding}
        )
      ))
  )

(defroutes app-routes
           (GET  "/content/:urlparam" {params :params urlparam :urlparam headers :headers} (build-response params urlparam headers))
           (route/resources "/")
           (route/not-found "Page not found"))

;(def app
;  (wrap-defaults app-routes site-defaults))

(def app
  (-> app-routes
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)
      (wrap-defaults api-defaults)))
