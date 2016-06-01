(ns clj-gephi.io.export
  (require [clojure.java.io :as io])
  (import [org.openide.util Lookup])
  (import [org.gephi.io.exporter.api ExportController])
  )

(def ec
  (.lookup (Lookup/getDefault)  ExportController))

(defn export-graph-file!
  [file-name]
  (.exportFile ec (io/file file-name)))
