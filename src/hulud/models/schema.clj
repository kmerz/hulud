(ns hulud.models.schema
  (:require [clojure.java.jdbc :as sql]
            [noir.io :as io]))

(def db-store "site.db")

(def db-spec {:classname "org.h2.Driver"
              :subprotocol "h2"
              :subname (str (io/resource-path) db-store)
              :user "sa"
              :password ""
              :naming {:keys clojure.string/lower-case
                       :fields clojure.string/upper-case}
              :make-pool? true
              :excess-timeout 99
              :idle-timeout 88
              :minimum-pool-size 5
              :maximum-pool-size 20
              :test-connection-on-checkout true
              :test-connection-query "SELECT 1"})

(defn initialized?
  "checks to see if the database schema is present"
  []
  (.exists (new java.io.File (str (io/resource-path) db-store ".h2.db"))))

(defn create-posts-table
  []
  (sql/with-connection db-spec
     (sql/create-table
        :posts
        [:id "INTEGER PRIMARY KEY AUTO_INCREMENT"]
        [:title "varchar(100)"]
        [:content "text"] ; This should be :text.
        [:timestamp :time]
        [:public :boolean])
     (sql/do-commands
       "CREATE INDEX timestamp_index ON posts (timestamp)")))

(defn create-users-table
  []
  (sql/with-connection db-spec
     (sql/create-table
        :users
        [:id "INTEGER PRIMARY KEY AUTO_INCREMENT"]
        [:name "varchar(30)"]
        [:email "varchar(50)"]
        [:password "varchar(100)"]
        [:is_active :boolean]
        [:last_login :time]))) 

(defn create-tables
  "creates the database tables used by the application"
  []
  (create-posts-table)
  (create-users-table))
