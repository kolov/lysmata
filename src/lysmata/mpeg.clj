(ns lysmata.mpeg
  (:require
    [org.httpkit.server :refer :all]
    [lysmata.stream :refer :all]
    [ring.util.response :as r]
    [compojure.core :refer (GET defroutes)])
  )

(defn read-bin-file [path]
  (let [f (java.io.File. path)
        n (.length f)
        buf (byte-array n)]
    (.read (java.io.FileInputStream. path) buf 0 n)
    buf))


(defn send-file [ch path]
  (let [bytes (read-bin-file path)]

    (send! ch "--lysmata\r\nContent-type: image/jpeg\r\n" false)
    (send! ch "Content-Transfer-Encoding : base64\r\n" false)
    (send! ch (str "Content-length: " (count bytes) "\r\n\r\n" false))
    (send! ch bytes false)
    ))

(defn stream1 [ch loc]

  (future
    (println "step0")
    (send! ch "Content-type: multipart/x-mixed-replace;boundary=lysmata\r\n\r\n" false)


    (println "step1")
    (send-file ch loc)
    (println "step11")
    (Thread/sleep 1000)

    (println "step2")
    (send-file ch loc)
    (println "step21")
    )

  )

(defn send-file! [ch loc]
  (let [bytes (read-bin-file loc)]

    (send! ch "Connection: close\r\n" false)
    (send! ch "Content-Type: image/jpeg\r\n" false)
    (send! ch (str "Content-Length: " (count bytes) "\r\n") false)
    (send! ch "\r\n" false)
    (send! ch {:body (java.io.ByteArrayInputStream. bytes)} false)
    (send! ch "\r\n--lysmata\r\n" false)
    ))

(defn stream
  [ch loc]

  (future
    (println "step0")

    (send! ch {:headers {"Content-type"  "multipart/x-mixed-replace;boundary=lysmata"
                         "Cache-Control" "no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0"
                         "Pragma"        "no-cache"
                         "Connection"    "close"
                         }
               :status  200}

           false)
    (send! ch "--lysmata\r\n" false)

    (doseq [i (range 10)]
      (do
        (println "Doing loop " i)
        (send-file! ch loc)
        (Thread/sleep 500)
        (send-file! ch "/Users/assen/assenl.jpg")
        (Thread/sleep 500)
        (send-file! ch "/Users/assen/assenr.jpg")
        (Thread/sleep 500)
        )
                          )
    )
  )