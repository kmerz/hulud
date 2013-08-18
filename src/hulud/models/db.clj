(ns hulud.models.db
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [hulud.models.schema :as schema]))

(defdb db schema/db-spec)

(defentity posts)

(defn save-post
  [title content]
  (insert posts
          (values {:title title
                   :content content
                   :timestamp (new java.util.Date)})))

;(defn update-user [id first-name last-name email]
;  (update users
;  (set-fields {:first_name first-name
;               :last_name last-name
;               :email email})
;  (where {:id id})))

(defn get-posts
  []
  (select posts))
