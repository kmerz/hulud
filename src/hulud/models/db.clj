(ns hulud.models.db
  (:use korma.core
        [korma.db :only (defdb)])
  (:use markdown.core)
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
(defn update-post
  [title content id]
  ; should be update
  (insert posts
          (values {:title title
                   :content content
                   :timestamp (new java.util.Date)})))

(defn clob-to-string [clob]
  (with-open [rdr (java.io.BufferedReader. (.getCharacterStream clob))]
    (apply str (line-seq rdr))))

(defn get-posts
  []
  (select posts (order :id :DESC)))

(defn get-posts-for-html
  []
  (map #(update-in % [:content] md-to-html-string) (get-posts)))

(defn get-post-by-id
  [id]
  (select posts (where {:id id})))

(defn get-post-for-html
  [id]
  (let [x (get-post-by-id id)]
  (first (map #(update-in % [:content] md-to-html-string) x))))

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
