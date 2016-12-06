(ns clj-gephi.io.export
  (require [clojure.java.io :as io]
           [clj-gephi.project :as p])
  (import [org.openide.util Lookup])
  (import [org.gephi.io.exporter.api ExportController])
  (import [org.gephi.io.exporter.spi GraphExporter])
  )

(def ec
  (.lookup (Lookup/getDefault)  ExportController))

;; export only visible graph:
;; https://github.com/gephi/gephi-toolkit-demos/blob/master/src/main/java/org/gephi/toolkit/demos/ImportExport.java#L90

(defn exporter
  [exporter-name]
  (.getExporter ec exporter-name))

(defn graph-exporter
  [exporter-name wp visible?]
  (let [ge (->> (exporter exporter-name)
                (cast GraphExporter))]
    (.setExportVisible ge visible?)
    (.setWorkspace ge wp)
    ge))

(defn gexf-exporter
  [wp visible?]
  (graph-exporter "gexf" wp visible?))

(defn export-graph-file!
  ([file-name]
   (.exportFile ec (io/file file-name)))
  ([file-name wp]
   (let [cwp (p/current-workspace)]
     (p/open-workspace! wp)
     (export-graph-file! file-name)
     (p/open-workspace! cwp)))
  ([file-name exporter-name wp visible?]
   (->> (graph-exporter exporter-name wp visible?)
        (.exportFile ec (io/file file-name)))))
