(ns lysmata.stream
  (:gen-class
  :extends java.io.InputStream
  :state state
  :init init
  :constructors {[String String] []}
  :exposes-methods { read read}
  :main false))

(defn -init [path contentType]
  [[] (ref {:path path :content-type contentType})])

(defn -read-void [this]
  (println "read called")
  88)

