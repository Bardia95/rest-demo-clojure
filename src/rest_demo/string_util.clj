(ns rest-demo.string-util
  (:require [clojure.string :as str]))
;; => nil

(def non-blank? (complement str/blank?))
;; => #'rest-demo.string-util/non-blank?

(defn max-length? [length text]
  (<= (count text) length))
;; => #'rest-demo.string-util/max-length?

(defn non-blank-with-max-length? [length text]
  (and (non-blank? text) (max-length? length text)))
;; => #'rest-demo.string-util/non-blank-with-max-length?

(defn min-length? [length text]
  (>= (count text) length))
;; => #'rest-demo.string-util/min-length?

(defn length-in-range? [min-length max-length text]
  (and (min-length? min-length text) (max-length? max-length text)))
;; => #'rest-demo.string-util/length-in-range?

(def email-regex
  #"(?i)[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")
;; => #'rest-demo.string-util/email-regex

(defn email? [email]
  (boolean (and (string? email) (re-matches email-regex email))))
;; => #'rest-demo.string-util/email?
