(ns rest-demo.core
  (:require [toucan.db :as db]
            [toucan.models :as models]
            [ring.adapter.jetty :refer [run-jetty]]
            [compojure.api.sweet :refer [api routes]]
            [rest-demo.user :refer [user-routes]])
  (:gen-class))
;; => nil

(def db-spec
  {:dbtype "postgres"
   :dbname "rest-demo"
   :user "postgres"
   :password "postgres"})
;; => #'rest-demo.core/db-spec
;; => #'rest-demo.core/db-spec


(def swagger-config
  {:ui "/swagger"
   :spec "/swagger.json"
   :options {:ui {:validatorUrl nil}
             :data {:info {:version "1.0.0", :title "Restful CRUD API"}}}})
;; => #'rest-demo.core/swagger-config

(def app (api {:swagger swagger-config} (apply routes user-routes)))
;; => #'rest-demo.core/app


(defn -main
  [& args]
  (db/set-default-db-connection! db-spec)
  (models/set-root-namespace! 'rest-demo.models)
  (run-jetty app {:port 3000}))
;; => #'rest-demo.core/-main
