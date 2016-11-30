(ns clj-gephi.io.export
  (require [clojure.java.io :as io]
           [clj-gephi.project :as p])
  (import [org.openide.util Lookup])
  (import [org.gephi.io.exporter.api ExportController])
  )

(def ec
  (.lookup (Lookup/getDefault)  ExportController))

(defn export-graph-file!
  ([file-name]
   (.exportFile ec (io/file file-name)))
  ([wp file-name]
   (let [cwp (p/current-workspace)]
     (p/open-workspace! wp)
     (export-graph-file! file-name)
     (p/open-workspace! cwp))))
