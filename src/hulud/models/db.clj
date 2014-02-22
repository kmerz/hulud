(ns hulud.models.db
  (:use korma.core
        [korma.db :only (defdb)])
  (:use markdown.core)
  (:require [hulud.models.schema :as schema]
            [crypto.password.bcrypt :as pw]))

(defdb db schema/db-spec)

(defn clob-to-string [clob]
  "Turn a Derby 10.6.1.0 EmbedClob into a String"
  (if (not (nil? clob))
    (with-open [rdr (java.io.BufferedReader. (.getCharacterStream clob))]
      (clojure.string/join "\n" (line-seq rdr)))))

(defentity posts
   (pk :id)
   (entity-fields :id :timestamp :title :content :public)
   (database db)
   (transform #(assoc % :content (clob-to-string (:content %)))))

(defn save-post
  [title content]
  (insert posts
          (values {:title title
                   :content content
                   :timestamp (new java.util.Date)})))

(defn delete-post
  [id]
  (delete posts
    (where {:id id})))

(defn update-post
  [title content id]
  (update posts
    (set-fields
      {:title title
       :content content
       :timestamp (new java.util.Date)})
      (where {:id id})))

(defn get-posts
  []
  (select posts (order :id :DESC)))

(defn get-posts-for-html
  []
  (map #(update-in % [:content] md-to-html-string) (get-posts)))

(defn get-post-by-id
  [id]
  (first (select posts (where {:id id}))))

(defn get-post-for-html
  [id]
  (update-in (get-post-by-id id) [:content] md-to-html-string))

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
