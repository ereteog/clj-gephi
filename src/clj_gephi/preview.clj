(ns clj-gephi.preview
  (import [org.gephi.preview.api PreviewController])
  (import [org.openide.util Lookup])
  )

(defn pc []
  (.lookup (Lookup/getDefault) PreviewController))

(defn preview-model
  "PreviewController -> PreviewModel"
  []
  (.getModel pc))
