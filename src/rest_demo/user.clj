(ns rest-demo.user
  (:require [schema.core :as s]
            [rest-demo.string-util :as str]
            [rest-demo.models.user :refer [User]]
            [buddy.hashers :as hashers]
            [clojure.set :refer [rename-keys]]
            [toucan.db :as db]
            [compojure.api.sweet :refer [POST GET PUT DELETE]]
            [ring.util.http-response :refer [created ok not-found]]))
;; => nil

(defn valid-username? [name]
  (str/non-blank-with-max-length? 50 name))
;; => #'rest-demo.user/valid-username?

(defn valid-password? [password]
  (str/length-in-range? 5 50 password))
;; => #'rest-demo.user/valid-password?

(s/defschema UserRequestSchema
  {:username (s/constrained s/Str valid-username?)
   :password (s/constrained s/Str valid-password?)
   :email (s/constrained s/Str str/email?)})
;; => #'rest-demo.user/UserRequestSchema


(defn id->created [id]
  (created (str "/users/" id) {:id id}))
;; => #'rest-demo.user/id->created

(defn canonicalize-user-req [user-req]
  (-> (update user-req :password hashers/derive)
      (rename-keys {:password :password_hash})))
;; => #'rest-demo.user/canonicalize-user-req

(defn create-user-handler [create-user-req]
  (->> (canonicalize-user-req create-user-req)
       (db/insert! User)
       :id
       id->created))
;; => #'rest-demo.user/create-user-handler

(defn user->response [user]
  (if user (ok user) (not-found)))
;; => #'rest-demo.user/user->response

(defn get-user-handler [user-id]
  (-> (User user-id)
      (dissoc :password_hash)
      (user->response)))
;; => #'rest-demo.user/get-user-handler

(defn get-users-handler []
  (->> (db/select User)
       (map #(dissoc % :password_hash))
       ok))
;; => #'rest-demo.user/get-users-handler

(defn update-user-handler [id update-user-req]
  (db/update! User id (canonicalize-user-req update-user-req))
  (ok))
;; => #'rest-demo.user/update-user-handler

(defn delete-user-handler [user-id]
  (db/delete! User :id user-id)
  (ok))

(def user-routes
  [(POST "/users" []
     :body [create-user-req UserRequestSchema]
     (create-user-handler create-user-req))
   (GET "/users/:id" []
     :path-params [id :- s/Int]
     (get-user-handler id))
   (GET "/users" []
     (get-users-handler))
   (PUT "/users/:id" []
     :path-params [id :- s/Int]
     :body [update-user-req UserRequestSchema]
     (update-user-handler id update-user-req))])
;; => #'rest-demo.user/user-routes
