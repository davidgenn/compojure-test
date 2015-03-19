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
                          sometimes)]
            [clj-jgit "0.8.3"]))
(def my-repo
  (clj-jgit.porcelain/git-clone "https://github.com/clj-jgit/clj-jgit.git" "local-folder/clj-jgit"))
(def my-log (clj-jgit.porcelain/git-log my-repo "v0.0.1" "v0.0.3"))
(defn print-log-entry [rev-commit] (println (.getShortMessage rev-commit)))
(defn print-each-log (map print-log-entry my-log))


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