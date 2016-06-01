(ns clj-gephi.preview
  (import [org.gephi.preview.api PreviewController])
  (import [org.gephi.preview.api PreviewModel])
  (import [org.gephi.preview.api PreviewProperty])
  (import [org.gephi.preview.types EdgeColor])
  (import [org.openide.util Lookup])
  (import [java.awt Color])
  )

(def pc
  (.lookup (Lookup/getDefault) PreviewController))

(defn preview-model
  "PreviewController -> PreviewModel"
  []
  (.getModel pc))

(defn set-property!
  [pm prop value]
  (-> (.getProperties pm)
      (.putValue prop value))
  pm)

(defn show-node-labels!
  [pm show?]
  (set-property! pm PreviewProperty/SHOW_NODE_LABELS show?)
  pm)

(defn node-font-label
  [pm]
  (-> (.getProperties pm)
      (.getFontValue PreviewProperty/NODE_LABEL_FONT)))

(defn node-font-label!
  [pm font]
  (set-property! pm PreviewProperty/NODE_LABEL_FONT font)
  pm)

(defn edge-color!
  [pm color]
  (set-property! pm PreviewProperty/EDGE_COLOR (EdgeColor. color))
  pm)

(defn edge-thickness!
  [pm thickness]
  (set-property! pm PreviewProperty/EDGE_THICKNESS (float thickness))
  pm)

