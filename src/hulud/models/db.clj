(ns hulud.models.db
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [hulud.models.schema :as schema]
            [crypto.password.bcrypt :as pw]))

(defdb db schema/db-spec)

(defentity posts)

(defn save-post
  [title content]
  (insert posts
          (values {:title title
                   :content content
                   :timestamp (new java.util.Date)})))

(defn clob-to-string [clob]
  (with-open [rdr (java.io.BufferedReader. (.getCharacterStream clob))]
    (apply str (line-seq rdr))))

(defn get-posts
  []
  (select posts (order :timestamp :DESC)))

(defentity users)

(defn get-user-by-name
  [name]
  (first (select users (where {:name name}))))

(defn save-user
  [name password email]
  (if-not (= name (:name (get-user-by-name name)))
    (insert users
      (values {:name name
               :email email
               :password (pw/encrypt password)}))))

(defn correct-password-for-user? 
  [name password]
  (let [user (get-user-by-name name)]
    (if user
      (pw/check password (:password user))
      false)))
